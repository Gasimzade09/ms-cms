package ru.em.cms.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(String name);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);
}
