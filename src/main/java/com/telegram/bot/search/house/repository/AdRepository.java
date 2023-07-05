package com.telegram.bot.search.house.repository;

import com.telegram.bot.search.house.entity.Ad;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdRepository extends CrudRepository<Ad, Long> {
    boolean existsByDateCreatedAndTitle(LocalDateTime dateCreated, String title);

    Ad getAdByUserIdAndId(Long userId, Long adId);
    Ad getAdById(Long adId);

    List<Ad> getAllByUserId(Long userId);

    void deleteByUrl(String url);

    @Query(value = "SELECT * FROM Ad", nativeQuery = true)
    List<Ad> findAll();
}
