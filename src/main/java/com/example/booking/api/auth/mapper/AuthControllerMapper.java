package com.example.booking.api.auth.mapper;

import com.example.booking.api.dto.LoginResponse;
import com.example.booking.exception.ResultStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, imports = { ResultStatus.class })
public interface AuthControllerMapper {


	@Mapping(target = "result", expression = "java(ResultStatus.SUCCESS)")
	LoginResponse toLoginResponse(String token, String username);
}
