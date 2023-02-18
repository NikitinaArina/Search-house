package com.telegram.bot.search.house.repository;

import com.telegram.bot.search.house.entity.SearchCriteria;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchCriteriaRepository extends CrudRepository<SearchCriteria, Long> {
}
