package com.telegram.bot.search.house.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SignupRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String chatId;

    @NotBlank
    private String password;
}
