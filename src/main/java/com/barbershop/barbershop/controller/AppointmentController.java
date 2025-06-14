package com.barbershop.barbershop.controller;

import com.barbershop.barbershop.model.Transaction;
import com.barbershop.barbershop.model.User;
import com.barbershop.barbershop.repository.TransactionRepository;
import com.barbershop.barbershop.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AppointmentController {
    private static final Map<String, String> SERVICE_NAME_MAP = new HashMap<>();
    static {
        SERVICE_NAME_MAP.put("haircut", "Haircut");
        SERVICE_NAME_MAP.put("smoothing", "Smoothing");
        SERVICE_NAME_MAP.put("shave", "Shave");
        SERVICE_NAME_MAP.put("colouring", "Colouring");
    }

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;

    public static class ServiceDetail {
        public String name;
        public Integer price;
        public ServiceDetail(String name, Integer price) {
            this.name = name;
            this.price = price;
        }
    }

    @PostMapping("/appointment")
    public String showAppointmentForm(@RequestParam("hairstylist") String hairstylist,
                                      @RequestParam(value = "services") List<String> services,
                                      Model model, HttpSession session) {
        session.setAttribute("hairstylist", hairstylist);
        // Normalisasi dan mapping ke nama yang benar
        List<String> normalizedServices = services.stream()
                .map(s -> SERVICE_NAME_MAP.getOrDefault(s.trim().toLowerCase(), s.trim()))
                .collect(Collectors.toList());
        session.setAttribute("services", normalizedServices);
        model.addAttribute("hairstylist", hairstylist);
        model.addAttribute("services", normalizedServices);
        return "appointment";
    }

    @PostMapping("/payment")
    public String showPaymentPage(@RequestParam("appointmentDate") String appointmentDate,
                                  @RequestParam("appointmentTime") String appointmentTime,
                                  HttpSession session, Model model) {
        String hairstylist = (String) session.getAttribute("hairstylist");
        List<String> services = (List<String>) session.getAttribute("services");
        Map<String, Integer> priceMap = new HashMap<>();
        priceMap.put("Haircut", 30000);
        priceMap.put("Smoothing", 50000);
        priceMap.put("Shave", 20000);
        priceMap.put("Colouring", 70000);
        Map<String, Integer> stylistFee = new HashMap<>();
        stylistFee.put("Indra Farhan", 10000);
        stylistFee.put("Raffle Alghifari", 15000);
        stylistFee.put("Reza Faishal", 12000);
        stylistFee.put("hendyw", 8000);
        int total = 0;
        List<ServiceDetail> serviceDetails = new ArrayList<>();
        if (services != null) {
            for (String s : services) {
                int price = priceMap.getOrDefault(s, 0);
                total += price;
                serviceDetails.add(new ServiceDetail(s, price));
            }
        }
        int fee = stylistFee.getOrDefault(hairstylist, 0);
        total += fee;
        model.addAttribute("hairstylist", hairstylist);
        model.addAttribute("services", services);
        model.addAttribute("appointmentDate", appointmentDate);
        model.addAttribute("appointmentTime", appointmentTime);
        model.addAttribute("total", total);
        model.addAttribute("priceMap", priceMap);
        model.addAttribute("fee", fee);
        model.addAttribute("serviceDetails", serviceDetails);
        session.setAttribute("appointmentDate", appointmentDate);
        session.setAttribute("appointmentTime", appointmentTime);
        session.setAttribute("total", total);
        session.setAttribute("priceMap", priceMap);
        session.setAttribute("fee", fee);
        session.setAttribute("serviceDetails", serviceDetails);
        return "payment";
    }

    @PostMapping("/payment-success")
    public String paymentSuccess(HttpSession session) {
        // Simpan transaksi ke database
        String hairstylist = (String) session.getAttribute("hairstylist");
        List<String> services = (List<String>) session.getAttribute("services");
        String appointmentDate = (String) session.getAttribute("appointmentDate");
        String appointmentTime = (String) session.getAttribute("appointmentTime");
        Integer total = (Integer) session.getAttribute("total");
        // Ambil user dari security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            Transaction trx = new Transaction();
            trx.setUser(userOpt.get());
            trx.setHairstylist(hairstylist);
            trx.setServices(String.join(",", services));
            trx.setAppointmentDate(LocalDate.parse(appointmentDate));
            trx.setAppointmentTime(LocalTime.parse(appointmentTime));
            trx.setPrice(total.doubleValue());
            trx.setStatus("COMPLETED");
            trx.setCreatedAt(LocalDateTime.now());
            transactionRepository.save(trx);
        }
        return "payment-success";
    }

    @PostMapping("/receipt")
    public String showReceipt(HttpSession session, Model model) {
        String hairstylist = (String) session.getAttribute("hairstylist");
        List<String> services = (List<String>) session.getAttribute("services");
        String appointmentDate = (String) session.getAttribute("appointmentDate");
        String appointmentTime = (String) session.getAttribute("appointmentTime");
        Integer total = (Integer) session.getAttribute("total");
        Map<String, Integer> priceMap = (Map<String, Integer>) session.getAttribute("priceMap");
        Integer fee = (Integer) session.getAttribute("fee");
        List<ServiceDetail> serviceDetails = (List<ServiceDetail>) session.getAttribute("serviceDetails");
        model.addAttribute("hairstylist", hairstylist);
        model.addAttribute("services", services);
        model.addAttribute("appointmentDate", appointmentDate);
        model.addAttribute("appointmentTime", appointmentTime);
        model.addAttribute("total", total);
        model.addAttribute("priceMap", priceMap);
        model.addAttribute("fee", fee);
        model.addAttribute("serviceDetails", serviceDetails);
        return "receipt";
    }
} 