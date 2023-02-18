package com.telegram.bot.search.house.dto.enums;

public enum OwnerDto {
    OWNER("Собственник"),
    AGENT("Риелтор");

    private final String owner;

    OwnerDto(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }
}
