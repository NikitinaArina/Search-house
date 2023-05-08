package com.telegram.bot.search.house.controller;

import com.telegram.bot.search.house.service.scraper.impl.ScraperServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class ScraperController {
    private final ScraperServiceImpl service;

    @Autowired
    public ScraperController(ScraperServiceImpl service) {
        this.service = service;
    }

    @GetMapping("get")
    public void get() {
        service.getAds();
    }
}
