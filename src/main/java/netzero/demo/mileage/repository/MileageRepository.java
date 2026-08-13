package netzero.demo.mileage.repository;

import netzero.demo.mileage.entity.Mileage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MileageRepository extends JpaRepository<Mileage, Long> {
}
