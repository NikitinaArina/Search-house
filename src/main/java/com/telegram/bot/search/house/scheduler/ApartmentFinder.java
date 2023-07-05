package com.telegram.bot.search.house.scheduler;

import com.telegram.bot.search.house.TelegramConnect;
import com.telegram.bot.search.house.entity.SearchCriteria;
import com.telegram.bot.search.house.repository.SearchCriteriaRepository;
import com.telegram.bot.search.house.service.ApartmentFinderService;
import com.telegram.bot.search.house.service.scraper.impl.ScraperServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Component
@Log4j2
public class ApartmentFinder {
    private final ScraperServiceImpl service;
    private final ApartmentFinderService apartmentFinderService;
    private final SearchCriteriaRepository searchCriteriaService;
    private final TelegramConnect telegramConnect;

    @Autowired
    public ApartmentFinder(ScraperServiceImpl service, ApartmentFinderService apartmentFinderService, SearchCriteriaRepository searchCriteriaService, TelegramConnect telegramConnect) {
        this.service = service;
        this.apartmentFinderService = apartmentFinderService;
        this.searchCriteriaService = searchCriteriaService;
        this.telegramConnect = telegramConnect;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void get() {
        List<SearchCriteria> allActiveSearchCriteria = searchCriteriaService.findAllByIsActiveAndUserIsActive(true, true);
        service.getAds();
        apartmentFinderService.findApartmentsByCriteria(allActiveSearchCriteria, telegramConnect);
    }
}
