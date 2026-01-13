package ru.em.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.em.cms.model.entity.TransferEntity;

public interface TransferRepository extends JpaRepository<TransferEntity, Long>,
        JpaSpecificationExecutor<TransferEntity> {
}
