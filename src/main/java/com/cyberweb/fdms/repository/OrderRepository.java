package com.cyberweb.fdms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cyberweb.fdms.entity.Order;
import com.cyberweb.fdms.util.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ================= BASIC METHODS =================

    List<Order> findByCustomerId(Long customerId);

    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    BigDecimal totalRevenue();

    List<Order> findTop5ByOrderByOrderTimeDesc();

    List<Order> findByRestaurantId(Long restaurantId);

    List<Order> findByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);

    List<Order> findTop10ByRestaurantIdOrderByOrderTimeDesc(Long restaurantId);

    // ================= ADMIN (ALL ORDERS WITH ITEMS) =================
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.menuItem")
    List<Order> findAllWithItems();

    // ================= OWNER (FILTERED ORDERS WITH ITEMS) =================
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.menuItem " +
           "WHERE o.restaurant.id = :restaurantId")
    List<Order> findByRestaurantIdWithItems(Long restaurantId);
}