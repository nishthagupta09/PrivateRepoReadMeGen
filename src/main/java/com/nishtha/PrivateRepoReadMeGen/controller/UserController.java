package com.nishtha.PrivateRepoReadMeGen.controller;


import com.nishtha.PrivateRepoReadMeGen.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://repo-read-me-gen.vercel.app")
public class UserController {

    @Autowired
    private JWTService JWTService;

    @GetMapping("/user")
    public Object getUser(
            @RequestHeader("Authorization") String authHeader
    ) {

        String token = authHeader.replace("Bearer ", "");

        String username = JWTService.extractUsername(token);

        return Map.of(
                "loggedIn", true,
                "login", username
        );
    }
}