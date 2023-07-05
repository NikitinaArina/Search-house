package com.telegram.bot.search.house.repository;

import com.telegram.bot.search.house.entity.FavouriteAd;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavouriteAdRepository extends JpaRepository<FavouriteAd, Long> {
    void deleteByFavourites_IdAndAd_Id(Long favId, Long adId);
    FavouriteAd getFavouriteAdByFavourites_IdAndAd_Id(Long favId, Long adId);
}