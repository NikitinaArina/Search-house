package com.telegram.bot.search.house;

import com.telegram.bot.search.house.controller.AuthController;
import com.telegram.bot.search.house.dto.request.SignupRequest;
import com.telegram.bot.search.house.entity.User;
import com.telegram.bot.search.house.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import static com.telegram.bot.search.house.dto.enums.Commands.getByCommand;

@Component
public class TelegramConnect extends TelegramLongPollingBot {

    private final String username;
    private final UserService userService;
    private final AuthController authController;

    @Autowired
    public TelegramConnect(@Value("${telegram.token}") String botToken,
                           @Value("${telegram.username}") String username,
                           TelegramBotsApi telegramBotsApi, UserService userService, AuthController authController) throws TelegramApiException {
        super(botToken);
        this.username = username;
        this.userService = userService;
        this.authController = authController;
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
                    authController.registerUser(new SignupRequest()
                            .setUsername(user.getUsername())
                            .setPassword(user.getChatId() + user.getUsername())
                            .setChatId(String.valueOf(user.getChatId())));
                    userService.update(user);

                    SendMessage message = sendInlineKeyboard(String.valueOf(update.getMessage().getChatId()));

                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                case STOP -> userService.update(user.setIsActive(false));
            }
        } else {
            update.hasCallbackQuery();
        }
    }

    public SendMessage sendInlineKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Inline model below.");

        User user = userService.getUserByChatId(chatId);
        Random r = new Random();
        char c = (char) (r.nextInt(26) + 'a');

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> Buttons = new ArrayList<>();

        InlineKeyboardButton criteria = new InlineKeyboardButton("Настроить критерии");
        criteria.setUrl(String.format("http://127.0.0.1:8080/search-criteria/save?user_id=%s", user.getId() + "__" + c +
                Base64.getEncoder().encodeToString(user.getChatId().toString().getBytes()) + "--" + c
                + Base64.getEncoder().encodeToString(user.getUsername().getBytes())));
        criteria.setCallbackData(chatId);
        Buttons.add(criteria);

        keyboard.add(Buttons);

        inlineKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

}
