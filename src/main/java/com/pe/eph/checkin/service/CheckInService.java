package com.pe.eph.checkin.service;

import com.pe.eph.checkin.dto.request.CheckInRequest;
import com.pe.eph.checkin.dto.response.CheckInResponse;
import com.pe.eph.member.dto.response.MemberResponse;

public interface CheckInService {

    CheckInResponse toggleCheckIn(CheckInRequest request);

    MemberResponse validateMemberForCheckIn(String memberCode);

}
