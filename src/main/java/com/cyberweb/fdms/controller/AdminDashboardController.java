package com.cyberweb.fdms.controller;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cyberweb.fdms.entity.Order;
import com.cyberweb.fdms.entity.User;
import com.cyberweb.fdms.repository.OrderRepository;
import com.cyberweb.fdms.repository.RestaurantRepository;
import com.cyberweb.fdms.repository.UserRepository;
import com.cyberweb.fdms.util.SessionUtil;

@Controller
public class AdminDashboardController {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private RestaurantRepository restaurantRepository;
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/admin/dashboard")
	public String adminDashboard(HttpSession session, Model model) {

		// ===== Access Control =====
		if (!SessionUtil.isLoggedIn(session) || !"ADMIN".equals(SessionUtil.getRole(session))) {
			return "redirect:/login";
		}

		// ===== Total Orders =====
		long totalOrders = orderRepository.count();

		// ===== Total Restaurants =====
		long totalRestaurants = restaurantRepository.count();

		// ===== Total Customers =====
		long totalCustomers = userRepository.countByRole("CUSTOMER");

		// ===== Total Revenue =====
		BigDecimal totalRevenue = orderRepository.totalRevenue(); 
		
		List<Order> latestOrders = orderRepository.findTop5ByOrderByOrderTimeDesc();
		model.addAttribute("latestOrders", latestOrders);

		model.addAttribute("totalOrders", totalOrders);
		model.addAttribute("totalRestaurants", totalRestaurants);
		model.addAttribute("totalCustomers", totalCustomers);
		model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : 0);

		return "admin/admin-dashboard";
	}
	
	@GetMapping("/admin/customers")
    public String viewCustomers(HttpSession session, Model model) {

        // ===== Access Control =====
        if (!SessionUtil.isLoggedIn(session) ||
                !"ADMIN".equals(SessionUtil.getRole(session))) {
            return "redirect:/login";
        }

        // Fetch only CUSTOMERS
        List<User> customers = userRepository.findByRole("CUSTOMER");

        model.addAttribute("customers", customers);
        return "admin/admin-customers";
    }
}
