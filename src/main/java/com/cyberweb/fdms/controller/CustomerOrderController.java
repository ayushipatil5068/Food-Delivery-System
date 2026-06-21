package com.cyberweb.fdms.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cyberweb.fdms.entity.Order;
import com.cyberweb.fdms.entity.User;
import com.cyberweb.fdms.repository.OrderRepository;
import com.cyberweb.fdms.util.SessionUtil;

import java.util.List;

@Controller
public class CustomerOrderController {

    @Autowired
    private OrderRepository orderRepository;

    // ================= MY ORDERS =================
    @GetMapping("/customer/orders")
    public String myOrders(HttpSession session, Model model) {

        User customer = SessionUtil.getUser(session);

        if (customer == null || !"CUSTOMER".equals(customer.getRole())) {
            return "redirect:/login";
        }

        List<Order> orders =
                orderRepository.findByCustomerId(customer.getId());

        model.addAttribute("orders", orders);
        return "customer/order-tracking";
    }
}
