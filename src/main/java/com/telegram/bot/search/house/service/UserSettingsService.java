package com.telegram.bot.search.house.service;

import com.telegram.bot.search.house.entity.UserSettings;

public interface UserSettingsService {
    UserSettings getSettings(Long userId);
    UserSettings saveSettings(UserSettings userSettings);
}
