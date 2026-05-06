package com.nishtha.PrivateRepoReadMeGen.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    @GetMapping("/user")
    public Object getUser(@AuthenticationPrincipal OAuth2User user) {

        if (user == null) {
            return Map.of("loggedIn", false);
        }

        Map<String, Object> attributes = user.getAttributes();

        System.out.println(user.getAttributes());

        return Map.of(
                "loggedIn", true,
                "login", attributes.getOrDefault("login", "unknown"),
                "name", attributes.getOrDefault("name", "unknown"),
                "avatar", attributes.getOrDefault("avatar_url", "")
        );
    }
}