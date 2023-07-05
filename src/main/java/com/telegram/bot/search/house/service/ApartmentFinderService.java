package com.telegram.bot.search.house.service;

import com.telegram.bot.search.house.TelegramConnect;
import com.telegram.bot.search.house.entity.SearchCriteria;

import java.util.List;

public interface ApartmentFinderService {
    void findApartmentsByCriteria(List<SearchCriteria> searchCriteria, TelegramConnect telegramConnect);
}
