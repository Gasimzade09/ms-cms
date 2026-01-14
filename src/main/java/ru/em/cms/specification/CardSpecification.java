package ru.em.cms.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import ru.em.cms.model.entity.CardEntity;
import ru.em.cms.model.request.GetCardRequest;

@RequiredArgsConstructor
public class CardSpecification implements Specification<CardEntity> {
    private final GetCardRequest filter;

    @Override
    public Predicate toPredicate(Root<CardEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (!ObjectUtils.isEmpty(filter.getId())) {
            predicates.add(cb.equal(root.get("id"), filter.getId()));
        }
        if (!ObjectUtils.isEmpty(filter.getUserId())) {
            predicates.add(cb.equal(root.get("user").get("id"), filter.getUserId()));
        }
        if (!ObjectUtils.isEmpty(filter.getType())) {
            predicates.add(cb.equal(root.get("type"), filter.getType()));
        }
        if (!Optional.ofNullable(filter.getIncludeZeroBalance()).orElse(true)) {
            predicates.add(cb.greaterThan(root.get("balance"), BigDecimal.ZERO));
        }


        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
