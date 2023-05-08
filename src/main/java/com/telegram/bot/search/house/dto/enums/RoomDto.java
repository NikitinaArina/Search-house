package com.telegram.bot.search.house.dto.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum RoomDto {
    @JsonProperty("Все")
    ALL("Все"),
    @JsonProperty("Студия")
    STUDIO("Студия"),
    @JsonProperty("1")
    ONE("1"),
    @JsonProperty("2")
    TWO("2"),
    @JsonProperty("3")
    THREE("3"),
    @JsonProperty("4")
    FOUR("4"),
    @JsonProperty("5+")
    FIVEMORE("5+");

    private String rooms;

    RoomDto(String rooms) {
        this.rooms = rooms;
    }

    public String getRooms() {
        return rooms;
    }

    public static RoomDto getByRooms(String rooms) {
        return Arrays.stream(RoomDto.values())
                .filter(f -> f.getRooms().equalsIgnoreCase(rooms))
                .findFirst()
                .orElse(RoomDto.ALL);
    }
}
