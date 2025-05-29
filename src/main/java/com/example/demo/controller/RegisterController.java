
package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";  // file register.html di templates
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model) {
        // cek konfirmasi password
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Password dan konfirmasi password tidak sama");
            return "register";
        }

        String result = userService.registerUser(user);
        if (!result.equals("success")) {
            model.addAttribute("error", result);
            return "register";
        }

        model.addAttribute("message", "Registrasi berhasil! Silakan login.");
        return "login"; // arahkan ke halaman login, buat halaman login.html tersendiri
    }
}
