package com.cyberweb.fdms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cyberweb.fdms.entity.User;
import com.cyberweb.fdms.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // ================= REGISTER PAGE =================
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // ================= REGISTER USER =================
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {

        // ✅ Check existing email
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            model.addAttribute("error", "Email already registered");
            return "register";
        }

        // ✅ IMPORTANT: Only CUSTOMER can have address
        if (user.getRole() == null || !user.getRole().equalsIgnoreCase("CUSTOMER")) {
            user.setAddress(null);
        }

        // ✅ Save user
        userRepository.save(user);

        return "redirect:/login";
    }

    // ================= LOGIN PAGE =================
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // ================= LOGIN USER =================
    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        User user = userRepository.findByEmail(email);

        // ❌ Invalid login
        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }

        // ✅ Store session data
        session.setAttribute("loggedInUser", user);
        session.setAttribute("role", user.getRole());

        // ✅ DEBUG (optional)
        System.out.println("LOGIN SUCCESS");
        System.out.println("User: " + user.getEmail());
        System.out.println("Role: " + user.getRole());

        // ================= ROLE BASED REDIRECT =================
        String role = user.getRole();

        if ("ADMIN".equalsIgnoreCase(role)) {
            return "redirect:/admin/dashboard";

        } else if ("RESTAURANT".equalsIgnoreCase(role)) {
            return "redirect:/owner/dashboard";

        } else if ("DELIVERY_AGENT".equalsIgnoreCase(role)) {
            return "redirect:/delivery/dashboard";

        } else {
            return "redirect:/customer/dashboard";
        }
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}