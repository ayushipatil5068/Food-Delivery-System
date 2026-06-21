package com.cyberweb.fdms.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cyberweb.fdms.entity.Restaurant;
import com.cyberweb.fdms.repository.MenuItemRepository;
import com.cyberweb.fdms.repository.RestaurantRepository;
import com.cyberweb.fdms.util.SessionUtil;

@Controller
@RequestMapping("/customer")
public class CustomerMenuController {

    @Autowired
    private RestaurantRepository restaurantRepo;

    @Autowired
    private MenuItemRepository menuRepo;

    // View menu of selected restaurant
    @GetMapping("/restaurant/{id}")
    public String viewRestaurantMenu(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (!SessionUtil.isLoggedIn(session) ||
            !"CUSTOMER".equals(SessionUtil.getRole(session))) {
            return "redirect:/login";
        }

        Restaurant restaurant = restaurantRepo.findById(id).orElse(null);
        if (restaurant == null) {
            return "redirect:/customer/dashboard";
        }

        model.addAttribute("restaurant", restaurant);
        model.addAttribute(
            "menuItems",
            menuRepo.findByRestaurantIdAndAvailableTrue(id)
        );

        return "customer/restaurant-menu";
    }
}
