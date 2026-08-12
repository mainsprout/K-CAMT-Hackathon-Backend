package netzero.demo.food.repository;

import java.util.List;
import netzero.demo.food.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByRestaurantId(Long restaurantId);

    List<Food> findAllByOrderByIdDesc();
}
