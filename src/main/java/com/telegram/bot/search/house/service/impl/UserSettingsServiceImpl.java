package com.telegram.bot.search.house.service.impl;

import com.telegram.bot.search.house.entity.UserSettings;
import com.telegram.bot.search.house.repository.UserSettingsRepository;
import com.telegram.bot.search.house.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSettingsServiceImpl implements UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;

    @Autowired
    public UserSettingsServiceImpl(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    @Override
    public UserSettings getSettings(Long userId) {
        return userSettingsRepository.findByUserId(userId);
    }

    @Override
    public UserSettings saveSettings(UserSettings userSettings) {
        UserSettings settings = getSettings(userSettings.getUser().getId());

        if (settings != null) userSettings.setId(settings.getId());

        return userSettingsRepository.save(userSettings);
    }
}
