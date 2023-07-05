package com.telegram.bot.search.house.controller;

import com.telegram.bot.search.house.config.security.JwtUtils;
import com.telegram.bot.search.house.dto.JwtResponse;
import com.telegram.bot.search.house.dto.request.LoginRequest;
import com.telegram.bot.search.house.dto.request.SignupRequest;
import com.telegram.bot.search.house.entity.User;
import com.telegram.bot.search.house.entity.enums.Role;
import com.telegram.bot.search.house.service.UserService;
import com.telegram.bot.search.house.util.SecurityCipher;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                          PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@CookieValue(name = "accessToken", required = false) String accessToken,
                                                        @CookieValue(name = "refreshToken", required = false) String refreshToken,
                                                        @Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String decryptedAccessToken = SecurityCipher.decrypt(accessToken);
        String decryptedRefreshToken = SecurityCipher.decrypt(refreshToken);
        return userService.login(loginRequest, decryptedAccessToken, decryptedRefreshToken);
    }

    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtResponse> refreshToken(@CookieValue(name = "accessToken", required = false) String accessToken,
                                                    @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        String decryptedAccessToken = SecurityCipher.decrypt(accessToken);
        String decryptedRefreshToken = SecurityCipher.decrypt(refreshToken);
        return userService.refresh(decryptedAccessToken, decryptedRefreshToken);
    }

    @PostMapping("/signup")
    public User registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        User user = new User()
                .setUsername(signUpRequest.getUsername())
                .setPassword(encoder.encode(signUpRequest.getPassword()))
                .setChatId(Long.parseLong(signUpRequest.getChatId()));

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        user.setRoles(roles);
        userService.create(user);

        return user;
    }

    @GetMapping("/info/{userId}")
    public User getUserInfo(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }
}
