package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.models.Order;
import com.rocketFoodDelivery.rocketFood.repository.CustomOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomOrderService {

    private final CustomOrderRepository customOrderRepository;

    @Autowired
    public CustomOrderService(CustomOrderRepository customOrderRepository) {
        this.customOrderRepository = customOrderRepository;
    }

    public List<Order> getOrdersByUserTypeAndId(String userType, int id) {
        switch (userType.toLowerCase()) {
            case "customer":
                return customOrderRepository.findByCustomerId(id);
            case "restaurant":
                return customOrderRepository.findByRestaurantId(id);
            case "courier":
                return customOrderRepository.findByCourierId(id);
            default:
                throw new IllegalArgumentException("Invalid user type: " + userType);
        }
    }
}
