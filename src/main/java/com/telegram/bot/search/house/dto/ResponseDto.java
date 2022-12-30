package com.telegram.bot.search.house.dto;

import lombok.Data;

@Data
public class ResponseDto {
    private String url;
    private String title;
    private String price;
    private String district;
    private String street;
    private String rooms;
    private String floor;
}
