package com.telegram.bot.search.house.dto;

import com.telegram.bot.search.house.dto.enums.OwnerDto;
import com.telegram.bot.search.house.dto.enums.RenovationDto;
import com.telegram.bot.search.house.dto.enums.RoomDto;
import com.telegram.bot.search.house.entity.Floor;
import com.telegram.bot.search.house.entity.Price;
import com.telegram.bot.search.house.entity.SearchCriteria;
import com.telegram.bot.search.house.entity.Year;
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
    private String location;
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
                .setIsAnimal(getIsAnimal())
                .setIsChildren(getIsChildren())
                .setOwner(getOwner())
                .setRenovation(getRenovation())
                .setRooms(getRooms());
    }
}
