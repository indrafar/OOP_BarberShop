package com.barbershop.barbershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.barbershop.barbershop.model.User;
import com.barbershop.barbershop.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        try {
            user.setRole("USER");
            userService.saveUser(user);
            return "redirect:/login"; // redirect setelah register berhasil
        } catch (Exception e) {
            model.addAttribute("error", "Registrasi gagal: " + e.getMessage());
            return "register";
        }
    }


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
