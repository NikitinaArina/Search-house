package com.telegram.bot.search.house.service;

import com.telegram.bot.search.house.entity.Ad;
import org.springframework.web.multipart.MultipartFile;

public interface AdService {
    Ad getAd(Long userId);

    Ad saveAd(Ad ad, MultipartFile file);

    void removeAd(Ad ad);
}
