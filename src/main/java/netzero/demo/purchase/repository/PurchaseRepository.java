package netzero.demo.purchase.repository;

import java.util.List;
import netzero.demo.purchase.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByBuyerIdOrderByPurchasedAtDesc(Long buyerId);
}
