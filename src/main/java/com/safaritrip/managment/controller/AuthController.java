package com.safaritrip.management.controller;

import com.safaritrip.management.dto.UserDto;
import com.safaritrip.management.model.User;
import com.safaritrip.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid UserDto userDto, BindingResult result, Model model) {
        if (userService.findByUsername(userDto.getUsername()) != null) {
            result.rejectValue("username", null, "Username already exists");
        }
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", null, "Passwords do not match");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "register";
        }
        userService.saveUser(userDto);
        return "redirect:/login?success";
    }
}