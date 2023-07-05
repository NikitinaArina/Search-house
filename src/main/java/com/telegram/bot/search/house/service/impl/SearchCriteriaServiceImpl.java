package com.telegram.bot.search.house.service.impl;

import com.telegram.bot.search.house.dto.SearchCriteriaDto;
import com.telegram.bot.search.house.entity.SearchCriteria;
import com.telegram.bot.search.house.entity.User;
import com.telegram.bot.search.house.repository.LocationRepository;
import com.telegram.bot.search.house.repository.SearchCriteriaRepository;
import com.telegram.bot.search.house.repository.UserRepository;
import com.telegram.bot.search.house.service.SearchCriteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchCriteriaServiceImpl implements SearchCriteriaService {
    private final SearchCriteriaRepository searchCriteriaRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public SearchCriteriaServiceImpl(SearchCriteriaRepository searchCriteriaRepository,
                                     UserRepository userRepository, LocationRepository locationRepository) {
        this.searchCriteriaRepository = searchCriteriaRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
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
    public SearchCriteria update(SearchCriteria searchCriteria) {
        return searchCriteriaRepository.save(searchCriteria);
    }

    @Override
    public SearchCriteria getSearchCriteria(Long criteriaId, Long userId) {
        SearchCriteria criteria = searchCriteriaRepository.findSearchCriteriaByIdAndUserId(criteriaId, userId);
        return criteria == null ? new SearchCriteria() : criteria;
    }

    @Override
    public List<SearchCriteria> getAllSearchCriteria(Long userId) {
        return searchCriteriaRepository.findAllByUserId(userId);
    }

    @Override
    public List<SearchCriteria> getAllSearchCriteriaByUserIdAndActive(Long userId, boolean active) {
        return searchCriteriaRepository.findAllByIsActiveAndUserId(active, userId);
    }

    @Override
    public void deleteSearchCriteria(Long criteriaId) {
        searchCriteriaRepository.deleteById(criteriaId);
    }
}
