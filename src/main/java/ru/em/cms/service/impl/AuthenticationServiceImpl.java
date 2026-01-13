package ru.em.cms.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.em.cms.exception.UserAlreadyExistException;
import ru.em.cms.mapper.UserMapper;
import ru.em.cms.model.request.AuthenticationRequest;
import ru.em.cms.model.response.AuthenticationResponse;
import ru.em.cms.model.dto.UserDto;
import ru.em.cms.model.request.UserRegistrationRequest;
import ru.em.cms.repository.UserRepository;
import ru.em.cms.service.AuthenticationService;
import ru.em.cms.service.JwtService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public UserDto registration(UserRegistrationRequest request) {
        log.info("ActionLog.registration.start");
        checkUser(request.getEmail());
        String password = encoder.encode(request.getPassword());
        request.setPassword(password);
        var user = userMapper.dtoToEntity(request);
        userRepository.save(user);
        log.info("ActionLog.registration.End");
        return userMapper.entityToDto(user);
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        var authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtService.generateToken(authentication.getName());
        return new AuthenticationResponse(token);
    }

    private void checkUser(String email) {
        userRepository.findByEmail(email)
                .ifPresent(u -> {
                    var message = String.format("User with email: %s is already exist", email);
                    throw new UserAlreadyExistException("user_already_exist", message);
                });

    }
}
