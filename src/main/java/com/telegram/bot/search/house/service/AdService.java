package com.telegram.bot.search.house.service;

import com.telegram.bot.search.house.entity.Ad;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdService {
    Ad getAd(Long userId, Long adId);

    List<Ad> getAllAd(Long userId);

    Ad saveAd(Ad ad, MultipartFile file);

    void removeAd(Long adId);
}
