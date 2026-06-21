package com.cyberweb.fdms.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cyberweb.fdms.entity.Order;
import com.cyberweb.fdms.repository.OrderRepository;
import com.cyberweb.fdms.util.SessionUtil;

@Controller
public class AdminOrderController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/admin/orders")
    public String viewAllOrders(HttpSession session, Model model) {

        // ✅ Access Control
        if (!SessionUtil.isLoggedIn(session) ||
            !"ADMIN".equals(SessionUtil.getRole(session))) {
            return "redirect:/login";
        }

        // ✅ Load ALL orders with items
        List<Order> orders = orderRepository.findAllWithItems();

        model.addAttribute("orders", orders);

        return "admin/admin-orders";
    }
}