package com.example.booking.api;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.example.booking.api.dto.SlotDto;
import com.example.booking.api.dto.SlotsPageDto;
import com.example.booking.api.slot.SlotController;
import com.example.booking.security.JwtAuthFilter;
import com.example.booking.service.slot.AvailableSlotService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SlotController.class)
@AutoConfigureMockMvc(addFilters = false)
class SlotControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	AvailableSlotService availableSlotService;

	@MockBean
	JwtAuthFilter jwtAuthFilter;

	@Test
	void list_defaults_ok() throws Exception {
		var dto = new SlotsPageDto(
				List.of(new SlotDto(1L, Instant.parse("2025-09-01T10:00:00Z"), Instant.parse("2025-09-01T11:00:00Z"))),
				0, 10, 1, 1, true
		);

		when(availableSlotService.getFreeSlots(isNull(), isNull(), any(Pageable.class))).thenReturn(dto);

		mockMvc.perform(get("/api/slots").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(1));

		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(availableSlotService).getFreeSlots(isNull(), isNull(), captor.capture());

		Pageable used = captor.getValue();
		assertThat(used.getPageNumber()).isEqualTo(0);
		assertThat(used.getPageSize()).isEqualTo(10);
		assertThat(used.getSort().getOrderFor("startTime").getDirection()).isEqualTo(Sort.Direction.ASC);
	}

	@Test
	void list_sortBy_endTime() throws Exception {
		var dto = new SlotsPageDto(List.of(), 0, 10, 0, 0, true);
		when(availableSlotService.getFreeSlots(isNull(), isNull(), any(Pageable.class))).thenReturn(dto);

		mockMvc.perform(get("/api/slots")
						.param("sortBy", "endTime")
						.param("dir", "DESC"))
				.andExpect(status().isOk());

		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(availableSlotService).getFreeSlots(isNull(), isNull(), captor.capture());

		Pageable used = captor.getValue();
		assertThat(Objects.requireNonNull(used.getSort().getOrderFor("endTime")).getDirection()).isEqualTo(Direction.ASC);
	}

	@Test
	void list_withFromTo() throws Exception {
		Instant from = Instant.parse("2025-09-01T00:00:00Z");
		Instant to = Instant.parse("2025-09-30T23:59:59Z");

		var dto = new SlotsPageDto(List.of(), 0, 10, 0, 0, true);
		when(availableSlotService.getFreeSlots(any(), any(), any(Pageable.class))).thenReturn(dto);

		mockMvc.perform(get("/api/slots")
						.param("from", from.toString())
						.param("to", to.toString()))
				.andExpect(status().isOk());

		verify(availableSlotService).getFreeSlots(eq(from), eq(to), any(Pageable.class));
	}

	@Test
	void list_invalidDir_400() throws Exception {
		mockMvc.perform(get("/api/slots")
				.param("dir", "WRONG"));
	}
}
