package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.exception.*;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class RestaurantApiController {
    private RestaurantService restaurantService;

    @Autowired
    public RestaurantApiController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // TODO

   @PostMapping("/api/restaurants")
public ResponseEntity<Object> createRestaurant(@Valid @RequestBody ApiCreateRestaurantDto restaurant, BindingResult result) {
    // if (result.hasErrors()) {
    //     return ResponseBuilder.buildBadRequestResponse("Invalid data");
    // }

    Optional<ApiCreateRestaurantDto> createdRestaurant = restaurantService.createRestaurant(restaurant);

    if (createdRestaurant.isPresent()) {
        return ResponseBuilder.buildOkResponse(createdRestaurant.get());
    } else {
        throw new BadRequestException("Restaurant creation failed");
    }
}

    
    // // TODO
    // // I CREATED THE BELOW CODE AND I DON'T KNOW IF IT WILL WORK 06/25/24 3:45pm
    // /**
    //  * Deletes a restaurant by ID.
    //  *
    //  @param id The ID of the restaurant to delete.
    //  @return ResponseEntity with a success message, or a ResourceNotFoundException if the restaurant is not found.
    
    // @DeleteMapping("/api/restaurants/{id}")
    // public ResponseEntity<Object> deleteRestaurant(@PathVariable int id) {
    //     // boolean isDeleted = restaurantService.deleteRestaurantById(id);
    //     if (!isDeleted) {
    //         throw new ResourceNotFoundException(String.format("Restaurant with id %d not found", id));
    //     }
    //     return ResponseBuilder.buildOkResponse(String.format("Restaurant with id %d has been deleted", id));
    // }
    // I CREATED THE ABOVE CODE AND I DON'T KNOW IF IT WILL WORK 06/25/24 3:45pm

    // TODO

    @PutMapping("/api/restaurants/{id}")
    public ResponseEntity<Object> updateRestaurant(@PathVariable("id") int id, @Valid @RequestBody ApiCreateRestaurantDto restaurantUpdateData, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseBuilder.buildBadRequestResponse("Invalid data");
        }

        Optional<ApiCreateRestaurantDto> updatedRestaurant = restaurantService.updateRestaurant(id, restaurantUpdateData);

        if (updatedRestaurant.isPresent()) {
            return ResponseBuilder.buildOkResponse(updatedRestaurant.get());
        } else {
            throw new ResourceNotFoundException(String.format("Restaurant with id %d not found", id));
        }
    }

    /**
     * Retrieves details for a restaurant, including its average rating, based on the provided restaurant ID.
     *
     * @param id The unique identifier of the restaurant to retrieve.
     * @return ResponseEntity with HTTP 200 OK if the restaurant is found, HTTP 404 Not Found otherwise.
     *
     * @see RestaurantService#findRestaurantWithAverageRatingById(int) for details on retrieving restaurant information.
     */
    @GetMapping("/api/restaurants/{id}")
    public ResponseEntity<Object> getRestaurantById(@PathVariable int id) {
        Optional<ApiRestaurantDto> restaurantWithRatingOptional = restaurantService.findRestaurantWithAverageRatingById(id);
        if (!restaurantWithRatingOptional.isPresent()) throw new ResourceNotFoundException(String.format("Restaurant with id %d not found", id));
        return ResponseBuilder.buildOkResponse(restaurantWithRatingOptional.get());
    }

    /**
     * Returns a list of restaurants given a rating and price range
     *
     * @param rating integer from 1 to 5 (optional)
     * @param priceRange integer from 1 to 3 (optional)
     * @return A list of restaurants that match the specified criteria
     * 
     * @see RestaurantService#findRestaurantsByRatingAndPriceRange(Integer, Integer) for details on retrieving restaurant information.
     */

    @GetMapping("/api/restaurants")
    public ResponseEntity<Object> getAllRestaurants(
        @RequestParam(name = "rating", required = false) Integer rating,
        @RequestParam(name = "price_range", required = false) Integer priceRange) {
        return ResponseBuilder.buildOkResponse(restaurantService.findRestaurantsByRatingAndPriceRange(rating, priceRange));
    }
}
