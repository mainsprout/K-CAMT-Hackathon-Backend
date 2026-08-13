package netzero.demo.restaurant.repository;

import java.util.List;
import netzero.demo.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByOwnerId(Long ownerId);

    boolean existsByOwnerId(Long ownerId);
}
