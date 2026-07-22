package com.pe.eph.trainer.service.impl;

import com.pe.eph.booking.repository.PTBookingRepository;
import com.pe.eph.common.enums.PTBookingStatus;
import com.pe.eph.common.enums.UserStatus;
import com.pe.eph.common.exception.DuplicateResourceException;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.role.entity.Role;
import com.pe.eph.role.repository.RoleRepository;
import com.pe.eph.trainer.dto.request.CreateTrainerRequest;
import com.pe.eph.trainer.dto.request.UpdateTrainerRequest;
import com.pe.eph.trainer.dto.response.TrainerResponse;
import com.pe.eph.trainer.entity.Trainer;
import com.pe.eph.trainer.mapper.TrainerMapper;
import com.pe.eph.trainer.repository.TrainerRepository;
import com.pe.eph.trainer.service.TrainerService;
import com.pe.eph.user.entity.User;
import com.pe.eph.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainerMapper trainerMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PTBookingRepository ptBookingRepository;

    private TrainerResponse mapToResponse(Trainer trainer) {
        TrainerResponse response = trainerMapper.toResponse(trainer);
        if (trainer != null && response != null) {
            long completed = ptBookingRepository.countByTrainerAndStatus(trainer, PTBookingStatus.COMPLETED);
            long scheduled = ptBookingRepository.countByTrainerAndStatus(trainer, PTBookingStatus.SCHEDULED);
            response.setCompletedSessions(completed);
            response.setScheduledSessions(scheduled);
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainerResponse> getAllTrainers(Pageable pageable) {
        return trainerRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerResponse getTrainerById(Long id) {

        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy huấn luyện viên."));

        return mapToResponse(trainer);
    }

    @Override
    public TrainerResponse createTrainer(CreateTrainerRequest request) {

        if (trainerRepository.existsByTrainerCode(request.getTrainerCode())) {
            throw new DuplicateResourceException("Mã huấn luyện viên đã tồn tại.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã tồn tại.");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại đã tồn tại.");
        }

        Trainer trainer = trainerMapper.toEntity(request);
        trainer.setStatus(UserStatus.ACTIVE);

        Role trainerRole = roleRepository.findByRoleName("TRAINER")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleName("TRAINER")
                        .description("Huấn luyện viên")
                        .build()));

        User user = User.builder()
                .username(request.getPhone())
                .password(passwordEncoder.encode("123456"))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .status(UserStatus.ACTIVE)
                .role(trainerRole)
                .build();

        userRepository.save(user);

        trainer = trainerRepository.save(trainer);

        return mapToResponse(trainer);
    }

    @Override
    public TrainerResponse updateTrainer(Long id, UpdateTrainerRequest request) {

        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy huấn luyện viên."));

        if (!trainer.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã tồn tại.");
        }

        if (!trainer.getPhone().equals(request.getPhone())
                && userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại đã tồn tại.");
        }

        userRepository.findByPhone(trainer.getPhone()).ifPresent(user -> {
            user.setPhone(request.getPhone());
            user.setUsername(request.getPhone());
            user.setEmail(request.getEmail());
            user.setFullName(request.getFullName());
            user.setStatus(request.getStatus());
            userRepository.save(user);
        });

        trainerMapper.updateEntity(trainer, request);

        trainer = trainerRepository.save(trainer);

        return mapToResponse(trainer);
    }

    @Override
    public void deleteTrainer(Long id) {

        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy huấn luyện viên."));

        trainer.setStatus(UserStatus.INACTIVE);

        trainerRepository.save(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainerResponse> searchTrainers(String keyword, Pageable pageable) {

        return trainerRepository.searchTrainers(keyword, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerResponse getTrainerByPhone(String phone) {
        Trainer trainer = trainerRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy huấn luyện viên với số điện thoại này."));
        return mapToResponse(trainer);
    }
}