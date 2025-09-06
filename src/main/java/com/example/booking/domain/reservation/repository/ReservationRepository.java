package com.example.booking.domain.reservation.repository;

import com.example.booking.domain.reservation.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {}
