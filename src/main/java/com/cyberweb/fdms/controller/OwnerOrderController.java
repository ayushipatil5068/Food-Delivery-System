package com.cyberweb.fdms.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cyberweb.fdms.entity.Order;
import com.cyberweb.fdms.entity.Restaurant;
import com.cyberweb.fdms.entity.User;
import com.cyberweb.fdms.repository.OrderRepository;
import com.cyberweb.fdms.repository.RestaurantRepository;
import com.cyberweb.fdms.util.OrderStatus;
import com.cyberweb.fdms.util.SessionUtil;

import java.util.List;

@Controller
@RequestMapping("/owner/orders")
public class OwnerOrderController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private OrderRepository orderRepository;

    // ================= VIEW OWNER ORDERS =================
    @GetMapping
    public String viewOrders(HttpSession session, Model model) {

        User owner = SessionUtil.getUser(session);
        String role = SessionUtil.getRole(session);

        // ✅ FIX: USE "RESTAURANT" (NOT OWNER)
        if (owner == null || role == null || !role.equalsIgnoreCase("RESTAURANT")) {
            return "redirect:/login";
        }

        List<Restaurant> restaurants = restaurantRepository.findByOwner(owner);

        if (restaurants == null || restaurants.isEmpty()) {
            return "redirect:/owner/dashboard";
        }

        Long restaurantId = restaurants.get(0).getId();

        // ✅ FETCH ORDERS WITH ITEMS
        List<Order> orders = orderRepository.findByRestaurantIdWithItems(restaurantId);

        model.addAttribute("orders", orders);

        return "owner/order-list";
    }

    // ================= ACCEPT =================
    @PostMapping("/accept/{orderId}")
    public String acceptOrder(@PathVariable Long orderId, HttpSession session) {
        return updateStatus(orderId, OrderStatus.PREPARING, session);
    }

    // ================= PREPARE =================
    @PostMapping("/prepare/{orderId}")
    public String prepareOrder(@PathVariable Long orderId, HttpSession session) {
        return updateStatus(orderId, OrderStatus.OUT_FOR_DELIVERY, session);
    }

    // ================= DELIVER =================
    @PostMapping("/deliver/{orderId}")
    public String deliverOrder(@PathVariable Long orderId, HttpSession session) {
        return updateStatus(orderId, OrderStatus.DELIVERED, session);
    }

    // ================= COMMON METHOD =================
    private String updateStatus(Long orderId, OrderStatus newStatus, HttpSession session) {

        User owner = SessionUtil.getUser(session);
        String role = SessionUtil.getRole(session);

        // ✅ FIX HERE ALSO
        if (owner == null || role == null || !role.equalsIgnoreCase("RESTAURANT")) {
            return "redirect:/login";
        }

        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            return "redirect:/owner/orders";
        }

        if (order.getRestaurant() == null ||
            order.getRestaurant().getOwner() == null ||
            !order.getRestaurant().getOwner().getId().equals(owner.getId())) {
            return "redirect:/owner/orders";
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        return "redirect:/owner/orders";
    }
}