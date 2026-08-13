package netzero.demo.food.repository;

import java.time.LocalDateTime;
import java.util.List;
import netzero.demo.food.entity.Food;
import netzero.demo.food.entity.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByRestaurantId(Long restaurantId);

    List<Food> findBySoldFalseAndCreatedAtBetweenOrderByIdDesc(LocalDateTime start, LocalDateTime end);

    List<Food> findBySoldFalseAndCategoryAndCreatedAtBetweenOrderByIdDesc(
            FoodCategory category, LocalDateTime start, LocalDateTime end);
}
