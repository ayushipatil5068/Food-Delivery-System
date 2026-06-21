package com.cyberweb.fdms.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cyberweb.fdms.entity.Restaurant;
import com.cyberweb.fdms.repository.RestaurantRepository;
import com.cyberweb.fdms.util.SessionUtil;

import java.util.List;

@Controller
public class CustomerDashboardController {

	@Autowired
	private RestaurantRepository restaurantRepository;

	@GetMapping("/customer/dashboard")
	public String customerDashboard(HttpSession session, Model model) {

		// Session + Role check
		if (!SessionUtil.isLoggedIn(session) || !"CUSTOMER".equals(SessionUtil.getRole(session))) {
			return "redirect:/login";
		}

		//Dummy stats (keep for now)
		model.addAttribute("activeOrders", 2);
		model.addAttribute("totalOrders", 18);

		//REAL DATA – Restaurants list
		List<Restaurant> restaurants = restaurantRepository.findByAvailableTrue();
		model.addAttribute("restaurants", restaurants);

		return "customer/customer-dashboard";
	}
}
