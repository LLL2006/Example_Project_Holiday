package com.pe.eph.dashboard.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerDashboardResponse {

    private long totalMembers;
    private long completedSessions;
    private long scheduledSessions;
    private List<DashboardResponse.PtAppointmentDto> todayPtAppointments;
    private List<NewMemberDto> newAssignedMembers;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NewMemberDto {
        private String fullName;
        private String memberCode;
        private String phone;
        private String joinDate; // dd/MM/yyyy
        private String packageName;
    }
}

