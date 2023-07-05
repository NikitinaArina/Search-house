package com.telegram.bot.search.house.dto.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum RenovationDto {
    @JsonProperty("Косметический")
    BUDGETARY("Косметический"),
    @JsonProperty("Евроремонт")
    EURO("Евроремонт"),
    @JsonProperty("Дизайнерский")
    DESIGNER("Дизайнерский"),
    @JsonProperty("Без ремонта")
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
                .orElse(RenovationDto.GRANDMOTHER);
    }
}
