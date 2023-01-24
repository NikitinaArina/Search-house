package com.telegram.bot.search.house.dto;

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
    private RenovationDto renovationDto;
    private LocalDateTime dateCreated;
    private boolean animal;
    private boolean kids;
}
