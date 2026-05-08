package com.nishtha.PrivateRepoReadMeGen;


import com.nishtha.PrivateRepoReadMeGen.service.JWTService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthSuccessHandler
        implements AuthenticationSuccessHandler {

    @Autowired
    private final JWTService jwtService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public OAuthSuccessHandler(JWTService jwtService, OAuth2AuthorizedClientService authorizedClientService) {
        this.jwtService = jwtService;
        this.authorizedClientService = authorizedClientService;
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

        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        "github",
                        authentication.getName()
                );

        String githubToken =
                authorizedClient.getAccessToken().getTokenValue();

        String token =
                jwtService.generateToken(login,githubToken);

        response.sendRedirect(
                "https://repo-read-me-gen.vercel.app/?token="
                        + token
        );
    }
}