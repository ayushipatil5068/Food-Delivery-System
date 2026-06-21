package com.cyberweb.fdms.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cyberweb.fdms.entity.MenuItem;
import com.cyberweb.fdms.entity.Restaurant;
import com.cyberweb.fdms.entity.User;
import com.cyberweb.fdms.repository.MenuItemRepository;
import com.cyberweb.fdms.repository.RestaurantRepository;
import com.cyberweb.fdms.util.SessionUtil;

import java.util.List;

@Controller
@RequestMapping("/owner")
public class OwnerController {

	@Autowired
	private RestaurantRepository restaurantRepository;
	@Autowired
	private MenuItemRepository menuItemRepository;

	@GetMapping("/dashboard")
	public String ownerDashboard(HttpSession session, Model model) {
		User owner = SessionUtil.getUser(session);
		if (owner == null || !"RESTAURANT".equals(owner.getRole())) {
			return "redirect:/login";
		}

		// Fetch restaurants for this owner
		List<Restaurant> restaurants = restaurantRepository.findByOwner(owner);
		model.addAttribute("restaurants", restaurants);

		return "owner/dashboard";
	}

	// ==================== RESTAURANT ====================

	// Show Owner's Restaurant
	@GetMapping("/restaurants")
	public String myRestaurant(HttpSession session, Model model) {
		User owner = SessionUtil.getUser(session);
		if (owner == null || !"RESTAURANT".equals(owner.getRole())) {
			return "redirect:/login";
		}

		// Fetch restaurant for this owner
		List<Restaurant> restaurants = restaurantRepository.findByOwner(owner);
		model.addAttribute("restaurants", restaurants);
		return "owner/restaurant-list";
	}

	// Edit Owner's Restaurant
	@GetMapping("/restaurants/edit/{id}")
	public String editRestaurant(@PathVariable Long id, HttpSession session, Model model) {
		User owner = SessionUtil.getUser(session);
		if (owner == null || !"RESTAURANT".equals(owner.getRole())) {
			return "redirect:/login";
		}

		Restaurant restaurant = restaurantRepository.findById(id).orElse(null);
		if (restaurant == null || !restaurant.getOwner().getId().equals(owner.getId())) {
			return "redirect:/owner/dashboard";
		}

		model.addAttribute("restaurant", restaurant);
		return "owner/restaurant-form";
	}

	// Save Owner's Restaurant
	@PostMapping("/restaurants/save")
	public String saveRestaurant(@ModelAttribute Restaurant restaurant, HttpSession session) {
		User owner = SessionUtil.getUser(session);
		if (owner == null || !"RESTAURANT".equals(owner.getRole())) {
			return "redirect:/login";
		}

		// Ensure owner cannot change restaurant owner
		restaurant.setOwner(owner);
		restaurantRepository.save(restaurant);

		return "redirect:/owner/dashboard";
	}

	// ==================== MENU ====================

	// List Menu Items for Owner's Restaurant
	@GetMapping("/menu/{restaurantId}")
	public String menuList(@PathVariable Long restaurantId, HttpSession session, Model model) {
		User owner = SessionUtil.getUser(session);
		if (owner == null || !"RESTAURANT".equals(owner.getRole())) {
			return "redirect:/login";
		}

		Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
		if (restaurant == null || !restaurant.getOwner().getId().equals(owner.getId())) {
			return "redirect:/owner/dashboard";
		}

		List<MenuItem> items = menuItemRepository.findByRestaurantId(restaurantId);
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("menuItems", items);

		return "owner/menu-list";
	}

	// Show Add/Edit Menu Form
	@GetMapping("/menu/form/{restaurantId}")
	public String menuForm(@PathVariable Long restaurantId, @RequestParam(required = false) Long id,
			HttpSession session, Model model) {
		User owner = SessionUtil.getUser(session);
		if (owner == null || !"RESTAURANT".equals(owner.getRole())) {
			return "redirect:/login";
		}

		Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
		if (restaurant == null || !restaurant.getOwner().getId().equals(owner.getId())) {
			return "redirect:/owner/dashboard";
		}

		MenuItem item = id != null ? menuItemRepository.findById(id).orElse(new MenuItem()) : new MenuItem();
		item.setRestaurant(restaurant);

		model.addAttribute("restaurant", restaurant);
		model.addAttribute("menuItem", item);
		return "owner/menu-form";
	}

	// Save Menu Item
	@PostMapping("/menu/save")
	public String saveMenu(@ModelAttribute MenuItem menuItem, HttpSession session) {
		User owner = SessionUtil.getUser(session);
		if (owner == null || !"RESTAURANT".equals(owner.getRole())) {
			return "redirect:/login";
		}

		// Ensure menu item belongs to owner's restaurant
		Restaurant restaurant = restaurantRepository.findById(menuItem.getRestaurant().getId()).orElse(null);
		if (restaurant == null || !restaurant.getOwner().getId().equals(owner.getId())) {
			return "redirect:/owner/dashboard";
		}

		menuItem.setRestaurant(restaurant);
		menuItemRepository.save(menuItem);
		return "redirect:/owner/menu/" + restaurant.getId();
	}

	// Delete Menu Item
	@GetMapping("/menu/delete/{id}")
	public String deleteMenu(@PathVariable Long id, HttpSession session) {
		User owner = SessionUtil.getUser(session);
		if (owner == null || !"RESTAURANT".equals(owner.getRole())) {
			return "redirect:/login";
		}

		MenuItem item = menuItemRepository.findById(id).orElse(null);
		if (item != null && item.getRestaurant().getOwner().getId().equals(owner.getId())) {
			Long restaurantId = item.getRestaurant().getId();
			menuItemRepository.delete(item);
			return "redirect:/owner/menu/" + restaurantId;
		}

		return "redirect:/owner/dashboard";
	}
}
