package de.wps.ddd.cinema.filmselection.web;

import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.filmselection.data.Film;
import de.wps.ddd.cinema.filmselection.data.Hall;
import de.wps.ddd.cinema.filmselection.data.Screening;
import de.wps.ddd.cinema.filmselection.service.ProgramService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProgramController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProgramControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProgramService programService;

    @Test
    void getFilmsForDay_returnsFilmsWithBilingualTextsAndScreenings() throws Exception {
        // arrange
        var hall = new Hall(1L, LocalizedText.of("großer Saal", "Large Hall"));
        var film = new Film(
                1L,
                "Guardians of the Lunacy",
                120,
                "https://example.com/poster/guardians-of-the-lunacy.jpg",
                12,
                LocalizedText.of(
                        "Eine spektakuläre Weltraum-Komödie um eine chaotische Crew.",
                        "A spectacular space comedy about a chaotic crew."),
                LocalizedText.of("Science-Fiction", "Science fiction"),
                "Chris Pratt",
                "James Gunn",
                LocalizedText.of("Deutsch", "German"),
                List.of(
                        new Screening(1L, UUID.fromString("11111111-1111-1111-1111-111111111111"), 1L,
                                LocalDateTime.parse("2025-03-19T14:30"), hall),
                        new Screening(2L, UUID.fromString("22222222-2222-2222-2222-222222222222"), 1L,
                                LocalDateTime.parse("2025-03-19T20:30"), hall)));
        when(programService.getFilmsForDay(LocalDate.parse("2025-03-19"))).thenReturn(List.of(film));

        // act / assert
        mockMvc.perform(get("/api/program").param("date", "2025-03-19"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[0].title").value("Guardians of the Lunacy"))
                .andExpect(jsonPath("$[0].runtimeMinutes").value(120))
                .andExpect(jsonPath("$[0].posterUrl").value("https://example.com/poster/guardians-of-the-lunacy.jpg"))
                .andExpect(jsonPath("$[0].ageRating").value(12))
                .andExpect(jsonPath("$[0].description.de").value("Eine spektakuläre Weltraum-Komödie um eine chaotische Crew."))
                .andExpect(jsonPath("$[0].description.en").value("A spectacular space comedy about a chaotic crew."))
                .andExpect(jsonPath("$[0].genre.de").value("Science-Fiction"))
                .andExpect(jsonPath("$[0].genre.en").value("Science fiction"))
                .andExpect(jsonPath("$[0].leadActors").value("Chris Pratt"))
                .andExpect(jsonPath("$[0].director").value("James Gunn"))
                .andExpect(jsonPath("$[0].language.de").value("Deutsch"))
                .andExpect(jsonPath("$[0].language.en").value("German"))
                .andExpect(jsonPath("$[0].screenings.length()").value(2))
                .andExpect(jsonPath("$[0].screenings[0].id").value("11111111-1111-1111-1111-111111111111"))
                .andExpect(jsonPath("$[0].screenings[0].uuid").doesNotExist())
                .andExpect(jsonPath("$[0].screenings[0].filmId").doesNotExist())
                .andExpect(jsonPath("$[0].screenings[0].hall.de").value("großer Saal"))
                .andExpect(jsonPath("$[0].screenings[0].hall.en").value("Large Hall"))
                .andExpect(jsonPath("$[0].screenings[0].hall.id").doesNotExist())
                .andExpect(jsonPath("$[0].screenings[0].start").value("2025-03-19T14:30:00"));
    }
}
