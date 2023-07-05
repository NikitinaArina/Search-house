package com.telegram.bot.search.house.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FavouriteAdDto {
    private Long adId;

    private Long userId;
}
