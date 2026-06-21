package com.cyberweb.fdms.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cyberweb.fdms.entity.MenuItem;
import com.cyberweb.fdms.model.CartItem;
import com.cyberweb.fdms.repository.MenuItemRepository;
import com.cyberweb.fdms.util.SessionUtil;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/customer/cart")
public class CustomerCartController {

    @Autowired
    private MenuItemRepository menuRepo;

    // ---------------- ADD TO CART ----------------
    @GetMapping("/add/{id}")
    public String addToCart(
            @PathVariable Long id,
            HttpSession session) {

        if (!SessionUtil.isLoggedIn(session) ||
            !"CUSTOMER".equals(SessionUtil.getRole(session))) {
            return "redirect:/login";
        }

        MenuItem item = menuRepo.findById(id).orElse(null);
        if (item == null) {
            return "redirect:/customer/dashboard";
        }

        Map<Long, CartItem> cart =
                (Map<Long, CartItem>) session.getAttribute("cart");

        if (cart == null) {
            cart = new HashMap<>();
        }

        if (cart.containsKey(id)) {
            cart.get(id).incrementQuantity();
        } else {
            cart.put(id, new CartItem(item));
        }

        session.setAttribute("cart", cart);

        return "redirect:/customer/cart";
    }

    // ---------------- VIEW CART ----------------
    @GetMapping
    public String viewCart(HttpSession session, Model model) {

        if (!SessionUtil.isLoggedIn(session) ||
            !"CUSTOMER".equals(SessionUtil.getRole(session))) {
            return "redirect:/login";
        }

        Map<Long, CartItem> cart =
                (Map<Long, CartItem>) session.getAttribute("cart");

        double total = 0;
        if (cart != null) {
            for (CartItem ci : cart.values()) {
                total += ci.getTotalPrice();
            }
        }

        model.addAttribute("cartItems",
                cart == null ? new HashMap<>() : cart);
        model.addAttribute("totalAmount", total);

        return "customer/cart";
    }

    // ---------------- REMOVE ITEM ----------------
    @GetMapping("/remove/{id}")
    public String removeItem(
            @PathVariable Long id,
            HttpSession session) {

        Map<Long, CartItem> cart =
                (Map<Long, CartItem>) session.getAttribute("cart");

        if (cart != null) {
            cart.remove(id);
            session.setAttribute("cart", cart);
        }

        return "redirect:/customer/cart";
    }
}
