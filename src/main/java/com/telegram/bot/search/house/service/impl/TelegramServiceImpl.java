package com.telegram.bot.search.house.service.impl;

import com.telegram.bot.search.house.TelegramConnect;
import com.telegram.bot.search.house.controller.AuthController;
import com.telegram.bot.search.house.dto.enums.RenovationDto;
import com.telegram.bot.search.house.dto.request.SignupRequest;
import com.telegram.bot.search.house.entity.Location;
import com.telegram.bot.search.house.entity.SearchCriteria;
import com.telegram.bot.search.house.entity.User;
import com.telegram.bot.search.house.entity.enums.Role;
import com.telegram.bot.search.house.service.ApartmentFinderService;
import com.telegram.bot.search.house.service.SearchCriteriaService;
import com.telegram.bot.search.house.service.TelegramService;
import com.telegram.bot.search.house.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.stream.Collectors;

import static com.telegram.bot.search.house.constants.Constants.*;

@Service
public class TelegramServiceImpl implements TelegramService {
    private final UserService userService;
    private final SearchCriteriaService searchCriteriaService;
    private final AuthController authController;
    private final ApartmentFinderService apartmentFinderService;

    @Autowired
    public TelegramServiceImpl(UserService userService, SearchCriteriaService searchCriteriaService, AuthController authController, ApartmentFinderService apartmentFinderService) {
        this.userService = userService;
        this.searchCriteriaService = searchCriteriaService;
        this.authController = authController;
        this.apartmentFinderService = apartmentFinderService;
    }

    public InlineKeyboardMarkup sendStartInlineKeyboard(String chatId) {
        User user = userService.getUserByChatId(chatId);
        Random r = new Random();
        char c = (char) (r.nextInt(26) + 'a');

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        List<InlineKeyboardButton> row6 = new ArrayList<>();

        InlineKeyboardButton criteria = new InlineKeyboardButton("Настроить критерии");
        criteria.setUrl(String.format("http://127.0.0.1:8080/search-criteria/save?user_id=%s", user.getId() + "__" + c +
                Base64.getEncoder().encodeToString(user.getChatId().toString().getBytes()) + "--" + c
                + Base64.getEncoder().encodeToString(user.getUsername().getBytes())));

        InlineKeyboardButton allCriterias = new InlineKeyboardButton("Все критерии");
        allCriterias.setUrl(String.format("http://127.0.0.1:8080/search-criteria/%s", user.getId()));

        InlineKeyboardButton startSearch = new InlineKeyboardButton("Запустить поиск");
        startSearch.setCallbackData("Запуск");

        InlineKeyboardButton stopSearch = new InlineKeyboardButton("Остановить поиск");
        stopSearch.setCallbackData("Стоп");

        InlineKeyboardButton notif = new InlineKeyboardButton("Указать количество уведомлений");
        notif.setCallbackData("Уведомления");

        InlineKeyboardButton favAds = new InlineKeyboardButton("Избранное");
        favAds.setCallbackData("Избранное");

        row1.add(criteria);
        row1.add(allCriterias);
        row2.add(startSearch);
        row2.add(stopSearch);
        row3.add(notif);
        row4.add(favAds);

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);

        if (user.getRoles().contains(Role.LANDLORD)) {
            InlineKeyboardButton addAd = new InlineKeyboardButton("Добавить объявление");
            addAd.setUrl(String.format("http://127.0.0.1:8080/ad/save?user_id=%s", user.getId() + "__" + c +
                    Base64.getEncoder().encodeToString(user.getChatId().toString().getBytes()) + "--" + c
                    + Base64.getEncoder().encodeToString(user.getUsername().getBytes())));
            InlineKeyboardButton checkAds = new InlineKeyboardButton("Посмотреть объявления");
            checkAds.setUrl(String.format("http://127.0.0.1:8080/ads/%s", user.getId()));
            row5.add(addAd);
            row6.add(checkAds);
            keyboard.add(row5);
            keyboard.add(row6);
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup sendSearchCriterias(int start, int end, List<SearchCriteria> searchCriteriaList, boolean isStopSearch, EditMessageText message) {
        StringBuilder mainMessage = new StringBuilder();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (int i = start; i < end; i++) {
            SearchCriteria searchCriteria = searchCriteriaList.get(i);
            mainMessage.append(String.format("%s. %s\n", (i + 1), searchCriteria.getName()));
            mainMessage.append(String.format("%s\n", searchCriteria.getCity()));
            if (searchCriteria.getPrice().getTo() != null && searchCriteria.getPrice().getFrom() != null) {
                mainMessage.append(String.format("Цена от: %s до: %s\n", searchCriteria.getPrice().getFrom(), searchCriteria.getPrice().getTo()));
            } else if (searchCriteria.getPrice().getFrom() != null) {
                mainMessage.append(String.format("Цена от: %s\n", searchCriteria.getPrice().getFrom()));
            } else if (searchCriteria.getPrice().getTo() != null) {
                mainMessage.append(String.format("Цена до: %s\n", searchCriteria.getPrice().getTo()));
            }
            if (!searchCriteria.getRenovation().isEmpty()) {
                mainMessage.append(String.format("Ремонт: %s\n", searchCriteria.getRenovation().stream()
                        .map(RenovationDto::getRenovation)
                        .collect(Collectors.joining(", "))));
            }
            if (!searchCriteria.getLocation().isEmpty()) {
                mainMessage.append(String.format("Районы: %s", searchCriteria.getLocation().stream().limit(5)
                        .map(Location::getLocation)
                        .collect(Collectors.joining(", "))));
                if (searchCriteria.getLocation().size() > 5) {
                    mainMessage.append("...\n");
                } else mainMessage.append("\n");
            }
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(CRITERIA + " " + (i + 1));
            inlineKeyboardButton.setCallbackData(String.format("Button%s.%s", isStopSearch, searchCriteria.getId()));
            keyboard.add(new ArrayList<>(Collections.singleton(inlineKeyboardButton)));
        }

        message.setText(mainMessage.toString());

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public void nextSearchCriteriaPrint(List<SearchCriteria> criteriaList, int end, int start, int i2, boolean isStopSearch, EditMessageText message) {
        if (criteriaList.size() > end) {
            InlineKeyboardMarkup inlineKeyboardMarkup = sendSearchCriterias(start, end, criteriaList, isStopSearch, message);
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(NEXT);
            inlineKeyboardButton.setCallbackData(String.format("Next%s.%s", isStopSearch, i2));
            ArrayList<InlineKeyboardButton> buttons = new ArrayList<>();
            buttons.add(inlineKeyboardButton);
            if (i2 - 1 > 1) {
                InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton(BACK);
                inlineKeyboardButton1.setCallbackData(String.format("Back%s.%s", isStopSearch, (i2 - 1)));
                buttons.add(inlineKeyboardButton1);
            }

            InlineKeyboardButton returnMenu = new InlineKeyboardButton(MENU);
            returnMenu.setCallbackData(MENU);
            buttons.add(returnMenu);

            inlineKeyboardMarkup.getKeyboard().add(buttons);
            message.setReplyMarkup(inlineKeyboardMarkup);
        }
        if (criteriaList.size() < end) {
            InlineKeyboardMarkup inlineKeyboardMarkup = sendSearchCriterias(start, criteriaList.size(), criteriaList, isStopSearch, message);

            InlineKeyboardButton returnMenu = new InlineKeyboardButton(MENU);
            returnMenu.setCallbackData(MENU);

            inlineKeyboardMarkup.getKeyboard().add(new ArrayList<>(Collections.singleton(returnMenu)));

            message.setReplyMarkup(inlineKeyboardMarkup);
        }
    }

    public void backSearchCriteriaPrint(List<SearchCriteria> criteriaList, int end, int start, int i2, boolean isStopSearch, EditMessageText message) {
        if (i2 >= 1) {
            nextSearchCriteriaPrint(criteriaList, end, start, i2, isStopSearch, message);
        }
    }

    public SendMessage registerUser(@NotNull Update update, User user) {
        SendMessage message = new SendMessage();
        message.setChatId(user.getChatId());
        authController.registerUser(new SignupRequest()
                .setUsername(user.getUsername())
                .setPassword(user.getChatId() + user.getUsername())
                .setChatId(String.valueOf(user.getChatId())));
        userService.update(user);

        InlineKeyboardMarkup inlineKeyboardMarkup = sendStartInlineKeyboard(String.valueOf(update.getMessage().getChatId()));
        message.setReplyMarkup(inlineKeyboardMarkup);

        message.setText(String.format("%s, добро пожаловать! " +
                "\nПрисылаю тебе команды для взаимодействия со мной👇🏼", user.getUsername()));
        return message;
    }

    @Override
    public void manageSearch(String chatId, EditMessageText message, boolean isStopSearch) {
        User userByChatId = userService.getUserByChatId(chatId);
        List<SearchCriteria> criteriaList = searchCriteriaService.getAllSearchCriteriaByUserIdAndActive(userByChatId.getId(), isStopSearch);

        if (criteriaList.isEmpty()) {
            message.setText(isStopSearch ? "У вас еще нет активных критериев" : "У вас еще нет критериев");
            InlineKeyboardMarkup inlineKeyboardMarkup = sendStartInlineKeyboard(chatId);
            message.setReplyMarkup(inlineKeyboardMarkup);
        } else
            nextSearchCriteriaPrint(criteriaList, 5, 0, 1, isStopSearch, message);
    }

    public void selectSearchCriteria(String chatId, SendMessage message, boolean isStopSearch, long i) {
        User userByChatId = userService.getUserByChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = sendStartInlineKeyboard(message.getChatId());
        message.setReplyMarkup(inlineKeyboardMarkup);

        SearchCriteria searchCriteria = searchCriteriaService.getSearchCriteria(i, userByChatId.getId());
        if (!searchCriteria.getIsActive() && isStopSearch) {
            message.setText("Поиск уже выключен, запросите новую информацию через кнопку \"Остановить поиск\"");
        } else if (!searchCriteria.getIsActive()) {
            searchCriteria.setIsActive(true);
            searchCriteriaService.update(searchCriteria);

            message.setText("Поиск запущен!");
        } else if (!isStopSearch) {
            message.setText("Этот поиск уже запущен, запросите новую информацию через кнопку \"Запустить поиск\"");
        } else {
            searchCriteria.setIsActive(false);
            searchCriteriaService.update(searchCriteria);

            message.setText("Поиск остановлен!");
        }
    }

    public void backSearchCriteria(String chatId, EditMessageText message, boolean isStopSearch, int i) {
        User userByChatId = userService.getUserByChatId(chatId);
        List<SearchCriteria> criteriaList = searchCriteriaService.getAllSearchCriteriaByUserIdAndActive(userByChatId.getId(), isStopSearch);
        backSearchCriteriaPrint(criteriaList, i * 10, (i - 1) * 10, i + 1, isStopSearch, message);
    }

    public void nextSearchCriteria(String chatId, EditMessageText message, boolean isStopSearch, int i) {
        User userByChatId = userService.getUserByChatId(chatId);
        List<SearchCriteria> criteriaList = searchCriteriaService.getAllSearchCriteriaByUserIdAndActive(userByChatId.getId(), isStopSearch);
        nextSearchCriteriaPrint(criteriaList, (i + 1) * 10, i * 10, i + 1, isStopSearch, message);
    }

    @Override
    public void findApartmentsByCriteria(List<SearchCriteria> searchCriteriaList, TelegramConnect telegramConnect) {
        apartmentFinderService.findApartmentsByCriteria(searchCriteriaList, telegramConnect);
    }
}
