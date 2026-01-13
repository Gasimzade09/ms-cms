package ru.em.cms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.em.cms.model.request.AuthenticationRequest;
import ru.em.cms.model.response.AuthenticationResponse;
import ru.em.cms.model.dto.UserDto;
import ru.em.cms.model.request.UserRegistrationRequest;
import ru.em.cms.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/registration")
    public ResponseEntity<UserDto> registration(@RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(authenticationService.registration(request));
    }
}
