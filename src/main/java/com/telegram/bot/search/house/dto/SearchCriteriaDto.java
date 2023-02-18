package com.telegram.bot.search.house.dto;

import com.telegram.bot.search.house.dto.enums.OwnerDto;
import com.telegram.bot.search.house.dto.enums.RenovationDto;
import com.telegram.bot.search.house.entity.Floor;
import com.telegram.bot.search.house.entity.Room;
import com.telegram.bot.search.house.entity.Year;
import lombok.Data;

import java.util.List;

@Data
public class SearchCriteriaDto {
    private Long id;
    private Long userId;
    private List<Room> rooms;
    private Long price;
    private OwnerDto owner;
    private RenovationDto renovation;
    private String location;
    private Floor floor;
    private Year year;
    private Boolean isChildren;
    private Boolean isAnimal;
}
