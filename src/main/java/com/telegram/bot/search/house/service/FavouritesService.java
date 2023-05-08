package com.telegram.bot.search.house.service;

import com.telegram.bot.search.house.dto.FavouriteAdDto;
import com.telegram.bot.search.house.entity.FavouriteAd;
import com.telegram.bot.search.house.entity.Favourites;

public interface FavouritesService {
    Favourites getFavourites(Long userId);
    FavouriteAd addToFavorites(FavouriteAdDto favouriteAdDto);
    void deleteFromFavorites(FavouriteAdDto favouriteAdDto);
}
