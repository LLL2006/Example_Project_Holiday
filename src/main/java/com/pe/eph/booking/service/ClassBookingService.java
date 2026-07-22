package com.pe.eph.booking.service;

import com.pe.eph.booking.dto.request.ClassBookingRequest;
import com.pe.eph.booking.dto.response.ClassBookingResponse;

public interface ClassBookingService {

    ClassBookingResponse bookClass(ClassBookingRequest request);

    void cancelBooking(Long bookingId);

}
