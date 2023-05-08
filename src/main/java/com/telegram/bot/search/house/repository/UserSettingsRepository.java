package com.telegram.bot.search.house.repository;

import com.telegram.bot.search.house.entity.UserSettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingsRepository extends CrudRepository<UserSettings, Long> {
    UserSettings findByUserId(Long userId);
}
