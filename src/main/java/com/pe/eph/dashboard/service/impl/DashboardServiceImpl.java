package com.pe.eph.dashboard.service.impl;

import com.pe.eph.booking.entity.PTBooking;
import com.pe.eph.booking.repository.ClassBookingRepository;
import com.pe.eph.booking.repository.PTBookingRepository;
import com.pe.eph.checkin.entity.CheckInLog;
import com.pe.eph.checkin.repository.CheckInLogRepository;
import com.pe.eph.common.enums.MemberStatus;
import com.pe.eph.common.enums.PTBookingStatus;
import com.pe.eph.common.enums.PaymentStatus;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.dashboard.dto.response.DashboardResponse;
import com.pe.eph.dashboard.dto.response.DashboardResponse.*;
import com.pe.eph.dashboard.dto.response.TrainerDashboardResponse;
import com.pe.eph.dashboard.dto.response.TrainerDashboardResponse.NewMemberDto;
import com.pe.eph.dashboard.service.DashboardService;
import com.pe.eph.gympackage.repository.GymPackageRepository;
import com.pe.eph.member.entity.Member;
import com.pe.eph.member.repository.MemberRepository;
import com.pe.eph.payment.entity.Payment;
import com.pe.eph.payment.repository.PaymentRepository;
import com.pe.eph.trainer.entity.Trainer;
import com.pe.eph.trainer.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;
    private final GymPackageRepository gymPackageRepository;
    private final PaymentRepository paymentRepository;
    private final CheckInLogRepository checkInLogRepository;
    private final PTBookingRepository ptBookingRepository;
    private final ClassBookingRepository classBookingRepository;

    @Override
    public DashboardResponse getDashboardStats() {
        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.countByStatus(MemberStatus.ACTIVE);
        long expiredMembers = memberRepository.countByStatus(MemberStatus.EXPIRED);
        long totalTrainers = trainerRepository.count();
        long totalPackages = gymPackageRepository.count();
        long membersPresent = checkInLogRepository.countByCheckOutTimeIsNull();

        LocalDate now = LocalDate.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59, 999999999);

        BigDecimal monthlyRevenue = paymentRepository.sumAmountByPaymentStatusAndPaymentDateBetween(
                PaymentStatus.PAID,
                startOfMonth,
                endOfMonth
        );

        List<ChartDataPoint> revenueChartData = new ArrayList<>();
        LocalDateTime startOfYear = now.withDayOfYear(1).atStartOfDay();
        List<Payment> yearPayments = paymentRepository.findByPaymentStatusAndPaymentDateAfter(
                PaymentStatus.PAID,
                startOfYear
        );
        for (int m = 1; m <= 12; m++) {
            final int monthVal = m;
            BigDecimal monthlySum = yearPayments.stream()
                    .filter(p -> p.getPaymentDate().getMonthValue() == monthVal)
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            revenueChartData.add(ChartDataPoint.builder()
                    .label("Thg " + m)
                    .value(monthlySum)
                    .build());
        }

        List<ChartDataPoint> checkInHourChartData = new ArrayList<>();
        List<ChartDataPoint> checkInDayChartData = new ArrayList<>();
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<CheckInLog> recentLogs = checkInLogRepository.findByCheckInTimeAfter(thirtyDaysAgo);

        String[] hourSlots = {
                "06:00-08:00", "08:00-10:00", "10:00-12:00", "12:00-14:00",
                "14:00-16:00", "16:00-18:00", "18:00-20:00", "20:00-22:00"
        };
        Map<String, Long> hourCounts = new LinkedHashMap<>();
        for (String slot : hourSlots) {
            hourCounts.put(slot, 0L);
        }
        for (CheckInLog log : recentLogs) {
            int hr = log.getCheckInTime().getHour();
            String matchedSlot = null;
            if (hr >= 6 && hr < 8) matchedSlot = "06:00-08:00";
            else if (hr >= 8 && hr < 10) matchedSlot = "08:00-10:00";
            else if (hr >= 10 && hr < 12) matchedSlot = "10:00-12:00";
            else if (hr >= 12 && hr < 14) matchedSlot = "12:00-14:00";
            else if (hr >= 14 && hr < 16) matchedSlot = "14:00-16:00";
            else if (hr >= 16 && hr < 18) matchedSlot = "16:00-18:00";
            else if (hr >= 18 && hr < 20) matchedSlot = "18:00-20:00";
            else if (hr >= 20 && hr < 22) matchedSlot = "20:00-22:00";

            if (matchedSlot != null) {
                hourCounts.put(matchedSlot, hourCounts.get(matchedSlot) + 1);
            }
        }
        for (Map.Entry<String, Long> entry : hourCounts.entrySet()) {
            checkInHourChartData.add(ChartDataPoint.builder()
                    .label(entry.getKey())
                    .value(BigDecimal.valueOf(entry.getValue()))
                    .build());
        }

        String[] dayLabels = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"};
        Map<DayOfWeek, String> dayOfWeekMap = new HashMap<>();
        dayOfWeekMap.put(DayOfWeek.MONDAY, "Thứ 2");
        dayOfWeekMap.put(DayOfWeek.TUESDAY, "Thứ 3");
        dayOfWeekMap.put(DayOfWeek.WEDNESDAY, "Thứ 4");
        dayOfWeekMap.put(DayOfWeek.THURSDAY, "Thứ 5");
        dayOfWeekMap.put(DayOfWeek.FRIDAY, "Thứ 6");
        dayOfWeekMap.put(DayOfWeek.SATURDAY, "Thứ 7");
        dayOfWeekMap.put(DayOfWeek.SUNDAY, "Chủ Nhật");

        Map<String, Long> dayCounts = new LinkedHashMap<>();
        for (String dl : dayLabels) {
            dayCounts.put(dl, 0L);
        }
        for (CheckInLog log : recentLogs) {
            DayOfWeek dow = log.getCheckInTime().getDayOfWeek();
            String dl = dayOfWeekMap.get(dow);
            if (dl != null) {
                dayCounts.put(dl, dayCounts.get(dl) + 1);
            }
        }
        for (Map.Entry<String, Long> entry : dayCounts.entrySet()) {
            checkInDayChartData.add(ChartDataPoint.builder()
                    .label(entry.getKey())
                    .value(BigDecimal.valueOf(entry.getValue()))
                    .build());
        }

        List<PackageStatDto> topPackages = new ArrayList<>();
        List<Object[]> packageStats = memberRepository.countActiveMembersByPackage();
        for (Object[] stat : packageStats) {
            topPackages.add(PackageStatDto.builder()
                    .packageName(stat[0] != null ? stat[0].toString() : "Không tên")
                    .count((Long) stat[1])
                    .build());
        }

        long newMembersThisMonth = memberRepository.countByJoinDateBetween(startOfMonth.toLocalDate(), endOfMonth.toLocalDate());
        List<Payment> monthPayments = paymentRepository.findByPaymentStatusAndPaymentDateBetween(
                PaymentStatus.PAID,
                startOfMonth,
                endOfMonth
        );
        long renewalMembersThisMonth = monthPayments.stream()
                .map(Payment::getMember)
                .filter(Objects::nonNull)
                .filter(m -> m.getJoinDate() != null && m.getJoinDate().isBefore(startOfMonth.toLocalDate()))
                .map(Member::getId)
                .distinct()
                .count();
        double renewalRate = 0.0;
        long totalActionMembers = newMembersThisMonth + renewalMembersThisMonth;
        if (totalActionMembers > 0) {
            renewalRate = (double) (renewalMembersThisMonth * 100.0) / totalActionMembers;
        }

        List<ExpiringMemberDto> expiringMembers = new ArrayList<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Member> expMembers = memberRepository.findByStatusAndExpireDateBetween(
                MemberStatus.ACTIVE,
                LocalDate.now(),
                LocalDate.now().plusDays(7)
        );
        for (Member m : expMembers) {
            expiringMembers.add(ExpiringMemberDto.builder()
                    .fullName(m.getFullName())
                    .memberCode(m.getMemberCode())
                    .phone(m.getPhone())
                    .expireDate(m.getExpireDate() != null ? m.getExpireDate().format(df) : "--")
                    .build());
        }

        List<RecentCheckInDto> recentCheckIns = new ArrayList<>();
        List<CheckInLog> topLogs = checkInLogRepository.findTop5ByOrderByCheckInTimeDesc();
        DateTimeFormatter tdf = DateTimeFormatter.ofPattern("HH:mm dd/MM");
        for (CheckInLog log : topLogs) {
            recentCheckIns.add(RecentCheckInDto.builder()
                    .fullName(log.getMember().getFullName())
                    .memberCode(log.getMember().getMemberCode())
                    .checkInTime(log.getCheckInTime().format(tdf))
                    .packageName(log.getMember().getGymPackage() != null ? log.getMember().getGymPackage().getPackageName() : "Tự tập")
                    .build());
        }

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        long todayPtBookings = ptBookingRepository.countBySessionTimeBetween(todayStart, todayEnd);
        long todayClassBookings = classBookingRepository.countTodayBookings(todayStart, todayEnd);

        List<PtAppointmentDto> todayPtAppointments = new ArrayList<>();
        List<PTBooking> ptsToday = ptBookingRepository.findBySessionTimeBetween(todayStart, todayEnd);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for (PTBooking ptb : ptsToday) {
            todayPtAppointments.add(PtAppointmentDto.builder()
                    .id(ptb.getId())
                    .memberName(ptb.getMember().getFullName())
                    .memberPhone(ptb.getMember().getPhone())
                    .trainerName(ptb.getTrainer().getFullName())
                    .sessionTime(ptb.getSessionTime().format(timeFormatter))
                    .status(ptb.getStatus())
                    .build());
        }

        return DashboardResponse.builder()
                .totalMembers(totalMembers)
                .activeMembers(activeMembers)
                .expiredMembers(expiredMembers)
                .totalTrainers(totalTrainers)
                .totalPackages(totalPackages)
                .monthlyRevenue(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO)
                .membersPresent(membersPresent)
                .revenueChartData(revenueChartData)
                .checkInHourChartData(checkInHourChartData)
                .checkInDayChartData(checkInDayChartData)
                .topPackages(topPackages)
                .newMembersThisMonth(newMembersThisMonth)
                .renewalMembersThisMonth(renewalMembersThisMonth)
                .renewalRate(Math.round(renewalRate * 10.0) / 10.0)
                .expiringMembers(expiringMembers)
                .recentCheckIns(recentCheckIns)
                .todayPtBookings(todayPtBookings)
                .todayClassBookings(todayClassBookings)
                .todayPtAppointments(todayPtAppointments)
                .build();
    }

    @Override
    public TrainerDashboardResponse getTrainerDashboardStats(String phone) {
        Trainer trainer = trainerRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy huấn luyện viên."));

        long totalMembers = memberRepository.countByTrainer(trainer);
        long completedSessions = ptBookingRepository.countByTrainerAndStatus(trainer, PTBookingStatus.COMPLETED);
        long scheduledSessions = ptBookingRepository.countByTrainerAndStatus(trainer, PTBookingStatus.SCHEDULED);

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

        List<PTBooking> ptsToday = ptBookingRepository.findByTrainerAndSessionTimeBetween(trainer, todayStart, todayEnd);
        List<PtAppointmentDto> todayPtAppointments = new ArrayList<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for (PTBooking ptb : ptsToday) {
            todayPtAppointments.add(PtAppointmentDto.builder()
                    .id(ptb.getId())
                    .memberName(ptb.getMember().getFullName())
                    .memberPhone(ptb.getMember().getPhone())
                    .trainerName(ptb.getTrainer().getFullName())
                    .sessionTime(ptb.getSessionTime().format(timeFormatter))
                    .status(ptb.getStatus())
                    .build());
        }

        List<Member> newMembers = memberRepository.findTop5ByTrainerOrderByJoinDateDesc(trainer);
        List<NewMemberDto> newAssignedMembers = new ArrayList<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Member m : newMembers) {
            newAssignedMembers.add(NewMemberDto.builder()
                    .fullName(m.getFullName())
                    .memberCode(m.getMemberCode())
                    .phone(m.getPhone())
                    .joinDate(m.getJoinDate() != null ? m.getJoinDate().format(df) : "--")
                    .packageName(m.getGymPackage() != null ? m.getGymPackage().getPackageName() : "Chưa đăng ký")
                    .build());
        }

        return TrainerDashboardResponse.builder()
                .totalMembers(totalMembers)
                .completedSessions(completedSessions)
                .scheduledSessions(scheduledSessions)
                .todayPtAppointments(todayPtAppointments)
                .newAssignedMembers(newAssignedMembers)
                .build();
    }
}

