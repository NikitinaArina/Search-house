package com.telegram.bot.search.house.dto.enums;

import java.util.Arrays;

public enum RenovationDto {
    UNKNOWN("Неизвестно"),
    BUDGETARY("Косметический"),
    EURO("Евроремонт"),
    DESIGNER("Дизайнерский"),
    GRANDMOTHER("Без ремонта");

    private String renovation;

    RenovationDto(String renovation) {
        this.renovation = renovation;
    }

    public String getRenovation() {
        return renovation;
    }

    public static RenovationDto getByRenovation(String renovation) {
        return Arrays.stream(RenovationDto.values())
                .filter(f -> f.getRenovation().equalsIgnoreCase(renovation))
                .findFirst()
                .orElse(RenovationDto.UNKNOWN);
    }
}
