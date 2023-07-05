package com.telegram.bot.search.house.service;

import com.telegram.bot.search.house.entity.Ad;
import com.telegram.bot.search.house.entity.SearchCriteria;

import java.util.List;

public interface ApartmentFinderService {
    List<Ad> findApartmentsByCriteria(SearchCriteria searchCriteria);
}
