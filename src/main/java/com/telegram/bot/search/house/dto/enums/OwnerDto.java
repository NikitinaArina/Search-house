package com.telegram.bot.search.house.dto.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OwnerDto {
    @JsonProperty("Собственник")
    OWNER("Собственник"),
    @JsonProperty("Риелтор")

    AGENT("Риелтор");

    private final String owner;

    OwnerDto(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }
}
