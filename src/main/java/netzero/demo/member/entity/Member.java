package netzero.demo.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String googleId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    private String profileImageUrl;

    @Column(nullable = false)
    private Integer mileageBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20) default 'NONE'")
    private MemberRole role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Member(String googleId, String email, String name, String profileImageUrl) {
        this.googleId = googleId;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.mileageBalance = 0;
        this.role = MemberRole.NONE;
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateProfile(String name, String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public void earnMileage(int amount) {
        this.mileageBalance += amount;
    }

    public void updateRole(MemberRole role) {
        this.role = role;
    }
}
