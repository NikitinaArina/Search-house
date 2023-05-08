package com.telegram.bot.search.house.service.impl;

import com.telegram.bot.search.house.config.security.JwtUtils;
import com.telegram.bot.search.house.dto.JwtResponse;
import com.telegram.bot.search.house.dto.Token;
import com.telegram.bot.search.house.dto.request.LoginRequest;
import com.telegram.bot.search.house.entity.User;
import com.telegram.bot.search.house.repository.UserRepository;
import com.telegram.bot.search.house.service.UserService;
import com.telegram.bot.search.house.util.CookieUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final CookieUtil cookieUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtils jwtUtils, CookieUtil cookieUtil) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public User create(User user) {
        User userByChatId = getUserByChatId(user);

        if (userByChatId == null) {
            return userRepository.save(user);
        }
        return userByChatId;
        //throw new EntityExistsException("User with username " + userByChatId.getUsername() + " already exist");
    }

    @Override
    public User update(User user) {
        User userByChatId = getUserByChatId(user);

        if (userByChatId != null) {

            user.setId(userByChatId.getId())
                    .setPassword(userByChatId.getPassword())
                    .setRoles(userByChatId.getRoles());

            return userRepository.save(user);
        }
        throw new EntityNotFoundException("User with username " + user.getUsername() + " not found");
    }

    @Override
    public ResponseEntity<JwtResponse> login(LoginRequest loginRequest, String accessToken, String refreshToken) {
        String username = loginRequest.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username " + username));

        boolean accessTokenValid = accessToken != null && jwtUtils.validateJwtToken(accessToken);
        boolean refreshTokenValid = accessToken != null && jwtUtils.validateJwtToken(refreshToken);

        HttpHeaders responseHeaders = new HttpHeaders();
        Token newAccessToken;
        Token newRefreshToken;
        if (!accessTokenValid && !refreshTokenValid) {
            newAccessToken = jwtUtils.generateAccessToken(user.getUsername());
            newRefreshToken = jwtUtils.generateRefreshToken(user.getUsername());
            addAccessTokenCookie(responseHeaders, newAccessToken);
            addRefreshTokenCookie(responseHeaders, newRefreshToken);
        }

        if (!accessTokenValid && refreshTokenValid) {
            newAccessToken = jwtUtils.generateAccessToken(user.getUsername());
            addAccessTokenCookie(responseHeaders, newAccessToken);
        }

        if (accessTokenValid && refreshTokenValid) {
            newAccessToken = jwtUtils.generateAccessToken(user.getUsername());
            newRefreshToken = jwtUtils.generateRefreshToken(user.getUsername());
            addAccessTokenCookie(responseHeaders, newAccessToken);
            addRefreshTokenCookie(responseHeaders, newRefreshToken);
        }

        JwtResponse jwtResponse = new JwtResponse(JwtResponse.SuccessFailure.SUCCESS, "Auth successful. Tokens are created in cookie.");
        return ResponseEntity.ok().headers(responseHeaders).body(jwtResponse);

    }

    @Override
    public ResponseEntity<JwtResponse> refresh(String accessToken, String refreshToken) {
        boolean refreshTokenValid = jwtUtils.validateJwtToken(refreshToken);
        if (!refreshTokenValid) {
            throw new IllegalArgumentException("Refresh Token is invalid!");
        }

        String currentUserEmail = jwtUtils.getUserNameFromJwtToken(accessToken);

        Token newAccessToken = jwtUtils.generateAccessToken(currentUserEmail);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(newAccessToken.getTokenValue(), newAccessToken.getDuration()).toString());

        JwtResponse jwtResponse = new JwtResponse(JwtResponse.SuccessFailure.SUCCESS, "Auth successful. Tokens are created in cookie.");
        return ResponseEntity.ok().headers(responseHeaders).body(jwtResponse);
    }

    @Override
    public User getUserByChatId(String chatId) {
        return userRepository.findUserByChatId(Long.parseLong(chatId));
    }

    private User getUserByChatId(User user) {
        return userRepository.findUserByChatId(user.getChatId());
    }

    private void addAccessTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(token.getTokenValue(), token.getDuration()).toString());
    }

    private void addRefreshTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(token.getTokenValue(), token.getDuration()).toString());
    }
}
