package com.cyberweb.fdms.util;

import com.cyberweb.fdms.entity.User;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loggedInUser") != null;
    }

    public static String getRole(HttpSession session) {
        Object role = session.getAttribute("role");
        return role == null ? null : role.toString();
    }

    // Get logged-in user
    public static User getUser(HttpSession session) {
        Object user = session.getAttribute("loggedInUser");
        return user != null ? (User) user : null;
    }

    // ✅ SAFE METHOD (NO CRASH)
    public static Long getRestaurantId(HttpSession session) {
        Object id = session.getAttribute("restaurantId");
        return id != null ? (Long) id : null;
    }
}