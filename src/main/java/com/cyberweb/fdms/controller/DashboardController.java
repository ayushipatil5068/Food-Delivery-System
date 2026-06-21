package com.cyberweb.fdms.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

import com.cyberweb.fdms.util.SessionUtil;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {

        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        String role = SessionUtil.getRole(session);

        if ("ADMIN".equals(role)) {
            return "redirect:/admin/dashboard";
        } 
        else if ("CUSTOMER".equals(role)) {
            return "redirect:/customer/dashboard";
        } 
        else if ("DELIVERY_AGENT".equals(role)) {
            return "redirect:/delivery/dashboard";
        }
        else if ("RESTAURANT".equals(role)) {      
            return "redirect:/owner/dashboard";
        }

        return "redirect:/login";
    }
}

