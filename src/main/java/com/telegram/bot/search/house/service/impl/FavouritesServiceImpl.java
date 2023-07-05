package com.telegram.bot.search.house.service.impl;

import com.telegram.bot.search.house.dto.FavouriteAdDto;
import com.telegram.bot.search.house.entity.Ad;
import com.telegram.bot.search.house.entity.FavouriteAd;
import com.telegram.bot.search.house.entity.Favourites;
import com.telegram.bot.search.house.repository.AdRepository;
import com.telegram.bot.search.house.repository.FavouriteAdRepository;
import com.telegram.bot.search.house.repository.FavouritesRepository;
import com.telegram.bot.search.house.repository.UserRepository;
import com.telegram.bot.search.house.service.FavouritesService;
import com.telegram.bot.search.house.service.scraper.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional
public class FavouritesServiceImpl implements FavouritesService {
    private final FavouritesRepository favouritesRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final FavouriteAdRepository favouriteAdRepository;
    private final ScraperService scraperService;

    @Autowired
    public FavouritesServiceImpl(FavouritesRepository favouritesRepository, AdRepository adRepository, UserRepository userRepository,
                                 FavouriteAdRepository favouriteAdRepository, ScraperService scraperService) {
        this.favouritesRepository = favouritesRepository;
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.favouriteAdRepository = favouriteAdRepository;
        this.scraperService = scraperService;
    }

    @Override
    public Favourites getFavourites(Long userId) {
        return favouritesRepository.findByUserId(userId);
    }

    @Override
    public FavouriteAd addToFavorites(FavouriteAdDto favouriteAdDto) {
        Favourites fav = favouritesRepository.findByUserId(favouriteAdDto.getUserId());
        Ad ad = adRepository.findById(favouriteAdDto.getAdId()).orElse(null);
        FavouriteAd favouriteAd = new FavouriteAd().setAd(ad);
        if (fav != null) {
            favouriteAd.setFavourites(fav);
        } else {
            fav = new Favourites()
                    .setUser(userRepository.findUserById(favouriteAdDto.getUserId()))
                    .setAds(Collections.singletonList(favouriteAd));
        }
        Favourites saved = favouritesRepository.save(fav);
        favouriteAd.setFavourites(saved);
        favouriteAdRepository.save(favouriteAd);
        return favouriteAd;
    }

    @Override
    public void deleteFromFavorites(FavouriteAdDto favouriteAdDto) {
        Favourites favourites = favouritesRepository.findByUserId(favouriteAdDto.getUserId());
        favouriteAdRepository.deleteByFavourites_IdAndAd_Id(favourites.getId(), favouriteAdDto.getAdId());
    }

    @Override
    public boolean deleteAd(Ad ad) {
        Ad adById = adRepository.getAdById(ad.getId());
        return scraperService.checkAd(adById.getUrl());
    }
}
