package com.pe.eph.booking.service;

import com.pe.eph.booking.dto.request.PTBookingRequest;
import com.pe.eph.booking.dto.response.PTBookingResponse;

public interface PTBookingService {

    PTBookingResponse bookSession(PTBookingRequest request);

    void completeSession(Long bookingId);

    void cancelSession(Long bookingId);

}
