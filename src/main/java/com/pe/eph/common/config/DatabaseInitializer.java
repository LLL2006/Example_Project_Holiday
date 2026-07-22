package com.pe.eph.common.config;

import com.pe.eph.common.enums.ClassBookingStatus;
import com.pe.eph.common.enums.Gender;
import com.pe.eph.common.enums.MemberStatus;
import com.pe.eph.common.enums.PTBookingStatus;
import com.pe.eph.common.enums.PackageStatus;
import com.pe.eph.common.enums.PaymentMethod;
import com.pe.eph.common.enums.PaymentStatus;
import com.pe.eph.common.enums.UserStatus;
import com.pe.eph.role.entity.Role;
import com.pe.eph.role.repository.RoleRepository;
import com.pe.eph.user.entity.User;
import com.pe.eph.user.repository.UserRepository;
import com.pe.eph.trainer.entity.Trainer;
import com.pe.eph.trainer.repository.TrainerRepository;
import com.pe.eph.gympackage.entity.GymPackage;
import com.pe.eph.gympackage.repository.GymPackageRepository;
import com.pe.eph.member.entity.Member;
import com.pe.eph.member.repository.MemberRepository;
import com.pe.eph.payment.entity.Payment;
import com.pe.eph.payment.repository.PaymentRepository;
import com.pe.eph.checkin.entity.CheckInLog;
import com.pe.eph.checkin.repository.CheckInLogRepository;
import com.pe.eph.booking.entity.GymClass;
import com.pe.eph.booking.entity.ClassSchedule;
import com.pe.eph.booking.entity.ClassBooking;
import com.pe.eph.booking.entity.PTBooking;
import com.pe.eph.booking.repository.GymClassRepository;
import com.pe.eph.booking.repository.ClassScheduleRepository;
import com.pe.eph.booking.repository.ClassBookingRepository;
import com.pe.eph.booking.repository.PTBookingRepository;
import com.pe.eph.equipment.entity.Equipment;
import com.pe.eph.equipment.entity.EquipmentStatus;
import com.pe.eph.equipment.repository.EquipmentRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final TrainerRepository trainerRepository;
    private final GymPackageRepository gymPackageRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final CheckInLogRepository checkInLogRepository;
    private final GymClassRepository gymClassRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final ClassBookingRepository classBookingRepository;
    private final PTBookingRepository ptBookingRepository;
    private final EquipmentRepository equipmentRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting database initialization...");

        String[] alterStatements = {
            "ALTER TABLE members MODIFY COLUMN expire_date DATE NULL",
            "ALTER TABLE members MODIFY COLUMN package_id BIGINT NULL",
            "ALTER TABLE members MODIFY COLUMN trainer_id BIGINT NULL",
            "ALTER TABLE members MODIFY COLUMN user_id BIGINT NULL"
        };

        for (String sql : alterStatements) {
            try {
                log.info("Executing schema update: {}", sql);
                jdbcTemplate.execute(sql);
                log.info("Successfully executed: {}", sql);
            } catch (Exception e) {
                log.warn("Could not execute: {}. Error: {}", sql, e.getMessage());
            }
        }

        List<String> requiredRoles = Arrays.asList("ADMIN", "TRAINER", "MEMBER");
        for (String roleName : requiredRoles) {

            if (!roleRepository.existsByRoleName(roleName)) {
                Role role = Role.builder()
                        .roleName(roleName)
                        .description("Vai trò " + roleName)
                        .build();
                roleRepository.save(role);
                log.info("Created role: {}", roleName);
            }
        }

        String adminUsername = "admin";
        if (!userRepository.existsByUsername(adminUsername)) {
            Role adminRole = roleRepository.findByRoleName("ADMIN")
                    .orElseThrow(() -> new IllegalStateException("ADMIN role must exist."));

            User adminUser = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode("123456"))
                    .fullName("System Administrator")
                    .email("admin@smartgym.com")
                    .phone("0999999999")
                    .status(UserStatus.ACTIVE)
                    .role(adminRole)
                    .build();

            userRepository.save(adminUser);
            log.info("====================================================");
            log.info("DEFAULT ADMIN USER INITIALIZED SUCCESSFULLY!");
            log.info("Username: {}", adminUsername);
            log.info("Password: 123456");
            log.info("====================================================");
        } else {
            log.info("Admin user already exists. Skipping creation.");
        }

        seedMockData();

        log.info("Database initialization completed.");
    }

    private void seedMockData() {
        if (gymPackageRepository.count() == 0) {
            log.info("Seeding Gym Packages...");
            gymPackageRepository.save(GymPackage.builder()
                    .packageCode("PKG-1M")
                    .packageName("Gói 1 Tháng Thường")
                    .duration(1)
                    .price(BigDecimal.valueOf(500000))
                    .description("Tập luyện tự do trong 1 tháng")
                    .status(PackageStatus.ACTIVE)
                    .ptSessions(0)
                    .build());

            gymPackageRepository.save(GymPackage.builder()
                    .packageCode("PKG-3M")
                    .packageName("Gói 3 Tháng Thường")
                    .duration(3)
                    .price(BigDecimal.valueOf(1200000))
                    .description("Tập luyện tự do trong 3 tháng")
                    .status(PackageStatus.ACTIVE)
                    .ptSessions(0)
                    .build());

            gymPackageRepository.save(GymPackage.builder()
                    .packageCode("PKG-12M")
                    .packageName("Gói 12 Tháng VIP")
                    .duration(12)
                    .price(BigDecimal.valueOf(4500000))
                    .description("Tập luyện tự do trong 1 năm + Tặng 5 ca tập với PT")
                    .status(PackageStatus.ACTIVE)
                    .ptSessions(5)
                    .build());

            gymPackageRepository.save(GymPackage.builder()
                    .packageCode("PKG-PT20")
                    .packageName("Gói PT Cá Nhân 20 Ca")
                    .duration(6)
                    .price(BigDecimal.valueOf(8000000))
                    .description("20 Ca tập 1 kèm 1 với Huấn luyện viên")
                    .status(PackageStatus.ACTIVE)
                    .ptSessions(20)
                    .build());
            log.info("Gym Packages seeded.");
        }

        Role trainerRole = roleRepository.findByRoleName("TRAINER").orElse(null);
        if (trainerRepository.count() == 0 && trainerRole != null) {
            Trainer t1 = trainerRepository.save(Trainer.builder()
                    .trainerCode("PT001")
                    .fullName("Nguyễn Văn Hùng")
                    .specialization("Thể hình & Giảm cân")
                    .yearsOfExperience(5)
                    .phone("0912345678")
                    .email("hung.nv@smartgym.com")
                    .gender(Gender.MALE)
                    .status(UserStatus.ACTIVE)
                    .build());

            Trainer t2 = trainerRepository.save(Trainer.builder()
                    .trainerCode("PT002")
                    .fullName("Trần Thị Lan")
                    .specialization("Yoga & Pilates")
                    .yearsOfExperience(3)
                    .phone("0923456789")
                    .email("lan.tt@smartgym.com")
                    .gender(Gender.FEMALE)
                    .status(UserStatus.ACTIVE)
                    .build());

            Trainer t3 = trainerRepository.save(Trainer.builder()
                    .trainerCode("PT003")
                    .fullName("Lê Hoàng Nam")
                    .specialization("Tăng cơ & Siết mỡ")
                    .yearsOfExperience(4)
                    .phone("0934567890")
                    .email("nam.lh@smartgym.com")
                    .gender(Gender.MALE)
                    .status(UserStatus.ACTIVE)
                    .build());

            for (Trainer t : Arrays.asList(t1, t2, t3)) {
                if (!userRepository.existsByUsername(t.getPhone())) {
                    userRepository.save(User.builder()
                            .username(t.getPhone())
                            .password(passwordEncoder.encode("123456"))
                            .fullName(t.getFullName())
                            .email(t.getEmail())
                            .phone(t.getPhone())
                            .status(UserStatus.ACTIVE)
                            .role(trainerRole)
                            .build());
                }
            }
            log.info("Trainers seeded.");
        } else {
            if (trainerRole != null) {
                List<Trainer> trainers = trainerRepository.findAll();
                for (Trainer trainer : trainers) {
                    if (!userRepository.existsByUsername(trainer.getPhone())) {
                        userRepository.save(User.builder()
                                .username(trainer.getPhone())
                                .password(passwordEncoder.encode("123456"))
                                .fullName(trainer.getFullName())
                                .email(trainer.getEmail())
                                .phone(trainer.getPhone())
                                .status(UserStatus.ACTIVE)
                                .role(trainerRole)
                                .build());
                    }
                }
            }
        }

        Role memberRole = roleRepository.findByRoleName("MEMBER").orElse(null);
        if (memberRepository.count() == 0 && memberRole != null) {
            log.info("Seeding Members...");
            List<GymPackage> packages = gymPackageRepository.findAll();
            List<Trainer> trainers = trainerRepository.findAll();
            
            GymPackage pkg1M = packages.stream().filter(p -> p.getPackageCode().equals("PKG-1M")).findFirst().orElse(null);
            GymPackage pkg3M = packages.stream().filter(p -> p.getPackageCode().equals("PKG-3M")).findFirst().orElse(null);
            GymPackage pkg12M = packages.stream().filter(p -> p.getPackageCode().equals("PKG-12M")).findFirst().orElse(null);
            GymPackage pkgPt20 = packages.stream().filter(p -> p.getPackageCode().equals("PKG-PT20")).findFirst().orElse(null);
            
            Trainer pt1 = trainers.isEmpty() ? null : trainers.get(0);
            Trainer pt3 = trainers.size() >= 3 ? trainers.get(2) : pt1;

            String[] names = {
                "Lê Minh Tuấn", "Phạm Thanh Hà", "Nguyễn Hải Đăng", "Vũ Thùy Chi",
                "Hoàng Đức Anh", "Đỗ Bảo Ngọc", "Nguyễn Bích Phương", "Bùi Tiến Dũng",
                "Vũ Minh Anh", "Nguyễn Trung Kiên"
            };
            String[] phones = {
                "0987000001", "0987000002", "0987000003", "0987000004",
                "0987000005", "0987000006", "0987000007", "0987000008",
                "0987000009", "0987000010"
            };
            Gender[] genders = {
                Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE,
                Gender.MALE, Gender.FEMALE, Gender.FEMALE, Gender.MALE,
                Gender.FEMALE, Gender.MALE
            };
            GymPackage[] memberPkgs = {
                pkg12M, pkg3M, pkgPt20, pkg1M,
                pkg1M, pkg3M, pkg1M, pkg3M,
                pkg12M, pkg1M
            };
            MemberStatus[] statuses = {
                MemberStatus.ACTIVE, MemberStatus.ACTIVE, MemberStatus.ACTIVE, MemberStatus.ACTIVE,
                MemberStatus.ACTIVE, MemberStatus.ACTIVE, MemberStatus.EXPIRED, MemberStatus.ACTIVE,
                MemberStatus.ACTIVE, MemberStatus.ACTIVE
            };
            LocalDate today = LocalDate.now();
            LocalDate[] joinDates = {
                today.minusMonths(3), today.minusMonths(1), today.minusWeeks(2), today.minusDays(3),
                today.minusDays(27), today.minusDays(85), today.minusDays(45), today.minusMonths(2),
                today.minusMonths(5), today.minusDays(20)
            };
            LocalDate[] expireDates = {
                joinDates[0].plusMonths(12), joinDates[1].plusMonths(3), joinDates[2].plusMonths(6), joinDates[3].plusMonths(1),
                today.plusDays(3), today.plusDays(5), today.minusDays(15), today.plusMonths(1),
                joinDates[8].plusMonths(12), today.plusDays(10)
            };

            for (int i = 0; i < names.length; i++) {
                User memberUser = User.builder()
                        .username(phones[i])
                        .password(passwordEncoder.encode("123456"))
                        .fullName(names[i])
                        .email(phones[i] + "@gmail.com")
                        .phone(phones[i])
                        .status(UserStatus.ACTIVE)
                        .role(memberRole)
                        .build();

                Member member = Member.builder()
                        .memberCode("MEM" + String.format("%03d", i + 1))
                        .fullName(names[i])
                        .gender(genders[i])
                        .phone(phones[i])
                        .email(phones[i] + "@gmail.com")
                        .address("Hà Nội")
                        .joinDate(joinDates[i])
                        .startDate(joinDates[i])
                        .expireDate(expireDates[i])
                        .status(statuses[i])
                        .gymPackage(memberPkgs[i])
                        .remainingPtSessions(memberPkgs[i] != null ? memberPkgs[i].getPtSessions() : 0)
                        .trainer(memberPkgs[i] == pkgPt20 ? pt1 : (memberPkgs[i] == pkg12M ? pt3 : null))
                        .user(memberUser)
                        .build();

                memberRepository.save(member);
            }
            log.info("Members seeded.");
        }

        List<Member> dbMembers = memberRepository.findAll();
        List<GymPackage> packages = gymPackageRepository.findAll();
        List<Trainer> trainers = trainerRepository.findAll();
        LocalDate today = LocalDate.now();
        Random rand = new Random();

        if (paymentRepository.count() == 0 && !dbMembers.isEmpty()) {
            log.info("Seeding Payments...");
            int invoiceSeq = 1000;
            for (Member m : dbMembers) {
                if (m.getGymPackage() != null) {
                    LocalDateTime paymentDateTime = m.getJoinDate().atTime(10, 30);
                    paymentRepository.save(Payment.builder()
                            .invoiceCode("INV" + invoiceSeq++)
                            .member(m)
                            .gymPackage(m.getGymPackage())
                            .amount(m.getGymPackage().getPrice())
                            .remainingValue(BigDecimal.ZERO)
                            .paymentMethod(rand.nextBoolean() ? PaymentMethod.BANK_TRANSFER : PaymentMethod.CASH)
                            .paymentStatus(PaymentStatus.PAID)
                            .paymentDate(paymentDateTime)
                            .note("Thanh toán gói " + m.getGymPackage().getPackageName())
                            .build());
                }
            }

            for (int month = 1; month < today.getMonthValue(); month++) {
                int extraCount = rand.nextInt(3) + 2;
                for (int count = 0; count < extraCount; count++) {
                    GymPackage pkg = packages.get(rand.nextInt(packages.size()));
                    Member memberObj = dbMembers.get(rand.nextInt(dbMembers.size()));
                    LocalDateTime paymentDateTime = today.withMonth(month).withDayOfMonth(rand.nextInt(25) + 1).atTime(9, 15);
                    
                    paymentRepository.save(Payment.builder()
                            .invoiceCode("INV" + invoiceSeq++)
                            .member(memberObj)
                            .gymPackage(pkg)
                            .amount(pkg.getPrice())
                            .remainingValue(BigDecimal.ZERO)
                            .paymentMethod(PaymentMethod.BANK_TRANSFER)
                            .paymentStatus(PaymentStatus.PAID)
                            .paymentDate(paymentDateTime)
                            .note("Đăng ký thêm dịch vụ")
                            .build());
                }
            }
            log.info("Payments seeded.");
        }

        if (checkInLogRepository.count() == 0 && !dbMembers.isEmpty()) {
            log.info("Seeding Check-In Logs...");
            for (int day = 0; day < 30; day++) {
                LocalDate checkInDate = today.minusDays(day);
                int logsCount = rand.nextInt(4) + 3;
                for (int c = 0; c < logsCount; c++) {
                    Member memberObj = dbMembers.get(rand.nextInt(dbMembers.size()));
                    if (memberObj.getStatus() == MemberStatus.ACTIVE) {
                        int checkInHour = rand.nextBoolean() ? (rand.nextInt(2) + 6) : (rand.nextInt(3) + 17);
                        int checkInMin = rand.nextInt(60);
                        LocalDateTime checkInTime = checkInDate.atTime(checkInHour, checkInMin);
                        LocalDateTime checkOutTime = checkInTime.plusHours(1).plusMinutes(rand.nextInt(45) + 15);

                        checkInLogRepository.save(CheckInLog.builder()
                                .member(memberObj)
                                .checkInTime(checkInTime)
                                .checkOutTime(checkOutTime)
                                .photoPath("/images/default-avatar.png")
                                .build());
                    }
                }
            }
            if (!dbMembers.isEmpty()) {
                checkInLogRepository.save(CheckInLog.builder()
                        .member(dbMembers.get(0))
                        .checkInTime(LocalDateTime.now().minusMinutes(45))
                        .checkOutTime(null)
                        .photoPath("/images/default-avatar.png")
                        .build());
            }
            if (dbMembers.size() >= 3) {
                checkInLogRepository.save(CheckInLog.builder()
                        .member(dbMembers.get(2))
                        .checkInTime(LocalDateTime.now().minusMinutes(20))
                        .checkOutTime(null)
                        .photoPath("/images/default-avatar.png")
                        .build());
            }
            if (dbMembers.size() >= 4) {
                checkInLogRepository.save(CheckInLog.builder()
                        .member(dbMembers.get(3))
                        .checkInTime(LocalDateTime.now().minusMinutes(10))
                        .checkOutTime(null)
                        .photoPath("/images/default-avatar.png")
                        .build());
            }
            log.info("Check-In Logs seeded.");
        }

        if (gymClassRepository.count() == 0 && !trainers.isEmpty()) {
            log.info("Seeding Gym Classes & Bookings...");
            Trainer pt1Obj = trainers.get(0);
            Trainer pt2Obj = trainers.size() >= 2 ? trainers.get(1) : pt1Obj;

            GymClass zumba = gymClassRepository.save(GymClass.builder()
                    .className("Zumba Dance")
                    .trainer(pt2Obj)
                    .capacity(15)
                    .status("ACTIVE")
                    .build());

            GymClass yoga = gymClassRepository.save(GymClass.builder()
                    .className("Yoga Core")
                    .trainer(pt2Obj)
                    .capacity(20)
                    .status("ACTIVE")
                    .build());

            GymClass cardio = gymClassRepository.save(GymClass.builder()
                    .className("Cardio Intense")
                    .trainer(pt1Obj)
                    .capacity(25)
                    .status("ACTIVE")
                    .build());

            ClassSchedule s1 = classScheduleRepository.save(ClassSchedule.builder()
                    .gymClass(zumba)
                    .startTime(today.atTime(18, 0))
                    .endTime(today.atTime(19, 0))
                    .room("Phòng A")
                    .build());

            ClassSchedule s2 = classScheduleRepository.save(ClassSchedule.builder()
                    .gymClass(yoga)
                    .startTime(today.atTime(8, 30))
                    .endTime(today.atTime(9, 30))
                    .room("Phòng B")
                    .build());

            ClassSchedule s3 = classScheduleRepository.save(ClassSchedule.builder()
                    .gymClass(cardio)
                    .startTime(today.plusDays(1).atTime(9, 0))
                    .endTime(today.plusDays(1).atTime(10, 0))
                    .room("Phòng A")
                    .build());

            if (!dbMembers.isEmpty()) {
                classBookingRepository.save(ClassBooking.builder()
                        .member(dbMembers.get(0))
                        .classSchedule(s1)
                        .bookingTime(LocalDateTime.now().minusDays(1))
                        .status(ClassBookingStatus.BOOKED)
                        .build());
                if (dbMembers.size() >= 2) {
                    classBookingRepository.save(ClassBooking.builder()
                            .member(dbMembers.get(1))
                            .classSchedule(s1)
                            .bookingTime(LocalDateTime.now().minusDays(1))
                            .status(ClassBookingStatus.BOOKED)
                            .build());
                }
                if (dbMembers.size() >= 8) {
                    classBookingRepository.save(ClassBooking.builder()
                            .member(dbMembers.get(7))
                            .classSchedule(s1)
                            .bookingTime(LocalDateTime.now().minusDays(1))
                            .status(ClassBookingStatus.BOOKED)
                            .build());
                }
                if (dbMembers.size() >= 4) {
                    classBookingRepository.save(ClassBooking.builder()
                            .member(dbMembers.get(3))
                            .classSchedule(s2)
                            .bookingTime(LocalDateTime.now().minusDays(2))
                            .status(ClassBookingStatus.BOOKED)
                            .build());
                }
                if (dbMembers.size() >= 10) {
                    classBookingRepository.save(ClassBooking.builder()
                            .member(dbMembers.get(9))
                            .classSchedule(s2)
                            .bookingTime(LocalDateTime.now().minusDays(2))
                            .status(ClassBookingStatus.BOOKED)
                            .build());
                }
            }
            log.info("Gym Classes & Bookings seeded.");
        }

        if (ptBookingRepository.count() == 0 && !dbMembers.isEmpty() && !trainers.isEmpty()) {
            log.info("Seeding PT Bookings...");
            Trainer pt1Obj = trainers.get(0);
            Trainer pt3 = trainers.size() >= 3 ? trainers.get(2) : pt1Obj;

            if (dbMembers.size() >= 3) {
                ptBookingRepository.save(PTBooking.builder()
                        .member(dbMembers.get(2))
                        .trainer(pt1Obj)
                        .sessionTime(today.atTime(9, 0))
                        .status(PTBookingStatus.SCHEDULED)
                        .build());
            }
            if (dbMembers.size() >= 2) {
                ptBookingRepository.save(PTBooking.builder()
                        .member(dbMembers.get(1))
                        .trainer(pt3)
                        .sessionTime(today.atTime(15, 30))
                        .status(PTBookingStatus.SCHEDULED)
                        .build());
            }
            if (dbMembers.size() >= 9) {
                ptBookingRepository.save(PTBooking.builder()
                        .member(dbMembers.get(8))
                        .trainer(pt1Obj)
                        .sessionTime(today.atTime(18, 0))
                        .status(PTBookingStatus.SCHEDULED)
                        .build());
            }
            if (dbMembers.size() >= 3) {
                ptBookingRepository.save(PTBooking.builder()
                        .member(dbMembers.get(2))
                        .trainer(pt1Obj)
                        .sessionTime(today.minusDays(1).atTime(9, 0))
                        .status(PTBookingStatus.COMPLETED)
                        .build());
            }
            if (dbMembers.size() >= 9) {
                ptBookingRepository.save(PTBooking.builder()
                        .member(dbMembers.get(8))
                        .trainer(pt3)
                        .sessionTime(today.minusDays(2).atTime(14, 0))
                        .status(PTBookingStatus.COMPLETED)
                        .build());
            }
            log.info("PT Bookings seeded.");
        }

        if (equipmentRepository.count() == 0) {
            log.info("Seeding Gym Equipment...");
            equipmentRepository.save(Equipment.builder()
                    .equipmentCode("EQ001")
                    .name("Máy chạy bộ Treadmill Pro 1")
                    .type("Cardio")
                    .status(EquipmentStatus.OPERATIONAL)
                    .purchaseDate(LocalDate.now().minusMonths(12))
                    .lastMaintenanceDate(LocalDate.now().minusMonths(1))
                    .location("Tầng 1 - Khu Cardio")
                    .build());
            equipmentRepository.save(Equipment.builder()
                    .equipmentCode("EQ002")
                    .name("Máy chạy bộ Treadmill Pro 2")
                    .type("Cardio")
                    .status(EquipmentStatus.OPERATIONAL)
                    .purchaseDate(LocalDate.now().minusMonths(12))
                    .lastMaintenanceDate(LocalDate.now().minusMonths(1))
                    .location("Tầng 1 - Khu Cardio")
                    .build());
            equipmentRepository.save(Equipment.builder()
                    .equipmentCode("EQ003")
                    .name("Xe đạp tập Spin Bike 1")
                    .type("Cardio")
                    .status(EquipmentStatus.MAINTENANCE)
                    .purchaseDate(LocalDate.now().minusMonths(8))
                    .lastMaintenanceDate(LocalDate.now().minusDays(5))
                    .location("Tầng 1 - Khu Cardio")
                    .build());
            equipmentRepository.save(Equipment.builder()
                    .equipmentCode("EQ004")
                    .name("Giàn tạ đa năng Power Rack")
                    .type("Strength")
                    .status(EquipmentStatus.OPERATIONAL)
                    .purchaseDate(LocalDate.now().minusMonths(24))
                    .lastMaintenanceDate(LocalDate.now().minusMonths(3))
                    .location("Tầng 2 - Khu Strength")
                    .build());
            equipmentRepository.save(Equipment.builder()
                    .equipmentCode("EQ005")
                    .name("Máy ép ngực Chest Press")
                    .type("Strength")
                    .status(EquipmentStatus.BROKEN)
                    .purchaseDate(LocalDate.now().minusMonths(18))
                    .lastMaintenanceDate(LocalDate.now().minusMonths(2))
                    .location("Tầng 2 - Khu Strength")
                    .build());
            equipmentRepository.save(Equipment.builder()
                    .equipmentCode("EQ006")
                    .name("Ghế đẩy tạ Bench Press")
                    .type("Free Weights")
                    .status(EquipmentStatus.OPERATIONAL)
                    .purchaseDate(LocalDate.now().minusMonths(6))
                    .lastMaintenanceDate(LocalDate.now().minusMonths(1))
                    .location("Tầng 2 - Khu Strength Area")
                    .build());
            equipmentRepository.save(Equipment.builder()
                    .equipmentCode("EQ007")
                    .name("Bộ tạ tay Dumbbell Set 5kg-30kg")
                    .type("Free Weights")
                    .status(EquipmentStatus.OPERATIONAL)
                    .purchaseDate(LocalDate.now().minusMonths(10))
                    .lastMaintenanceDate(LocalDate.now().minusMonths(2))
                    .location("Tầng 2 - Tạ tay Area")
                    .build());
            equipmentRepository.save(Equipment.builder()
                    .equipmentCode("EQ008")
                    .name("Thảm tập Yoga Cao Cấp")
                    .type("Accessories")
                    .status(EquipmentStatus.OPERATIONAL)
                    .purchaseDate(LocalDate.now().minusMonths(3))
                    .lastMaintenanceDate(LocalDate.now().minusDays(10))
                    .location("Phòng Yoga - Tầng 3")
                    .build());
            log.info("Gym Equipment seeded.");
        }

    }
}

