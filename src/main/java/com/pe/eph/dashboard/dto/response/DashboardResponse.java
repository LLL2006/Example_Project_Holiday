package com.pe.eph.dashboard.dto.response;

import com.pe.eph.common.enums.PTBookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private long totalMembers;
    private long activeMembers;
    private long expiredMembers;
    private long totalTrainers;
    private long totalPackages;
    private BigDecimal monthlyRevenue;
    private long membersPresent;

    private List<ChartDataPoint> revenueChartData;
    private List<ChartDataPoint> checkInHourChartData;
    private List<ChartDataPoint> checkInDayChartData;
    private List<PackageStatDto> topPackages;

    private long newMembersThisMonth;
    private long renewalMembersThisMonth;
    private double renewalRate;

    private List<ExpiringMemberDto> expiringMembers;
    private List<RecentCheckInDto> recentCheckIns;
    private long todayPtBookings;
    private long todayClassBookings;
    private List<PtAppointmentDto> todayPtAppointments;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChartDataPoint {
        private String label;
        private BigDecimal value;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PackageStatDto {
        private String packageName;
        private long count;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExpiringMemberDto {
        private String fullName;
        private String memberCode;
        private String phone;
        private String expireDate; // dd/MM/yyyy
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentCheckInDto {
        private String fullName;
        private String memberCode;
        private String checkInTime; // HH:mm dd/MM;
        private String packageName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PtAppointmentDto {
        private Long id;
        private String memberName;
        private String memberPhone;
        private String trainerName;
        private String sessionTime; // HH:mm
        private PTBookingStatus status;
    }
}

