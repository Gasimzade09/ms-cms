package ru.em.cms.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.em.cms.model.entity.UserEntity;
import ru.em.cms.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {
    private final UserRepository userRepository;

    public UserEntity get() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        return userRepository
                .findByEmail(auth.getName())
                .orElseThrow();
    }
}
