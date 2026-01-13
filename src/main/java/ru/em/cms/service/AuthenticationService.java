package ru.em.cms.service;

import ru.em.cms.model.request.AuthenticationRequest;
import ru.em.cms.model.response.AuthenticationResponse;
import ru.em.cms.model.dto.UserDto;
import ru.em.cms.model.request.UserRegistrationRequest;

public interface AuthenticationService {

    UserDto registration(UserRegistrationRequest request);

    AuthenticationResponse login(AuthenticationRequest request);
}
