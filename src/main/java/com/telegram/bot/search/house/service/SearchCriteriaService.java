package com.telegram.bot.search.house.service;

import com.telegram.bot.search.house.dto.SearchCriteriaDto;
import com.telegram.bot.search.house.entity.SearchCriteria;

import java.util.List;

public interface SearchCriteriaService {
    SearchCriteria save(SearchCriteriaDto searchCriteriaDto);

    SearchCriteria update(SearchCriteria searchCriteria);

    SearchCriteria getSearchCriteria(Long criteriaId, Long userId);

    List<SearchCriteria> getAllSearchCriteria(Long userId);

    List<SearchCriteria> getAllSearchCriteriaByUserIdAndActive(Long userId, boolean active);

    void deleteSearchCriteria(Long criteriaId);
}
