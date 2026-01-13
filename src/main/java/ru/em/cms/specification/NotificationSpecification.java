package ru.em.cms.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import ru.em.cms.model.entity.NotificationEntity;
import ru.em.cms.model.request.GetNotificationRequest;

@RequiredArgsConstructor
public class NotificationSpecification implements Specification<NotificationEntity> {
    private final GetNotificationRequest filter;

    @Override
    public Predicate toPredicate(Root<NotificationEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (!ObjectUtils.isEmpty(filter.getId())) {
            predicates.add(cb.equal(root.get("id"), filter.getId()));
        }
        if (!ObjectUtils.isEmpty(filter.getType())) {
            predicates.add(cb.equal(root.get("type"), filter.getType()));
        }
        if (!ObjectUtils.isEmpty(filter.getIsRead())) {
            predicates.add(cb.equal(root.get("read"), filter.getIsRead()));
        }
        if (!ObjectUtils.isEmpty(filter.getDateFrom())) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"),
                    filter.getDateFrom()));
        }
        if (!ObjectUtils.isEmpty(filter.getDateTo())) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getDateTo()));
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
