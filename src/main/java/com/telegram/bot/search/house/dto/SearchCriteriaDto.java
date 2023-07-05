package com.telegram.bot.search.house.dto;

import com.telegram.bot.search.house.dto.enums.OwnerDto;
import com.telegram.bot.search.house.dto.enums.RenovationDto;
import com.telegram.bot.search.house.dto.enums.RoomDto;
import com.telegram.bot.search.house.entity.*;
import lombok.Data;

import java.util.List;

@Data
public class SearchCriteriaDto {
    private Long id;
    private Long userId;
    private List<RoomDto> rooms;
    private Price price;
    private List<OwnerDto> owner;
    private List<RenovationDto> renovation;
    private List<Location> location;
    private String city;
    private String name;
    private Floor floor;
    private Year year;
    private Boolean isChildren;
    private Boolean isAnimal;

    public SearchCriteria getSearchCriteria() {
        return new SearchCriteria()
                .setPrice(getPrice())
                .setFloor(getFloor())
                .setYear(getYear())
                .setLocation(getLocation())
                .setCity(getCity())
                .setName(getName())
                .setIsAnimal(getIsAnimal())
                .setIsChildren(getIsChildren())
                .setOwner(getOwner())
                .setRenovation(getRenovation())
                .setRooms(getRooms());
    }
}
