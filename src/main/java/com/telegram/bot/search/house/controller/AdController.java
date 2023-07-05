package com.telegram.bot.search.house.controller;

import com.telegram.bot.search.house.entity.Ad;
import com.telegram.bot.search.house.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController()
@RequestMapping("/ads")
public class AdController {
    private final AdService adService;

    @Autowired
    public AdController(AdService adService) {
        this.adService = adService;
    }

    @GetMapping(path = "/{userId}/{adId}")
    public Ad getAd(@PathVariable String userId, @PathVariable String adId) {
        return adService.getAd(Long.valueOf(userId), Long.valueOf(adId));
    }

    @GetMapping(path = "/{userId}")
    public List<Ad> getAllAds(@PathVariable String userId) {
        return adService.getAllAd(Long.valueOf(userId));
    }

    @PostMapping(value = "/save", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public Ad saveAd(@RequestPart("ad") Ad ad, @RequestPart("file") MultipartFile file) {
        return adService.saveAd(ad, file);
    }

    @DeleteMapping("/delete/{adId}")
    public void deleteAd(@PathVariable Long adId) {
        adService.removeAd(adId);
    }
}
