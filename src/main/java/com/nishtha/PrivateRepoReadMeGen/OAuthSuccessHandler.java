package com.nishtha.PrivateRepoReadMeGen;


import com.nishtha.PrivateRepoReadMeGen.service.JWTService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthSuccessHandler
        implements AuthenticationSuccessHandler {

    private final JWTService jwtService;

    public OAuthSuccessHandler(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User user =
                (OAuth2User) authentication.getPrincipal();

        System.out.println(user.getAttributes());

        String login = user.getAttribute("login");

        if (login == null) {

            response.sendError(500, "GitHub login missing");
            return;
        }

        String token =
                jwtService.generateToken(login);

        response.sendRedirect(
                "https://repo-read-me-gen.vercel.app/?token="
                        + token
        );
    }
}