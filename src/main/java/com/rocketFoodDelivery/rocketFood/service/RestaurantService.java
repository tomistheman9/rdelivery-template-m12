package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.models.Address;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;


@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductOrderRepository productOrderRepository;
    private final UserRepository userRepository;
    private final AddressService addressService;

   @Autowired
    public RestaurantService(
        RestaurantRepository restaurantRepository,
        ProductRepository productRepository,
        OrderRepository orderRepository,
        ProductOrderRepository productOrderRepository,
        UserRepository userRepository,
        AddressService addressService) {
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.productOrderRepository = productOrderRepository;
        this.userRepository = userRepository;
        this.addressService = addressService;
    }

    public List<Restaurant> findAllRestaurants() {
        return restaurantRepository.findAll();
    }

    // /**
    //  * Retrieves a restaurant with its details, including the average rating, based on the provided restaurant ID.
    //  *
    //  * @param id The unique identifier of the restaurant to retrieve.
    //  * @return An Optional containing a RestaurantDTO with details such as id, name, price range, and average rating.
    //  *         If the restaurant with the given id is not found, an empty Optional is returned.
    //  *
    //  * @see RestaurantRepository#findRestaurantWithAverageRatingById(int) for the raw query details from the repository.
    //  */
    // public Optional<ApiRestaurantDto> findRestaurantWithAverageRatingById(int id) {
    //     List<Object[]> restaurant = restaurantRepository.findRestaurantWithAverageRatingById(id);

    //     if (!restaurant.isEmpty()) {
    //         Object[] row = restaurant.get(0);
    //         int restaurantId = (int) row[0];
    //         String name = (String) row[1];
    //         int priceRange = (int) row[2];
    //         double rating = (row[3] != null) ? ((BigDecimal) row[3]).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0;
    //         int roundedRating = (int) Math.ceil(rating);
    //         ApiRestaurantDto restaurantDto = new ApiRestaurantDto(restaurantId, name, priceRange, roundedRating);
    //         return Optional.of(restaurantDto);
    //     } else {
    //         return Optional.empty();
    //     }
    // }

    /**
     * Finds restaurants based on the provided rating and price range.
     *
     * @param rating     The rating for filtering the restaurants.
     * @param priceRange The price range for filtering the restaurants.
     * @return A list of ApiRestaurantDto objects representing the selected restaurants.
     *         Each object contains the restaurant's ID, name, price range, and a rounded-up average rating.
     */
    public List<ApiRestaurantDto> findRestaurantsByRatingAndPriceRange(Integer rating, Integer priceRange) {
        List<Object[]> restaurants = restaurantRepository.findRestaurantsByRatingAndPriceRange(rating, priceRange);

        List<ApiRestaurantDto> restaurantDtos = new ArrayList<>();

            for (Object[] row : restaurants) {
                int restaurantId = (int) row[0];
                String name = (String) row[1];
                int range = (int) row[2];
                double avgRating = (row[3] != null) ? ((BigDecimal) row[3]).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0.0;
                int roundedAvgRating = (int) Math.ceil(avgRating);
                restaurantDtos.add(new ApiRestaurantDto(restaurantId, name, range, roundedAvgRating));

            }

            return restaurantDtos;
    }

    // TODO

    @Transactional
    public Optional<ApiCreateRestaurantDto> createRestaurant(ApiCreateRestaurantDto restaurantDto) {
        try {

        //  if (!userRepository.existsById(restaurantDto.getUserId())) {
        //     return Optional.empty();
        // }

        ApiAddressDto receivedAddress = restaurantDto.getAddress();
        System.out.println(receivedAddress);

        Address newAddress = new Address();
        newAddress.setStreetAddress(receivedAddress.getStreetAddress());
        newAddress.setCity(receivedAddress.getCity());
        newAddress.setPostalCode(receivedAddress.getPostalCode());
        System.out.println(newAddress);

        addressService.saveAddress(newAddress);

        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantDto.getName());
        restaurant.setPriceRange(restaurantDto.getPriceRange());
        restaurant.setPhone(restaurantDto.getPhone());
        restaurant.setEmail(restaurantDto.getEmail());
        restaurant.setAddress(newAddress);
        restaurant.setUserEntity(userRepository.findById(restaurantDto.getUserId()).get());

        restaurantRepository.save(restaurant);

        restaurantDto.setId(restaurant.getId());
        return Optional.of(restaurantDto);
    
            
        } catch (Exception e) {
            System.out.println(e);
           return null;
        }
    }  
       


    /**ALL THE FOLLOWING BELOW IS WHAT I ADDED ON MY OWN
     * Finds a restaurant by its ID.
     *
     * @param id The ID of the restaurant to retrieve.
     * @return An Optional containing the restaurant with the specified ID,
     *         or Optional.empty() if no restaurant is found.
     */
    public Optional<Restaurant> findById(int id) {
        return restaurantRepository.findById(id);
    }

    /**
     * Finds a restaurant by its ID with its average rating.
     *
     * @param id The ID of the restaurant to retrieve.
     * @return An Optional containing ApiRestaurantDto with the specified ID and its average rating,
     *         or Optional.empty() if no restaurant is found.
     */
    public Optional<ApiRestaurantDto> findRestaurantWithAverageRatingById(int id) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findRestaurantById(id);
        if (restaurantOptional.isPresent()) {
            Restaurant restaurant = restaurantOptional.get();
            ApiRestaurantDto apiRestaurantDto = mapToApiRestaurantDto(restaurant);
            return Optional.of(apiRestaurantDto);
        }
        return Optional.empty();
    }

    // Assume this method exists for mapping Restaurant to ApiRestaurantDto
    private ApiRestaurantDto mapToApiRestaurantDto(Restaurant restaurant) {
        ApiRestaurantDto dto = new ApiRestaurantDto();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        // Add other fields as necessary
        return dto;
    /** I ADDED THE ABOVE CODE, I DON'T KNOW IF THIS WILL WORK... */ 
    
    }

    // TODO

    @Transactional
    public Optional<ApiCreateRestaurantDto> updateRestaurant(int id, ApiCreateRestaurantDto updatedRestaurantDto) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(id);

        System.out.println(restaurantOptional);

        if (restaurantOptional.isPresent()) {
            Restaurant restaurant = restaurantOptional.get();
            restaurant.setName(updatedRestaurantDto.getName());
            restaurant.setPriceRange(updatedRestaurantDto.getPriceRange());
            restaurant.setPhone(updatedRestaurantDto.getPhone());
            restaurant.setEmail(updatedRestaurantDto.getEmail());
                 System.out.println(restaurant);
            // Set other fields as necessary...

            restaurantRepository.save(restaurant);

            return Optional.of(mapToApiCreateRestaurantDto(restaurant));
        }

        return Optional.empty();
    }

    private ApiCreateRestaurantDto mapToApiCreateRestaurantDto(Restaurant restaurant) {
        ApiCreateRestaurantDto dto = new ApiCreateRestaurantDto();
        dto.setName(restaurant.getName());
        dto.setPriceRange(restaurant.getPriceRange());
        dto.setPhone(restaurant.getPhone());
        dto.setEmail(restaurant.getEmail());
        // Add other fields as necessary...
        return dto;
    }
    // TODO

     @Transactional
    public void deleteRestaurant(int restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found");
        }
        restaurantRepository.deleteRestaurantById(restaurantId);
    }
}