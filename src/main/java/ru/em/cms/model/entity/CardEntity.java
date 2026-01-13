package ru.em.cms.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.em.cms.model.type.CardType;
import ru.em.cms.model.type.Status;
import ru.em.cms.model.type.Currency;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card")
@EqualsAndHashCode(exclude = "user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String cardNumber;
    LocalDate expiryDate;
    BigDecimal balance;
    @Enumerated(EnumType.STRING)
    Status status;
    String cardHolder;
    @Enumerated(EnumType.STRING)
    Currency currency;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    UserEntity user;
    @Enumerated(EnumType.STRING)
    CardType type;
    @CreationTimestamp
    LocalDateTime createdAt;
    @UpdateTimestamp
    LocalDateTime updatedAt;
    @Version
    Long version;
}
