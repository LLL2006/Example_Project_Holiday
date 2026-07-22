package com.pe.eph.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/members")
    public String membersList() {
        return "members/list";
    }

    @GetMapping("/members/add")
    public String membersAdd(Model model) {
        model.addAttribute("isEdit", false);
        return "members/form";
    }

    @GetMapping("/members/edit/{id}")
    public String membersEdit(@PathVariable Long id, Model model) {
        model.addAttribute("isEdit", true);
        model.addAttribute("memberId", id);
        return "members/form";
    }

    @GetMapping("/trainers")
    public String trainersList() {
        return "trainers/list";
    }

    @GetMapping("/packages")
    public String packagesList() {
        return "packages/list";
    }

    @GetMapping("/packages/buy")
    public String buyPackage() {
        return "packages/buy";
    }

    @GetMapping("/payments")
    public String paymentsList() {
        return "payments/list";
    }


    @GetMapping("/bookings")
    public String bookingsIndex() {
        return "bookings/index";
    }

    @GetMapping("/checkin")
    public String checkinIndex() {
        return "checkin/index";
    }

    @GetMapping("/equipments")
    public String equipmentsList() {
        return "equipments/list";
    }

    @GetMapping("/schedules")
    public String schedulesList() {
        return "bookings/schedules";
    }
}

