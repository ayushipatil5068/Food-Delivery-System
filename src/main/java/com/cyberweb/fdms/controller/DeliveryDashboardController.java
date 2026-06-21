package com.cyberweb.fdms.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cyberweb.fdms.util.SessionUtil;

@Controller
public class DeliveryDashboardController {

    @GetMapping("/delivery/dashboard")
    public String deliveryDashboard(HttpSession session, Model model) {

        if (!SessionUtil.isLoggedIn(session) ||
            !"DELIVERY_AGENT".equals(SessionUtil.getRole(session))) {
            return "redirect:/login";
        }

        model.addAttribute("assignedOrders", 1);
        model.addAttribute("completedOrders", 45);

        return "delivery/delivery-dashboard";
    }
}

