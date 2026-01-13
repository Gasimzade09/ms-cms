package ru.em.cms.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.em.cms.model.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}
