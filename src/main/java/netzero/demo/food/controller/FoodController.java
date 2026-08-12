package netzero.demo.food.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import netzero.demo.food.dto.FoodRegisterRequest;
import netzero.demo.food.dto.FoodResponse;
import netzero.demo.food.entity.Food;
import netzero.demo.food.service.FoodService;
import netzero.demo.member.security.MemberPrincipal;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FoodResponse> register(
            @RequestParam Long restaurantId,
            @RequestParam String title,
            @RequestParam Integer price,
            @RequestParam String description,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        FoodRegisterRequest request = new FoodRegisterRequest(restaurantId, title, price, description);
        Food food = foodService.register(request, image, principal.getMemberId());
        return ResponseEntity.ok(FoodResponse.from(food));
    }

    @GetMapping
    public ResponseEntity<List<FoodResponse>> getAll() {
        List<FoodResponse> responses = foodService.getAll().stream()
                .map(FoodResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> getById(@PathVariable Long id) {
        Food food = foodService.getById(id);
        return ResponseEntity.ok(FoodResponse.from(food));
    }
}
