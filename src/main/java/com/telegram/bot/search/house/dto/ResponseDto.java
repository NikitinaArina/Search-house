package com.telegram.bot.search.house.dto;

import com.telegram.bot.search.house.dto.enums.OwnerDto;
import com.telegram.bot.search.house.dto.enums.RenovationDto;
import com.telegram.bot.search.house.dto.enums.RoomDto;
import com.telegram.bot.search.house.entity.Ad;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseDto {
    private String url,
            title,
            location;
    private Long price,
            floor,
            year,
            square;
    private RoomDto rooms;
    private OwnerDto owner;
    private RenovationDto renovationType;
    private LocalDateTime dateCreated;
    private boolean animal;
    private boolean kids;

    public Ad getAd() {
        return new Ad()
                .setUrl(url)
                .setLocation(location)
                .setTitle(title)
                .setPrice(price)
                .setRooms(rooms)
                .setFloor(floor)
                .setYear(year)
                .setSquare(square)
                .setOwner(owner)
                .setRenovationType(renovationType)
                .setDateCreated(dateCreated)
                .setAnimal(animal)
                .setKids(kids);
    }
}
