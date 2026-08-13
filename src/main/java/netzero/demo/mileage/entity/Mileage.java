package netzero.demo.mileage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import netzero.demo.member.entity.Member;
import netzero.demo.purchase.entity.Purchase;

@Entity
@Table(name = "mileage")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mileage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false, unique = true)
    private Purchase purchase;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime earnedAt;

    @Builder
    private Mileage(Member member, Purchase purchase, Integer amount) {
        this.member = member;
        this.purchase = purchase;
        this.amount = amount;
    }

    @PrePersist
    private void prePersist() {
        this.earnedAt = LocalDateTime.now();
    }
}
