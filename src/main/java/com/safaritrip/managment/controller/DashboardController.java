package com.safaritrip.management.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        // 'Authentication' is an object provided by Spring Security that holds
        // the details of the currently logged-in user.
        // We can get the username from it.
        String username = authentication.getName();

        // Add the username to the model so we can display it in our HTML view.
        model.addAttribute("username", username);

        // Return the name of the dashboard template.
        return "dashboard";
    }
}