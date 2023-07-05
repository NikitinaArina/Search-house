package com.telegram.bot.search.house.controller;

import com.telegram.bot.search.house.TelegramConnect;
import com.telegram.bot.search.house.dto.MessageDto;
import com.telegram.bot.search.house.entity.User;
import com.telegram.bot.search.house.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final TelegramConnect telegramConnect;
    private final UserService userService;

    @Autowired
    public AdminController(TelegramConnect telegramConnect, UserService userService) {
        this.telegramConnect = telegramConnect;
        this.userService = userService;
    }

    @PostMapping("/send/message")
    public void sendMessage(@RequestBody MessageDto messageDto) {
        List<User> users = userService.getUsers();

        users.parallelStream()
                        .forEach(f -> {
                            SendMessage sendMessage = new SendMessage();
                            sendMessage.setChatId(f.getChatId());
                            sendMessage.setText(messageDto.getMessage());
                            telegramConnect.sendMessageAsync(sendMessage);
                        });
    }
}
