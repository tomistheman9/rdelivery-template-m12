package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomOrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomerId(int customerId);
    List<Order> findByRestaurantId(int restaurantId);
    List<Order> findByCourierId(int courierId);
}
