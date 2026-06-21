package com.cyberweb.fdms.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cyberweb.fdms.entity.*;
import com.cyberweb.fdms.model.CartItem;
import com.cyberweb.fdms.repository.OrderItemRepository;
import com.cyberweb.fdms.repository.OrderRepository;
import com.cyberweb.fdms.util.OrderStatus;
import com.cyberweb.fdms.util.SessionUtil;

import java.math.BigDecimal;
import java.util.Map;

@Controller
public class CustomerCheckoutController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // ---------------- CHECKOUT PAGE ----------------
    @GetMapping("/customer/checkout")
    public String checkoutPage(HttpSession session, Model model) {

        if (!SessionUtil.isLoggedIn(session) ||
                !"CUSTOMER".equals(SessionUtil.getRole(session))) {
            return "redirect:/login";
        }

        Map<Long, CartItem> cart =
                (Map<Long, CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            return "redirect:/customer/cart";
        }

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.values()) {
            total = total.add(
                    BigDecimal.valueOf(cartItem.getTotalPrice())
            );
        }

        model.addAttribute("cartItems", cart.values());
        model.addAttribute("total", total);

        return "customer/checkout";
    }

    // ---------------- PLACE ORDER ----------------
    @PostMapping("/customer/place-order")
    public String placeOrder(HttpSession session) {

        User customer = SessionUtil.getUser(session);

        Map<Long, CartItem> cart =
                (Map<Long, CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            return "redirect:/customer/cart";
        }

        Order order = new Order();
        order.setCustomer(customer);

        BigDecimal totalAmount = BigDecimal.ZERO;
        Restaurant restaurant = null;

        for (CartItem cartItem : cart.values()) {
            MenuItem menuItem = cartItem.getItem();

            totalAmount = totalAmount.add(
                    BigDecimal.valueOf(cartItem.getTotalPrice())
            );

            restaurant = menuItem.getRestaurant(); // same restaurant
        }

        order.setRestaurant(restaurant);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.ORDER_PLACED);

        Order savedOrder = orderRepository.save(order);

        // Save Order Items
        for (CartItem cartItem : cart.values()) {

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setMenuItem(cartItem.getItem());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getItem().getPrice());

            orderItemRepository.save(orderItem);
        }

        session.removeAttribute("cart");

        return "redirect:/customer/dashboard";
    }
}
