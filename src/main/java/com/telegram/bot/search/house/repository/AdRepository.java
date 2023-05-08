package com.telegram.bot.search.house.repository;

import com.telegram.bot.search.house.entity.Ad;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AdRepository extends CrudRepository<Ad, Long> {
    boolean existsByDateCreatedAndTitle(LocalDateTime dateCreated, String title);

    Ad getAdByUsr_Id(Long userId);

}
