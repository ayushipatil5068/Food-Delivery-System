package com.cyberweb.fdms.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cyberweb.fdms.entity.Restaurant;
import com.cyberweb.fdms.entity.User;
import com.cyberweb.fdms.repository.RestaurantRepository;
import com.cyberweb.fdms.repository.UserRepository;
import com.cyberweb.fdms.util.SessionUtil;

import java.util.List;

@Controller
@RequestMapping("/admin/restaurants")
public class RestaurantController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	// View All Restaurants
	@GetMapping
	public String listRestaurants(HttpSession session, Model model) {
		if (!SessionUtil.isLoggedIn(session) || !"ADMIN".equals(SessionUtil.getRole(session))) {
			return "redirect:/login";
		}

		List<Restaurant> restaurants = restaurantRepository.findAll();
		model.addAttribute("restaurants", restaurants);
		return "admin/restaurant-list";
	}

	// Show Add Restaurant Form
	@GetMapping("/add")
	public String showAddForm(HttpSession session, Model model) {
		if (!SessionUtil.isLoggedIn(session) || !"ADMIN".equals(SessionUtil.getRole(session))) {
			return "redirect:/login";
		}
		// Fetch all owners
		List<User> owners = userRepository.findByRole("RESTAURANT");
		model.addAttribute("owners", owners);

		model.addAttribute("restaurant", new Restaurant());
		return "admin/restaurant-form";
	}

	// Save Restaurant
	@PostMapping("/save")
	public String saveRestaurant(@ModelAttribute Restaurant restaurant, HttpSession session) {
		if (!SessionUtil.isLoggedIn(session) || !"ADMIN".equals(SessionUtil.getRole(session))) {
			return "redirect:/login";
		}

		restaurantRepository.save(restaurant);
		return "redirect:/admin/restaurants";
	}

	// Edit Restaurant
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable Long id, HttpSession session, Model model) {
		if (!SessionUtil.isLoggedIn(session) || !"ADMIN".equals(SessionUtil.getRole(session))) {
			return "redirect:/login";
		}

		Restaurant restaurant = restaurantRepository.findById(id).orElse(null);
		if (restaurant == null) {
			return "redirect:/admin/restaurants";
		}
		List<User> owners = userRepository.findByRole("RESTAURANT");
		model.addAttribute("owners", owners);

		model.addAttribute("restaurant", restaurant);
		return "admin/restaurant-form";
	}

	// Delete Restaurant
	@GetMapping("/delete/{id}")
	public String deleteRestaurant(@PathVariable Long id, HttpSession session) {
		if (!SessionUtil.isLoggedIn(session) || !"ADMIN".equals(SessionUtil.getRole(session))) {
			return "redirect:/login";
		}

		restaurantRepository.deleteById(id);
		return "redirect:/admin/restaurants";
	}

}
