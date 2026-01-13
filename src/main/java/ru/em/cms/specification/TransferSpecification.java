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
import ru.em.cms.model.entity.TransferEntity;
import ru.em.cms.model.request.GetTransferRequest;

@RequiredArgsConstructor
public class TransferSpecification implements Specification<TransferEntity> {
    private final GetTransferRequest filter;

    @Override
    public Predicate toPredicate(Root<TransferEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (!ObjectUtils.isEmpty(filter.getId())) {
            predicates.add(cb.equal(root.get("id"), filter.getId()));
        }
        if (!ObjectUtils.isEmpty(filter.getStatus())) {
            predicates.add(cb.equal(root.get("status"), filter.getStatus()));
        }
        if (!ObjectUtils.isEmpty(filter.getCurrency())) {
            predicates.add(cb.equal(root.get("currency"), filter.getCurrency()));
        }
        if (!ObjectUtils.isEmpty(filter.getFromCard())) {
            predicates.add(cb.equal(root.get("fromCard"), filter.getFromCard()));
        }
        if (!ObjectUtils.isEmpty(filter.getToCard())) {
            predicates.add(cb.equal(root.get("toCard"), filter.getToCard()));
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
