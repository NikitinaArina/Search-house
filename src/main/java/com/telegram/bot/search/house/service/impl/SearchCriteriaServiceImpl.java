package com.telegram.bot.search.house.service.impl;

import com.telegram.bot.search.house.dto.SearchCriteriaDto;
import com.telegram.bot.search.house.entity.SearchCriteria;
import com.telegram.bot.search.house.entity.User;
import com.telegram.bot.search.house.repository.SearchCriteriaRepository;
import com.telegram.bot.search.house.repository.UserRepository;
import com.telegram.bot.search.house.service.SearchCriteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchCriteriaServiceImpl implements SearchCriteriaService {
    private final SearchCriteriaRepository searchCriteriaRepository;
    private final UserRepository userRepository;

    @Autowired
    public SearchCriteriaServiceImpl(SearchCriteriaRepository searchCriteriaRepository,
                                     UserRepository userRepository) {
        this.searchCriteriaRepository = searchCriteriaRepository;
        this.userRepository = userRepository;
    }

    @Override
    public SearchCriteria save(SearchCriteriaDto searchCriteriaDto) {
        User user = userRepository.findUserById(searchCriteriaDto.getUserId());

        SearchCriteria searchCriteria;

        if (searchCriteriaDto.getId() != null) {
            searchCriteria = searchCriteriaDto.getSearchCriteria()
                    .setId(searchCriteriaDto.getId())
                    .setUser(user);
        } else {
            searchCriteria = searchCriteriaDto.getSearchCriteria()
                    .setUser(user);
        }

        return searchCriteriaRepository.save(searchCriteria);
    }

    @Override
    public SearchCriteria getSearchCriteria(Long userId) {
        SearchCriteria criteria = searchCriteriaRepository.findSearchCriteriaByUser_Id(userId);
        return criteria == null ? new SearchCriteria() : criteria;
    }
}
