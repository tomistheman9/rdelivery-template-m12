package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByRestaurantId(int restaurantId);

    @Query(nativeQuery = true, value = "SELECT * FROM products WHERE restaurant_id = :restaurantId")
    List<Product> findProductsByRestaurantId(@Param("restaurantId") int restaurantId);
}
