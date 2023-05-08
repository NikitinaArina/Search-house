package com.telegram.bot.search.house.service;

import com.telegram.bot.search.house.dto.SearchCriteriaDto;
import com.telegram.bot.search.house.entity.SearchCriteria;

public interface SearchCriteriaService {
    SearchCriteria save(SearchCriteriaDto searchCriteriaDto);

    SearchCriteria getSearchCriteria(Long userId);
}
