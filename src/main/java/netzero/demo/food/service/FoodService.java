package netzero.demo.food.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import netzero.demo.food.dto.FoodRegisterRequest;
import netzero.demo.food.entity.Food;
import netzero.demo.food.entity.FoodCategory;
import netzero.demo.food.repository.FoodRepository;
import netzero.demo.global.file.LocalFileStorageService;
import netzero.demo.restaurant.entity.Restaurant;
import netzero.demo.restaurant.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;
    private final LocalFileStorageService fileStorageService;

    @Transactional
    public Food register(FoodRegisterRequest request, MultipartFile image, Long memberId) {
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        if (!restaurant.getOwner().getId().equals(memberId)) {
            throw new IllegalStateException("본인 소유의 가게에만 게시물을 등록할 수 있습니다.");
        }

        String imageUrl = fileStorageService.store(image);

        Food food = Food.builder()
                .title(request.title())
                .imageUrl(imageUrl)
                .originalPrice(request.originalPrice())
                .discountRate(request.discountRate())
                .description(request.description())
                .category(request.category())
                .restaurant(restaurant)
                .build();

        return foodRepository.save(food);
    }

    public List<Food> getAll() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        return foodRepository.findBySoldFalseAndCreatedAtBetweenOrderByIdDesc(todayStart, todayEnd);
    }

    public List<Food> getByCategory(FoodCategory category) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        return foodRepository.findBySoldFalseAndCategoryAndCreatedAtBetweenOrderByIdDesc(
                category, todayStart, todayEnd);
    }

    public Food getById(Long id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
    }
}
