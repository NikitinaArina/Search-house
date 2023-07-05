package com.telegram.bot.search.house.service;

import com.telegram.bot.search.house.TelegramConnect;
import com.telegram.bot.search.house.entity.SearchCriteria;
import com.telegram.bot.search.house.entity.User;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

public interface TelegramService {
    InlineKeyboardMarkup sendStartInlineKeyboard(String chatId);

    InlineKeyboardMarkup sendSearchCriterias(int start, int end, List<SearchCriteria> searchCriteriaList, boolean isStopSearch, EditMessageText message);

    void nextSearchCriteriaPrint(List<SearchCriteria> criteriaList, int end, int start, int i2, boolean isStopSearch, EditMessageText message);

    void backSearchCriteriaPrint(List<SearchCriteria> criteriaList, int end, int start, int i2, boolean isStopSearch, EditMessageText message);

    SendMessage registerUser(@NotNull Update update, User user);

    void manageSearch(String chatId, EditMessageText message, boolean isStopSearch);

    void selectSearchCriteria(String chatId, SendMessage message, boolean isStopSearch, long i);

    void backSearchCriteria(String chatId, EditMessageText message, boolean isStopSearch, int i);

    void nextSearchCriteria(String chatId, EditMessageText message, boolean isStopSearch, int i);

    void findApartmentsByCriteria(List<SearchCriteria> searchCriteriaList, TelegramConnect telegramConnect);
}
