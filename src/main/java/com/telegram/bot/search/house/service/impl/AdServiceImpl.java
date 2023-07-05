package com.telegram.bot.search.house.service.impl;

import com.telegram.bot.search.house.entity.Ad;
import com.telegram.bot.search.house.repository.AdRepository;
import com.telegram.bot.search.house.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;
    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public AdServiceImpl(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    @Override
    public Ad getAd(Long userId, Long adId) {
        return adRepository.getAdByUserIdAndId(userId, adId);
    }

    @Override
    public List<Ad> getAllAd(Long userId) {
        return adRepository.getAllByUserId(userId);
    }

    @Override
    public Ad saveAd(Ad ad, MultipartFile file) {
        if (file != null) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            try {
                file.transferTo(new File(uploadPath + "/" + resultFilename));
            } catch (IOException | IllegalStateException e) {
                throw new RuntimeException(e);
            }

            ad.setFilename(resultFilename);
        }

        ad.setUser(ad.getUser());
        ad.setDateCreated(LocalDateTime.now());

        return adRepository.save(ad);
    }

    @Override
    public void removeAd(Long adId) {
        adRepository.deleteById(adId);
    }
}
