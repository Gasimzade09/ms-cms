package ru.em.cms.model.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.em.cms.model.type.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRegistrationRequest {
    String firstname;
    String lastname;
    String email;
    String password;
    Role role;
}
