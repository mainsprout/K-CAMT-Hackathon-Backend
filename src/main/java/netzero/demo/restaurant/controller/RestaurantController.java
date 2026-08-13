package netzero.demo.restaurant.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import netzero.demo.member.security.MemberPrincipal;
import netzero.demo.restaurant.dto.RestaurantRegisterRequest;
import netzero.demo.restaurant.dto.RestaurantResponse;
import netzero.demo.restaurant.entity.Restaurant;
import netzero.demo.restaurant.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/mine")
    public ResponseEntity<List<RestaurantResponse>> getMine(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        List<RestaurantResponse> restaurants = restaurantService.getMine(principal.getMemberId()).stream()
                .map(RestaurantResponse::from)
                .toList();
        return ResponseEntity.ok(restaurants);
    }

    @PostMapping
    public ResponseEntity<RestaurantResponse> register(
            @RequestBody RestaurantRegisterRequest request,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Restaurant restaurant = restaurantService.register(request, principal.getMemberId());
        return ResponseEntity.ok(RestaurantResponse.from(restaurant));
    }
}
