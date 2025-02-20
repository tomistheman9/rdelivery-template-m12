package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.models.Order;
import com.rocketFoodDelivery.rocketFood.models.Product;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.service.CustomOrderService;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.exception.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RestaurantApiController {
    private final RestaurantService restaurantService;
    private final CustomOrderService customOrderService;

    @Autowired
    public RestaurantApiController(RestaurantService restaurantService, CustomOrderService customOrderService) {
        this.restaurantService = restaurantService;
        this.customOrderService = customOrderService;
    }

    @PostMapping("/restaurants")
    public ResponseEntity<Object> createRestaurant(@Valid @RequestBody ApiCreateRestaurantDto restaurantDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseBuilder.buildBadRequestResponse("Invalid data");
        }
        Optional<ApiCreateRestaurantDto> createdRestaurant = restaurantService.createRestaurant(restaurantDto);
        return createdRestaurant.map(ResponseBuilder::buildOkResponse)
                .orElseThrow(() -> new BadRequestException("Restaurant creation failed"));
    }

    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<Object> deleteRestaurant(@PathVariable int id) {
        try {
            restaurantService.deleteRestaurant(id);
            return ResponseBuilder.buildOkResponse(String.format("Restaurant with id %d has been deleted", id));
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(String.format("Restaurant with id %d not found", id));
        } catch (Exception e) {
            return ResponseBuilder.buildBadRequestResponse(e.getMessage());
        }
    }

    @PutMapping("/restaurants/{id}")
    public ResponseEntity<Object> updateRestaurant(@PathVariable("id") int id, @Valid @RequestBody ApiCreateRestaurantDto restaurantUpdateDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseBuilder.buildBadRequestResponse("Invalid data");
        }
        Optional<ApiCreateRestaurantDto> updatedRestaurant = restaurantService.updateRestaurant(id, restaurantUpdateDto);
        return updatedRestaurant.map(ResponseBuilder::buildOkResponse)
                .orElseThrow(() -> new BadRequestException(String.format("Unable to update restaurant with id %d", id)));
    }

    @GetMapping("/restaurants/{id}")
    public ResponseEntity<Object> getRestaurantById(@PathVariable int id) {
        Optional<ApiRestaurantDto> restaurant = restaurantService.findRestaurantWithAverageRatingById(id);
        return restaurant.map(ResponseBuilder::buildOkResponse)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Restaurant with id %d not found", id)));
    }

    @GetMapping("/restaurants")
    public ResponseEntity<Object> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.findAllRestaurants();
        return ResponseBuilder.buildOkResponse(restaurants);
    }

    @GetMapping("/restaurants/filter")
    public ResponseEntity<Object> getRestaurantsByRatingAndPriceRange(
            @RequestParam(name = "rating", required = false) Integer rating,
            @RequestParam(name = "priceRange", required = false) Integer priceRange) {
        List<ApiRestaurantDto> restaurants = restaurantService.findRestaurantsByRatingAndPriceRange(rating, priceRange);
        return ResponseBuilder.buildOkResponse(restaurants);
    }

    @GetMapping("/restaurants/{id}/products")
    public ResponseEntity<Object> getProductsByRestaurantId(@PathVariable int id) {
        List<Product> products = restaurantService.findProductsByRestaurantId(id);
        return ResponseBuilder.buildOkResponse(products);
    }

    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<Object> updateOrderStatus(@PathVariable int orderId, @RequestBody Map<String, String> statusMap) {
        if (!statusMap.containsKey("status")) {
            return ResponseBuilder.buildBadRequestResponse("Missing 'status' in request body");
        }
        String status = statusMap.get("status");
        try {
            restaurantService.updateOrderStatus(orderId, status);
            return ResponseBuilder.buildOkResponse("Order status updated successfully");
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            return ResponseBuilder.buildBadRequestResponse(e.getMessage());
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<Object> getOrdersByUserTypeAndId(
            @RequestParam(name = "type") String userType,
            @RequestParam(name = "id") int id) {
        try {
            List<Order> orders = customOrderService.getOrdersByUserTypeAndId(userType, id);
            return ResponseBuilder.buildOkResponse(orders);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildBadRequestResponse(e.getMessage());
        }
    }
}
