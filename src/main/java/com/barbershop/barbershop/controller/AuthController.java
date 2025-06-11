package com.barbershop.barbershop.controller;

import com.barbershop.barbershop.model.User;
import com.barbershop.barbershop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }   

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userService.isUsernameTaken(user.getUsername())) {
            model.addAttribute("error", "Username sudah digunakan!");
            return "register";
        }

        user.setRole(User.Role.USER);
        userService.registerUser(user);
        return "redirect:/login";
    }

    @GetMapping("/index")
    public String showIndex() {
        return "index"; // templates/index.html
    }

}
