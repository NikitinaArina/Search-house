package com.telegram.bot.search.house.dto;

import com.telegram.bot.search.house.dto.enums.OwnerDto;
import com.telegram.bot.search.house.dto.enums.RenovationDto;
import com.telegram.bot.search.house.entity.Ad;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseDto {
    private String url,
            title,
            location;
    private Long price,
            rooms,
            floor,
            year,
            square;
    private OwnerDto owner;
    private RenovationDto renovationType;
    private LocalDateTime dateCreated;
    private boolean animal;
    private boolean kids;

    public Ad getAd() {
        Ad ad = new Ad();
        ad.setUrl(url);
        ad.setLocation(location);
        ad.setTitle(title);
        ad.setPrice(price);
        ad.setRooms(rooms);
        ad.setFloor(floor);
        ad.setYear(year);
        ad.setSquare(square);
        ad.setOwner(owner);
        ad.setRenovationType(renovationType);
        ad.setDateCreated(dateCreated);
        ad.setAnimal(animal);
        ad.setKids(kids);
        return ad;
    }
}
