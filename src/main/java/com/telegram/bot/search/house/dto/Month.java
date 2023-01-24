package com.telegram.bot.search.house.dto;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.stream;

public enum Month {
    JANUARY("Января"),
    FEBRUARY("Февраля"),
    MARCH("Марта"),
    APRIL("Апреля"),
    MAY("Мая"),
    JUNE("Июня"),
    JULY("Июля"),
    AUGUST("Августа"),
    SEPTEMBER("Сентября"),
    OCTOBER("Октября"),
    NOVEMBER("Ноября"),
    DECEMBER("Декабря");

    private final String month;

    Month(String month) {
        this.month = month;
    }

    public static boolean isMonthsContains(String str) {
        return Arrays.stream(Month.values())
                .map(Enum::name)
                .anyMatch(a -> str.contains(a.substring(0, 2)));
    }
}
