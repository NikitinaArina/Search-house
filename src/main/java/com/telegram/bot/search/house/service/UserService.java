package com.telegram.bot.search.house.service;

import com.telegram.bot.search.house.dto.JwtResponse;
import com.telegram.bot.search.house.dto.request.LoginRequest;
import com.telegram.bot.search.house.entity.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    User create(User user);

    User update(User user);

    User getUserByChatId(String chatId);

    ResponseEntity<JwtResponse> login(LoginRequest loginRequest, String decryptedAccessToken, String decryptedRefreshToken);

    ResponseEntity<JwtResponse> refresh(String decryptedAccessToken, String decryptedRefreshToken);
}
