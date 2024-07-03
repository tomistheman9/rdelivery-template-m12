package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {
    /**
     * Finds an OrderStatus by its name.
     * @param name The name of the status (e.g., "pending", "in progress", "delivered").
     * @return An Optional containing the found OrderStatus, or an empty Optional if no status matches the name.
     */
    Optional<OrderStatus> findByName(String name);
}
