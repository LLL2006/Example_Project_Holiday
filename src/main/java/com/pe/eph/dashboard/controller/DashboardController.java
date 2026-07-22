package com.pe.eph.dashboard.controller;

import com.pe.eph.common.exception.BadRequestException;
import com.pe.eph.dashboard.dto.response.DashboardResponse;
import com.pe.eph.dashboard.dto.response.TrainerDashboardResponse;
import com.pe.eph.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardResponse getDashboardStats() {
        return dashboardService.getDashboardStats();
    }

    @GetMapping("/trainer")
    public TrainerDashboardResponse getTrainerStats(Authentication authentication) {
        if (authentication == null) {
            throw new BadRequestException("Chưa đăng nhập");
        }
        return dashboardService.getTrainerDashboardStats(authentication.getName());
    }
}
