package com.telegram.bot.search.house.controller;

import com.telegram.bot.search.house.dto.SearchCriteriaDto;
import com.telegram.bot.search.house.entity.SearchCriteria;
import com.telegram.bot.search.house.service.SearchCriteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/criteria")
public class SearchCriteriaController {
    private final SearchCriteriaService searchCriteriaService;

    @Autowired
    public SearchCriteriaController(SearchCriteriaService searchCriteriaService) {
        this.searchCriteriaService = searchCriteriaService;
    }

    @GetMapping(path = "/{userId}")
    public SearchCriteria getSearchCriteria(@PathVariable String userId) {
        return searchCriteriaService.getSearchCriteria(Long.valueOf(userId));
    }

    @PostMapping("/save")
    public SearchCriteria saveSearchCriteria(@RequestBody SearchCriteriaDto searchCriteriaDto) {
        return searchCriteriaService.save(searchCriteriaDto);
    }
}
