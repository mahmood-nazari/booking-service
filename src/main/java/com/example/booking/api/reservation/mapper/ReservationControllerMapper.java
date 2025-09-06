package com.example.booking.api.reservation.mapper;

import com.example.booking.api.dto.ReservationResponse;
import com.example.booking.exception.ResultStatus;
import com.example.booking.service.reservation.model.ReservationResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, imports = { ResultStatus.class })
public interface ReservationControllerMapper {

	@Mapping(target = "result", expression = "java(ResultStatus.SUCCESS)")
	ReservationResponse toReservationResponse(ReservationResponseModel result);
}
