package com.telegram.bot.search.house.controller;

import com.telegram.bot.search.house.entity.User;
import com.telegram.bot.search.house.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/update")
    public User updateUser(@RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping("/")
    public List<User> getUsers() {
        return userService.getUsers();
    }
}
