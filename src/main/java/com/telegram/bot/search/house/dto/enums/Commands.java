package com.telegram.bot.search.house.dto.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Commands {
    START("/start"),
    STOP("/stop"),
    DEFAULT("default");

    private String command;

    Commands(String command) {
        this.command = command;
    }

    public static Commands getByCommand(String command) {
        return Arrays.stream(Commands.values())
                .filter(f -> f.getCommand().equalsIgnoreCase(command))
                .findFirst()
                .orElse(Commands.DEFAULT);
    }
}
