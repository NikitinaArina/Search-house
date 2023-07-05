package com.telegram.bot.search.house.service.scraper;

public interface ScraperService {
    void getAds();

    boolean checkAd(String url);
}
