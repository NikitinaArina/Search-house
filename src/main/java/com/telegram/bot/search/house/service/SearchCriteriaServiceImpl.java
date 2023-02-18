package com.telegram.bot.search.house.service;

import com.telegram.bot.search.house.dto.SearchCriteriaDto;
import com.telegram.bot.search.house.entity.SearchCriteria;
import com.telegram.bot.search.house.entity.User;
import com.telegram.bot.search.house.repository.AdRepository;
import com.telegram.bot.search.house.repository.SearchCriteriaRepository;
import com.telegram.bot.search.house.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchCriteriaServiceImpl implements SearchCriteriaService{
    private final AdRepository adRepository;
    private final SearchCriteriaRepository searchCriteriaRepository;
    private final UserRepository userRepository;

    @Autowired
    public SearchCriteriaServiceImpl(AdRepository adRepository,
                                     SearchCriteriaRepository searchCriteriaRepository,
                                     UserRepository userRepository) {
        this.adRepository = adRepository;
        this.searchCriteriaRepository = searchCriteriaRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void save(SearchCriteriaDto searchCriteriaDto) {
        searchCriteriaRepository.save(new SearchCriteria());
    }
}
