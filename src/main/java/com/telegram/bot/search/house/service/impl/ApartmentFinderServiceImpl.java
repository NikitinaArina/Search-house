package com.telegram.bot.search.house.service.impl;

import com.telegram.bot.search.house.TelegramConnect;
import com.telegram.bot.search.house.entity.Ad;
import com.telegram.bot.search.house.entity.SearchCriteria;
import com.telegram.bot.search.house.repository.AdRepository;
import com.telegram.bot.search.house.service.ApartmentFinderService;
import com.telegram.bot.search.house.service.SearchCriteriaService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.telegram.bot.search.house.constants.Constants.ADDELETE;
import static com.telegram.bot.search.house.constants.Constants.ADDFAV;

@Component
public class ApartmentFinderServiceImpl implements ApartmentFinderService {
    private final AdRepository adRepository;
    private final SearchCriteriaService searchCriteriaService;

    @Autowired
    public ApartmentFinderServiceImpl(AdRepository adRepository, SearchCriteriaService searchCriteriaService) {
        this.adRepository = adRepository;
        this.searchCriteriaService = searchCriteriaService;
    }

    @Override
    public void findApartmentsByCriteria(List<SearchCriteria> searchCriteria, TelegramConnect telegramConnect) {
        List<Ad> all = adRepository.findAll();
        searchCriteria.forEach(sC -> {
            List<Ad> filteredList;
            if (sC.getLastSearch() != null) {
                filteredList = all.stream()
                        .filter(f -> f.getDateCreated().isAfter(sC.getLastSearch()))
                        .collect(Collectors.toList());
            } else filteredList = all;
            filteredList = filteredList.parallelStream()
                    .filter(f -> sC.getCity().equals(f.getCity()))
                    .filter(f -> sC.getRooms().isEmpty() || sC.getRooms().contains(f.getRooms()))
                    .filter(f -> sC.getOwner().isEmpty() || sC.getOwner().contains(f.getOwner()))
                    .filter(f -> sC.getRenovation().isEmpty() || sC.getRenovation().contains(f.getRenovationType()))
                    .filter(f -> sC.getFloor() != null && (sC.getFloor().getTo() != null && sC.getFloor().getFrom() != null ?
                            sC.getFloor().getFrom().compareTo(f.getFloor().intValue()) >= 0
                                    && sC.getFloor().getTo().compareTo(f.getFloor().intValue()) <= 0 :
                            sC.getFloor().getFrom() != null ? (sC.getFloor().getFrom().compareTo(f.getFloor().intValue()) >= 0 || sC.getFloor().getFrom().compareTo(f.getFloor().intValue()) <= 0) :
                                    sC.getFloor().getTo() == null || sC.getFloor().getTo().compareTo(f.getFloor().intValue()) >= 0))
                    .filter(f -> sC.getYear() != null && (sC.getYear().getTo() != null && sC.getYear().getFrom() != null ?
                            sC.getYear().getFrom().compareTo(f.getYear().intValue()) >= 0
                                    && sC.getYear().getTo().compareTo(f.getYear().intValue()) <= 0 :
                            sC.getYear().getFrom() != null ? sC.getYear().getFrom().compareTo(f.getYear().intValue()) >= 0 :
                                    sC.getYear().getTo() == null || sC.getYear().getTo().compareTo(f.getYear().intValue()) <= 0))
                    .filter(f -> sC.getPrice() != null && (sC.getPrice().getTo() != null && sC.getPrice().getFrom() != null ?
                            sC.getPrice().getFrom().compareTo(f.getPrice().doubleValue()) >= 0
                                    && sC.getPrice().getTo().compareTo(f.getPrice().doubleValue()) <= 0 :
                            sC.getPrice().getFrom() != null ? sC.getPrice().getFrom().compareTo(f.getPrice().doubleValue()) >= 0 :
                                    sC.getPrice().getTo() == null || sC.getPrice().getTo().compareTo(f.getPrice().doubleValue()) <= 0))
                    .filter(f -> sC.getLocation().isEmpty()
                            || sC.getLocation().stream().anyMatch(lc -> f.getLocation().contains(lc.getLocation())))
                    .filter(f -> sC.getIsAnimal().equals(f.isAnimal()))
                    .filter(f -> sC.getIsChildren().equals(f.isKids()))
                    .collect(Collectors.toList());

            if (!filteredList.isEmpty()) {
                SendMessage nameCriteria = new SendMessage();
                nameCriteria.setText(String.format("Подходящие объявления для: \"%s\"", sC.getName()));
                nameCriteria.setChatId(sC.getUser().getChatId());
                telegramConnect.sendMessageAsync(nameCriteria);
                sC.setLastSearch(LocalDateTime.now());

                searchCriteriaService.update(sC);

                filteredList.parallelStream().forEach(f -> {
                    Result result = getResult(sC.getUser().getChatId(), f, "Добавить в избранное", ADDFAV);

                    InlineKeyboardButton deleteAd = new InlineKeyboardButton("Объявление неактуально");
                    deleteAd.setCallbackData(ADDELETE + f.getId());

                    result.inlineKeyboardMarkup().getKeyboard().add(Collections.singletonList(deleteAd));

                    result.message().setReplyMarkup(result.inlineKeyboardMarkup());
                    telegramConnect.sendMessageAsync(result.message());
                });
            }
        });
    }

    @NotNull
    public static Result getResult(Long chatId, Ad ad, String buttonName, String command) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String mess = String.format("%s\nГород: %s\nАдрес: %s\nЦена: %s\nРемонт: %s\nСсылка: %s",
                ad.getTitle(), ad.getCity(), ad.getLocation(),
                ad.getPrice(), ad.getRenovationType().getRenovation(), ad.getUrl() != null ? ad.getUrl() : "@" + ad.getUser().getUsername());

        InlineKeyboardButton favButton = new InlineKeyboardButton(buttonName);
        favButton.setCallbackData(command + ad.getId());

        message.setText(mess);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        ArrayList<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(new ArrayList<>(Collections.singleton(favButton)));

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return new Result(message, inlineKeyboardMarkup);
    }

    public record Result(SendMessage message, InlineKeyboardMarkup inlineKeyboardMarkup) {
    }
}
