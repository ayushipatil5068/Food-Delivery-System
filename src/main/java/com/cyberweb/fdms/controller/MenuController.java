package com.cyberweb.fdms.controller;


import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cyberweb.fdms.entity.MenuItem;
import com.cyberweb.fdms.entity.Restaurant;
import com.cyberweb.fdms.repository.MenuItemRepository;
import com.cyberweb.fdms.repository.RestaurantRepository;
import com.cyberweb.fdms.util.SessionUtil;

import java.util.List;

@Controller
@RequestMapping("/admin/menu")
public class MenuController {

	@Autowired
    private MenuItemRepository menuItemRepository;
	@Autowired
	private RestaurantRepository restaurantRepository;

    // List menu items for a restaurant
    @GetMapping("/{restaurantId}")
    public String listMenu(@PathVariable Long restaurantId, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session) || !"ADMIN".equals(SessionUtil.getRole(session))) {
            return "redirect:/login";
        }

        List<MenuItem> items = menuItemRepository.findByRestaurantId(restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);

        model.addAttribute("menuItems", items);
        model.addAttribute("restaurant", restaurant);

        return "admin/menu-list";
    }

    // Show Add/Edit Menu Form
    @GetMapping("/form/{restaurantId}")
    public String showMenuForm(@PathVariable Long restaurantId,
                               @RequestParam(required = false) Long id,
                               HttpSession session, Model model) {

        if (!SessionUtil.isLoggedIn(session) || !"ADMIN".equals(SessionUtil.getRole(session))) {
            return "redirect:/login";
        }

        MenuItem item = id != null ? menuItemRepository.findById(id).orElse(new MenuItem()) : new MenuItem();
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);

        item.setRestaurant(restaurant);
        model.addAttribute("menuItem", item);
        model.addAttribute("restaurant", restaurant);

        return "admin/menu-form";
    }

    // Save Menu Item
    @PostMapping("/save")
    public String saveMenu(@ModelAttribute MenuItem menuItem, HttpSession session) {
        if (!SessionUtil.isLoggedIn(session) || !"ADMIN".equals(SessionUtil.getRole(session))) {
            return "redirect:/login";
        }

        menuItemRepository.save(menuItem);
        return "redirect:/admin/menu/" + menuItem.getRestaurant().getId();
    }

    // Delete Menu Item
    @GetMapping("/delete/{id}")
    public String deleteMenu(@PathVariable Long id, HttpSession session) {
        if (!SessionUtil.isLoggedIn(session) || !"ADMIN".equals(SessionUtil.getRole(session))) {
            return "redirect:/login";
        }

        MenuItem item = menuItemRepository.findById(id).orElse(null);
        if (item != null) {
            Long restaurantId = item.getRestaurant().getId();
            menuItemRepository.delete(item);
            return "redirect:/admin/menu/" + restaurantId;
        }

        return "redirect:/admin/restaurants";
    }
}

