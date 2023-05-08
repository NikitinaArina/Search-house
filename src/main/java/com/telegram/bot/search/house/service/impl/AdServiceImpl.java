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
    public Ad getAd(Long userId) {
        return adRepository.getAdByUsr_Id(userId);
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

        return adRepository.save(ad);
    }

    @Override
    public void removeAd(Ad ad) {
        adRepository.deleteById(ad.getId());
    }
}
