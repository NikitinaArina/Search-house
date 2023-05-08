package com.telegram.bot.search.house.repository;

import com.telegram.bot.search.house.entity.Favourites;
import org.springframework.data.repository.CrudRepository;

public interface FavouritesRepository extends CrudRepository<Favourites, Long> {
    Favourites findByUserId(Long userId);

}
