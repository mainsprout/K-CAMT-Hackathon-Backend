package netzero.demo.purchase.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import netzero.demo.food.entity.Food;
import netzero.demo.member.entity.Member;

@Entity
@Table(name = "purchase")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Member buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private LocalTime pickupTime;

    @Column(nullable = false, updatable = false)
    private LocalDateTime purchasedAt;

    @Builder
    private Purchase(Member buyer, Food food, Integer price, LocalTime pickupTime) {
        this.buyer = buyer;
        this.food = food;
        this.price = price;
        this.pickupTime = pickupTime;
    }

    @PrePersist
    private void prePersist() {
        this.purchasedAt = LocalDateTime.now();
    }
}
