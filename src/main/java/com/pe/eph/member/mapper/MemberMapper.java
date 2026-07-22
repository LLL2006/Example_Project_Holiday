package com.pe.eph.member.mapper;

import com.pe.eph.member.dto.request.CreateMemberRequest;
import com.pe.eph.member.dto.request.UpdateMemberRequest;
import com.pe.eph.member.dto.response.MemberResponse;
import com.pe.eph.member.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public Member toEntity(CreateMemberRequest request) {
        if (request == null) {
            return null;
        }

        return Member.builder()
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .build();
    }

    public void updateEntity(Member member, UpdateMemberRequest request) {
        if (member == null || request == null) {
            return;
        }

        member.setFullName(request.getFullName());
        member.setGender(request.getGender());
        member.setDateOfBirth(request.getDateOfBirth());
        member.setPhone(request.getPhone());
        member.setEmail(request.getEmail());
        member.setAddress(request.getAddress());
    }

    public MemberResponse toResponse(Member member) {
        if (member == null) {
            return null;
        }

        return MemberResponse.builder()
                .id(member.getId())
                .memberCode(member.getMemberCode())
                .fullName(member.getFullName())
                .gender(member.getGender())
                .dateOfBirth(member.getDateOfBirth())
                .phone(member.getPhone())
                .email(member.getEmail())
                .address(member.getAddress())
                .packageName(member.getGymPackage() == null ? null : member.getGymPackage().getPackageName())
                .trainerName(member.getTrainer() == null ? null : member.getTrainer().getFullName())
                .trainerId(member.getTrainer() == null ? null : member.getTrainer().getId())
                .joinDate(member.getJoinDate())
                .expireDate(member.getExpireDate())
                .status(member.getStatus())
                .avatar(member.getAvatar())
                .remainingPtSessions(member.getRemainingPtSessions())
                .totalPtSessions(member.getGymPackage() == null ? 0 : member.getGymPackage().getPtSessions())
                .build();
    }

}
