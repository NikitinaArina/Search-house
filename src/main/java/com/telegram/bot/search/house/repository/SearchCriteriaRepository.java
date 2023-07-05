package com.telegram.bot.search.house.repository;

import com.telegram.bot.search.house.entity.SearchCriteria;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchCriteriaRepository extends CrudRepository<SearchCriteria, Long> {
    SearchCriteria findSearchCriteriaByIdAndUserId(Long criteriaId, Long userId);

    List<SearchCriteria> findAllByUserId(Long userId);

    List<SearchCriteria> findAllByIsActiveAndUserId(boolean active, Long userId);

    List<SearchCriteria> findAllByIsActiveAndUserIsActive(boolean isActiveSc, boolean isActiveUser);
}
