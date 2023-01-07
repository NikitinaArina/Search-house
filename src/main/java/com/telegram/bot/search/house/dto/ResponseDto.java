package com.telegram.bot.search.house.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseDto {
    private String url;
    private String title;
    private String price;
    private String location;
    private String square;
    private String rooms;
    private String floor;
    private String year;
    private OwnerDto owner;
    private RenovationDto renovationDto;
    private LocalDateTime dateCreated;
    private boolean animal;
    private boolean kids;
}
