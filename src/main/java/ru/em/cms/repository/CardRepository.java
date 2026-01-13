package ru.em.cms.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.em.cms.model.entity.CardEntity;
import ru.em.cms.model.type.Status;

public interface CardRepository extends JpaRepository<CardEntity, Long>, JpaSpecificationExecutor<CardEntity> {

    Optional<CardEntity> findByIdAndUserIdAndStatus(Long id, Long userId, Status status);

    Optional<CardEntity> findByIdAndUserId(Long id, Long userId);

    List<CardEntity> findByUserId(Long userId);
}
