package com.sougata.auth_service.controller;

import com.sougata.auth_service.dto.SingleValueResponseDto;
import com.sougata.auth_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "{username}/exists")
    public SingleValueResponseDto<Boolean> existsByUsername(
            @PathVariable("username") String username
    ) {
        var exists = userService.existsByEmail(username);
        return SingleValueResponseDto.of(exists);
    }
}
