package com.upc.courtly.courts.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.courtly.courts.domain.model.aggregates.Court;
import com.upc.courtly.courts.domain.model.commands.CreateCourtCommand;
import com.upc.courtly.courts.domain.model.commands.DeleteCourtCommand;
import com.upc.courtly.courts.domain.model.commands.UpdateCourtCommand;
import com.upc.courtly.courts.domain.model.queries.GetAllCourtsQuery;
import com.upc.courtly.courts.domain.model.queries.GetCourtByIdQuery;
import com.upc.courtly.courts.domain.services.CourtCommandService;
import com.upc.courtly.courts.domain.services.CourtQueryService;
import com.upc.courtly.courts.interfaces.rest.resources.CreateCourtResource;
import com.upc.courtly.courts.interfaces.rest.resources.UpdateCourtResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CourtsIntegrationTest {

    @Mock
    private CourtCommandService courtCommandService;

    @Mock
    private CourtQueryService courtQueryService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CourtsController(courtCommandService, courtQueryService))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    @DisplayName("Debería crear una cancha correctamente")
    void shouldCreateCourtSuccessfully() throws Exception {
        CreateCourtResource request = new CreateCourtResource(
                "Court Central",
                "Lima",
                "Tennis",
                "https://image.com/court.png",
                new BigDecimal("50.00")
        );

        Court court = new Court();
        court.setId(1L);
        court.setName("Court Central");
        court.setLocation("Lima");
        court.setType("Tennis");
        court.setImageUrl("https://image.com/court.png");
        court.setPricePerHour(new BigDecimal("50.00"));

        when(courtCommandService.handle(any(CreateCourtCommand.class)))
                .thenReturn(Optional.of(court));

        mockMvc.perform(post("/api/v1/courts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Court Central"))
                .andExpect(jsonPath("$.location").value("Lima"));
    }

    @Test
    @DisplayName("Debería devolver todas las canchas")
    void shouldReturnAllCourts() throws Exception {
        Court court = new Court();
        court.setId(1L);
        court.setName("Court Central");
        court.setLocation("Lima");
        court.setType("Tennis");
        court.setImageUrl("https://image.com/court.png");
        court.setPricePerHour(new BigDecimal("50.00"));

        when(courtQueryService.handle(any(GetAllCourtsQuery.class)))
                .thenReturn(List.of(court));

        mockMvc.perform(get("/api/v1/courts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Court Central"));
    }

    @Test
    @DisplayName("Debería devolver una cancha por su id")
    void shouldReturnCourtById() throws Exception {
        Court court = new Court();
        court.setId(1L);
        court.setName("Court Central");
        court.setLocation("Lima");
        court.setType("Tennis");
        court.setImageUrl("https://image.com/court.png");
        court.setPricePerHour(new BigDecimal("50.00"));

        when(courtQueryService.handle(any(GetCourtByIdQuery.class)))
                .thenReturn(Optional.of(court));

        mockMvc.perform(get("/api/v1/courts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Court Central"));
    }

    @Test
    @DisplayName("Debería actualizar la información de una cancha correctamente")
    void shouldUpdateCourtSuccessfully() throws Exception {
        UpdateCourtResource request = new UpdateCourtResource(
                "Court Updated",
                "Miraflores",
                "Padel",
                "https://image.com/court-updated.png",
                new BigDecimal("75.00")
        );

        Court updatedCourt = new Court();
        updatedCourt.setId(1L);
        updatedCourt.setName("Cancha actualizada");
        updatedCourt.setLocation("Miraflores");
        updatedCourt.setType("Padel");
        updatedCourt.setImageUrl("https://image.com/court-updated.png");
        updatedCourt.setPricePerHour(new BigDecimal("75.00"));

        when(courtCommandService.handle(any(UpdateCourtCommand.class)))
                .thenReturn(Optional.of(updatedCourt));

        mockMvc.perform(put("/api/v1/courts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Cancha actualizada"));
    }

    @Test
    @DisplayName("Debería eliminar una cancha correctamente")
    void shouldDeleteCourtSuccessfully() throws Exception {
        doNothing().when(courtCommandService).handle(any(DeleteCourtCommand.class));

        mockMvc.perform(delete("/api/v1/courts/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cancha eliminada correctamente"));
    }
}