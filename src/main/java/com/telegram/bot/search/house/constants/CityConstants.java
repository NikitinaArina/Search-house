package com.telegram.bot.search.house.constants;

import java.util.Arrays;
import java.util.List;

public enum CityConstants {
    SARATOV("Саратов"),
    ENGELS("Энгельс");

    private String city;

    CityConstants(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public static List<String> getCities() {
        return Arrays.stream(CityConstants.values()).map(CityConstants::getCity).toList();
    }
}
