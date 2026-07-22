package com.pe.eph.dashboard.service;

import com.pe.eph.dashboard.dto.response.DashboardResponse;
import com.pe.eph.dashboard.dto.response.TrainerDashboardResponse;

public interface DashboardService {

    DashboardResponse getDashboardStats();

    TrainerDashboardResponse getTrainerDashboardStats(String phone);

}
