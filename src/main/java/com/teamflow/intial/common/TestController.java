package com.teamflow.intial.common;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test/me")
    public String me(Authentication authentication) {
        return "Authenticated as: " + authentication.getName();
    }
}