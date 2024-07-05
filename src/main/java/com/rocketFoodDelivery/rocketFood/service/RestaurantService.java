package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.models.Address;
import com.rocketFoodDelivery.rocketFood.models.Order;
import com.rocketFoodDelivery.rocketFood.models.OrderStatus;
import com.rocketFoodDelivery.rocketFood.models.Product;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.repository.OrderRepository;
import com.rocketFoodDelivery.rocketFood.repository.OrderStatusRepository;
import com.rocketFoodDelivery.rocketFood.repository.ProductRepository;
import com.rocketFoodDelivery.rocketFood.repository.RestaurantRepository;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
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
    private final OrderStatusRepository orderStatusRepository;
    private final UserRepository userRepository;
    private final AddressService addressService;

    @Autowired
    public RestaurantService(
            RestaurantRepository restaurantRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            OrderStatusRepository orderStatusRepository,
            UserRepository userRepository,
            AddressService addressService) {
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.userRepository = userRepository;
        this.addressService = addressService;
    }

    public List<Restaurant> findAllRestaurants() {
        return restaurantRepository.findAll();
    }

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

    @Transactional
    public Optional<ApiCreateRestaurantDto> createRestaurant(ApiCreateRestaurantDto restaurantDto) {
        ApiAddressDto receivedAddress = restaurantDto.getAddress();
        Address newAddress = new Address();
        newAddress.setStreetAddress(receivedAddress.getStreetAddress());
        newAddress.setCity(receivedAddress.getCity());
        newAddress.setPostalCode(receivedAddress.getPostalCode());
        // newAddress.setAddress(newAddress);

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
    }

    public Optional<ApiRestaurantDto> findRestaurantWithAverageRatingById(int id) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findRestaurantById(id);
        if (restaurantOptional.isPresent()) {
            Restaurant restaurant = restaurantOptional.get();
            ApiRestaurantDto apiRestaurantDto = mapToApiRestaurantDto(restaurant);
            return Optional.of(apiRestaurantDto);
        }
        return Optional.empty();
    }

    private ApiRestaurantDto mapToApiRestaurantDto(Restaurant restaurant) {
        ApiRestaurantDto dto = new ApiRestaurantDto();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setPriceRange(restaurant.getPriceRange());
        // Add logic to calculate and set rating
        dto.setRating(0); // Replace with actual rating calculation
        return dto;
    }

    @Transactional
    public Optional<ApiCreateRestaurantDto> updateRestaurant(int id, ApiCreateRestaurantDto updatedRestaurantDto) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(id);
        if (restaurantOptional.isPresent()) {
            Restaurant restaurant = restaurantOptional.get();
            restaurant.setName(updatedRestaurantDto.getName());
            restaurant.setPriceRange(updatedRestaurantDto.getPriceRange());
            restaurant.setPhone(updatedRestaurantDto.getPhone());
            restaurant.setEmail(updatedRestaurantDto.getEmail());
            // Set other fields as necessary...

            restaurantRepository.save(restaurant);
            return Optional.of(mapToApiCreateRestaurantDto(restaurant));
        }
        return Optional.empty();
    }

    private ApiCreateRestaurantDto mapToApiCreateRestaurantDto(Restaurant restaurant) {
        ApiCreateRestaurantDto dto = new ApiCreateRestaurantDto();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setPriceRange(restaurant.getPriceRange());
        dto.setPhone(restaurant.getPhone());
        dto.setEmail(restaurant.getEmail());
        // Add other fields as necessary...
        return dto;
    }

    @Transactional
    public void deleteRestaurant(int restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found");
        }
        restaurantRepository.deleteRestaurantById(restaurantId);
    }

    public List<Product> findProductsByRestaurantId(int restaurantId) {
        return productRepository.findProductsByRestaurantId(restaurantId);
    }
@Transactional
public void updateOrderStatus(int orderId, String statusName) {
    Optional<Order> orderOptional = orderRepository.findById(orderId);
    if (!orderOptional.isPresent()) {
        throw new ResourceNotFoundException("Order with ID " + orderId + " not found.");
    }
    Order order = orderOptional.get();

    // Map string status to OrderStatus entity
    Optional<OrderStatus> statusOptional = orderStatusRepository.findByName(statusName);
    if (!statusOptional.isPresent()) {
        throw new IllegalArgumentException("Invalid status: " + statusName);
    }
    order.setOrderStatus(statusOptional.get());
    orderRepository.save(order);
}



}
