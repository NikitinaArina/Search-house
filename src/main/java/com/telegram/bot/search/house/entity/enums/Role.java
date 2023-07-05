package com.telegram.bot.search.house.entity.enums;

import jakarta.persistence.Column;

public enum Role {
    USER("Пользователь"),
    LANDLORD("Арендодатель"),
    ADMIN("Администратор");

    public final String role;

    Role(String role) {
        this.role = role;
    }
}
