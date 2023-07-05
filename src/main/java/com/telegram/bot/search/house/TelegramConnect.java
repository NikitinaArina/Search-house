package com.telegram.bot.search.house;

import com.telegram.bot.search.house.dto.FavouriteAdDto;
import com.telegram.bot.search.house.entity.*;
import com.telegram.bot.search.house.entity.enums.Role;
import com.telegram.bot.search.house.service.*;
import com.telegram.bot.search.house.service.impl.ApartmentFinderServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;

import static com.telegram.bot.search.house.constants.Constants.*;
import static com.telegram.bot.search.house.dto.enums.Commands.getByCommand;

@Component
public class TelegramConnect extends TelegramLongPollingBot {

    private final String username;
    private final UserService userService;
    private final TelegramService telegramService;
    private final FavouritesService favouritesService;
    private final SearchCriteriaService searchCriteriaService;
    private final UserSettingsService userSettingsService;

    @Autowired
    public TelegramConnect(@Value("${telegram.token}") String botToken,
                           @Value("${telegram.username}") String username,
                           TelegramBotsApi telegramBotsApi, UserService userService, TelegramService telegramService,
                           FavouritesService favouritesService, SearchCriteriaService searchCriteriaService, UserSettingsService userSettingsService) throws TelegramApiException {
        super(botToken);
        this.username = username;
        this.userService = userService;
        this.telegramService = telegramService;
        this.favouritesService = favouritesService;
        this.searchCriteriaService = searchCriteriaService;
        this.userSettingsService = userSettingsService;
        telegramBotsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        boolean kicked = false;
        if (update.getMyChatMember() != null && update.getMyChatMember().getNewChatMember() != null) {
            kicked = update.getMyChatMember().getNewChatMember().getStatus().equals("kicked");
        }
        if (kicked || (update.hasMessage() && update.getMessage().hasText())) {
            org.telegram.telegrambots.meta.api.objects.User tgUser = kicked ? update.getMyChatMember().getFrom()
                    : update.getMessage().getFrom();

            String messageText = kicked ? "/stop" : update.getMessage().getText();

            User user = new User()
                    .setChatId(kicked ? update.getMyChatMember().getChat().getId() : update.getMessage().getChatId())
                    .setUsername(tgUser.getUserName())
                    .setFirstName(tgUser.getFirstName())
                    .setLastName(tgUser.getLastName())
                    .setIsActive(true);

            switch (getByCommand(messageText)) {
                case START -> {
                    SendMessage message = telegramService.registerUser(update, user);

                    sendMessage(message);
                }
                case STOP -> userService.update(user.setIsActive(false));
                case LANDLORD -> {
                    user.getRoles().add(Role.LANDLORD);
                    userService.update(user);
                }
                case MENU -> {
                    InlineKeyboardMarkup inlineKeyboardMarkup = telegramService.sendStartInlineKeyboard(String.valueOf(user.getChatId()));
                    SendMessage message = new SendMessage();
                    message.setText("Меню бота");
                    message.setChatId(user.getChatId());
                    message.setReplyMarkup(inlineKeyboardMarkup);
                    sendMessage(message);
                }
                default -> {
                    try {
                        long notif = Long.parseLong(messageText);
                        User userByChatId = userService.getUserByChatId(String.valueOf(user.getChatId()));
                        userSettingsService.saveSettings(new UserSettings().setUser(userByChatId).setMaxNotifications((int) notif));
                    } catch (NumberFormatException e) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(user.getChatId());
                        sendMessage.setText("Принимаются только целочисленные значения");
                        sendMessage(sendMessage);
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            String command = update.getCallbackQuery().getData();
            SendMessage message = new SendMessage();
            message.setChatId(update.getCallbackQuery().getFrom().getId());

            User userByChatId = userService.getUserByChatId(message.getChatId());

            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(message.getChatId());
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

            switch (getByCommand(command)) {
                case START_SEARCH -> {
                    telegramService.manageSearch(message.getChatId(), editMessageText, false);
                    sendEditMessageText(editMessageText);
                }
                case STOP_SEARCH -> {
                    telegramService.manageSearch(message.getChatId(), editMessageText, true);
                    sendEditMessageText(editMessageText);
                }
                case NOTIF -> {
                    message.setText("Отправьте максимальное количество уведомлений");
                    sendMessage(message);
                }
                case FAV -> {
                    Favourites favourites = favouritesService.getFavourites(userByChatId.getId());
                    message.setText("Ваше избранное:");
                    sendMessage(message);
                    favourites.getAds().parallelStream().forEach(f -> {
                        ApartmentFinderServiceImpl.Result result = ApartmentFinderServiceImpl
                                .getResult(userByChatId.getChatId(), f.getAd(), "Удалить из избранного", DELETEFAV);
                        SendMessage favs = result.message();
                        favs.setReplyMarkup(result.inlineKeyboardMarkup());
                        sendMessageAsync(result.message());
                    });
                }
                default -> {
                    if (command.matches("\\w+.\\d+")) {
                        boolean button = command.matches("Button\\w+.\\d+");
                        String[] split = command.split("\\.");
                        int i = Integer.parseInt(split[1]);

                        boolean isStopSearch = !split[0].contains("false");
                        if (command.matches("Next\\w+.\\d+")) {
                            telegramService.nextSearchCriteria(message.getChatId(), editMessageText, isStopSearch, i);
                        } else if (command.matches("Back\\w+.\\d+")) {
                            telegramService.backSearchCriteria(message.getChatId(), editMessageText, isStopSearch, i);
                        } else if (command.matches(ADDFAV + ".\\d+")) {
                            favouritesService.addToFavorites(new FavouriteAdDto((long) i, userByChatId.getId()));
                            message.setText("Объявление добавлено в избранное");
                        } else if (command.matches(DELETEFAV + ".\\d+")) {
                            favouritesService.deleteFromFavorites(new FavouriteAdDto((long) i, userByChatId.getId()));
                            message.setText("Объявление удалено из избранного");
                        } else if (command.matches(ADDELETE + ".\\d+")) {
                            boolean isDeleted = favouritesService.deleteAd(new Ad().setId((long) i));
                            message.setText(isDeleted ? "Спасибо за внимательность, объявление удалено из базы!" : "Объявление все еще актуально");
                        } else if (button) {
                            telegramService.selectSearchCriteria(message.getChatId(), message, isStopSearch, i);
                        }

                        if (button && !isStopSearch) {
                            sendMessage(message);
                            SearchCriteria searchCriteria = searchCriteriaService.getSearchCriteria((long) i,
                                    userService.getUserByChatId(message.getChatId()).getId());
                            telegramService.findApartmentsByCriteria(Collections.singletonList(searchCriteria), this);
                        } else if (!button || isStopSearch) {
                            sendMessage(message);
                        } else {
                            sendEditMessageText(editMessageText);
                        }
                    } else if (command.equals(MENU)) {
                        InlineKeyboardMarkup inlineKeyboardMarkup = telegramService.sendStartInlineKeyboard(message.getChatId());
                        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
                        editMessageText.setText(MENU + ":");
                        sendEditMessageText(editMessageText);
                    }
                }
            }
        }
    }

    private void sendEditMessageText(EditMessageText editMessageText) {
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    public void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageAsync(SendMessage message) {
        try {
            executeAsync(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
