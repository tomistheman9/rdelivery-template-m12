package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.exception.*;
import com.rocketFoodDelivery.rocketFood.models.Order;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;

import java.util.Optional;
import java.util.List;

@RestController
public class RestaurantApiController {
    private RestaurantService restaurantService;

    @Autowired
    public RestaurantApiController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping("/api/restaurants")
    public ResponseEntity<Object> createRestaurant(@Valid @RequestBody ApiCreateRestaurantDto restaurant, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseBuilder.buildBadRequestResponse("Invalid data");
        }

        Optional<ApiCreateRestaurantDto> createdRestaurant = restaurantService.createRestaurant(restaurant);

        if (createdRestaurant.isPresent()) {
            return ResponseBuilder.buildOkResponse(createdRestaurant.get());
        } else {
            throw new BadRequestException("Restaurant creation failed");
        }
    }

    @DeleteMapping("/api/restaurants/{id}")
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

    @PutMapping("/api/restaurants/{id}")
    public ResponseEntity<Object> updateRestaurant(@PathVariable("id") int id, @Valid @RequestBody ApiCreateRestaurantDto restaurantUpdateData, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseBuilder.buildBadRequestResponse("Invalid data");
        }

        Optional<ApiCreateRestaurantDto> updatedRestaurant = restaurantService.updateRestaurant(id, restaurantUpdateData);

        if (updatedRestaurant.isPresent()) {
            return ResponseBuilder.buildOkResponse(updatedRestaurant.get());
        } else {
            throw new BadRequestException(String.format("Unable to update restaurant with id %d", id));
        }
    }

    @GetMapping("/api/restaurants/{id}")
    public ResponseEntity<Object> getRestaurantById(@PathVariable int id) {
        Optional<ApiRestaurantDto> restaurantOptional = restaurantService.findRestaurantWithAverageRatingById(id);

        if (restaurantOptional.isPresent()) {
            return ResponseBuilder.buildOkResponse(restaurantOptional.get());
        } else {
            throw new ResourceNotFoundException(String.format("Restaurant with id %d not found", id));
        }
    }

    @GetMapping("/api/restaurants")
    public ResponseEntity<Object> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.findAllRestaurants();
        return ResponseBuilder.buildOkResponse(restaurants);
    }

    @GetMapping("/api/restaurants/filter")
    public ResponseEntity<Object> getRestaurantsByRatingAndPriceRange(
            @RequestParam(name = "rating", required = false) Integer rating,
            @RequestParam(name = "priceRange", required = false) Integer priceRange) {
        try {
            List<ApiRestaurantDto> restaurants = restaurantService.findRestaurantsByRatingAndPriceRange(rating, priceRange);
            return ResponseBuilder.buildOkResponse(restaurants);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildBadRequestResponse(e.getMessage());
        }
    }

    // @GetMapping("/api/orders")
    // public ResponseEntity<Object> getOrdersByUserTypeAndId(
    //         @RequestParam(name = "userType") String userType,
    //         @RequestParam(name = "userId") int userId) {
    //     try {
    //         List<Order> orders = restaurantService.getOrdersByUserTypeAndId(userType, userId);
    //         return ResponseBuilder.buildOkResponse(orders);
    //     } catch (IllegalArgumentException e) {
    //         return ResponseBuilder.buildBadRequestResponse(e.getMessage());
    //     }
    
}

