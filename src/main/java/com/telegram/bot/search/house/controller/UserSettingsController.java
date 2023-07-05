package com.telegram.bot.search.house.controller;

import com.telegram.bot.search.house.entity.UserSettings;
import com.telegram.bot.search.house.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/settings")
public class SettingsController {
    private final UserSettingsService userSettingsService;

    @Autowired
    public SettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @PutMapping("/update")
    public UserSettings updateUserSettings(@RequestBody UserSettings userSettings) {
        return userSettingsService.saveSettings(userSettings);
    }
}
