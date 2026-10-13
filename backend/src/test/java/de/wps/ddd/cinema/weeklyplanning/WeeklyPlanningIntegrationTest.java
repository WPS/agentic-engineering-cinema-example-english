package de.wps.ddd.cinema.weeklyplanning;

import de.wps.ddd.cinema.FixedClockTestConfig;
import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.weeklyplanning.WeeklyPlanQuery;
import de.wps.ddd.cinema.weeklyplanning.application.fixtures.WeeklyPlanFixture;
import de.wps.ddd.cinema.weeklyplanning.filmcatalog.AgeRating;
import de.wps.ddd.cinema.weeklyplanning.filmcatalog.DistributionDeal;
import de.wps.ddd.cinema.weeklyplanning.filmcatalog.Film;
import de.wps.ddd.cinema.weeklyplanning.filmcatalog.FilmCatalog;
import de.wps.ddd.cinema.weeklyplanning.filmcatalog.FilmTitle;
import de.wps.ddd.cinema.weeklyplanning.filmcatalog.MoneyAmount;
import de.wps.ddd.cinema.weeklyplanning.hallmanagement.HallName;
import de.wps.ddd.cinema.weeklyplanning.weeklyplan.CalendarWeek;
import de.wps.ddd.cinema.weeklyplanning.weeklyplan.Screening;
import de.wps.ddd.cinema.weeklyplanning.weeklyplan.ScreeningCategory;
import de.wps.ddd.cinema.weeklyplanning.weeklyplan.WeeklyPlan;
import de.wps.ddd.cinema.weeklyplanning.weeklyplan.WeeklyPlans;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ApplicationModuleTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Import(FixedClockTestConfig.class)
class WeeklyPlanningIntegrationTest {

    private static final LocalizedText LARGE_HALL = LocalizedText.of("großer Saal", "Large Hall");
    private static final LocalizedText SMALL_HALL = LocalizedText.of("kleiner Saal", "Small Hall");

    @Autowired
    private WeeklyPlanQuery weeklyPlanQuery;

    @Autowired
    private FilmCatalog filmCatalog;

    @Autowired
    private WeeklyPlans weeklyPlans;

    @Autowired
    private WeeklyPlanFixture weeklyPlanFixture;

    @Test
    void providesWeeklyPlan() {
        // arrange — seed catalog and weekly plan in a week the fixture leaves alone (the hall is referenced
        // only by its name)
        var title = new FilmTitle("Test Film");
        var description = LocalizedText.of("Beschreibung", "Description");
        var genre = LocalizedText.of("Sci-Fi, Comedy", "Sci-Fi, Comedy");
        var language = LocalizedText.of("deutsch", "German");
        filmCatalog.save(new Film(title, 125, "assets/Test_Film.jpeg", description, genre,
                "Luke Stywalker", "George Laxus", language, AgeRating.FSK12,
                new DistributionDeal("Galactic Film Distribution", MoneyAmount.euro(1500, 0), LocalDate.of(2025, 3, 1), LocalDate.of(2025, 12, 31))));

        var weeklyPlan = WeeklyPlan.forWeek(new CalendarWeek(2025, 40));
        weeklyPlan.scheduleScreening(Screening.schedule(filmCatalog.find(title).orElseThrow(),
                new HallName(LARGE_HALL), ScreeningCategory.PRIME_TIME, LocalDateTime.of(2025, 9, 29, 14, 30)));
        weeklyPlans.save(weeklyPlan);
        var expectedId = weeklyPlan.getScreenings().get(0).id().value();

        // act — the neutral view shared by both consumers
        var view = weeklyPlanQuery.getWeeklyPlan(2025, 40);

        // assert — films: master data only (age rating as int, texts bilingual), no internals such as the distribution fee
        assertThat(view.films()).singleElement().satisfies(f -> {
            assertThat(f.title()).isEqualTo("Test Film");
            assertThat(f.runtimeMinutes()).isEqualTo(125);
            assertThat(f.ageRating()).isEqualTo(12);
            assertThat(f.description()).isEqualTo(description);
            assertThat(f.genre()).isEqualTo(genre);
            assertThat(f.language()).isEqualTo(language);
            assertThat(f.leadActors()).isEqualTo("Luke Stywalker");
            assertThat(f.director()).isEqualTo("George Laxus");
        });

        // assert — screenings: film/hall referenced by name, only the start time (not the planning-internal
        // times); the category goes out as the enum constant name. Seating is not part of weekly planning.
        assertThat(view.screenings()).singleElement().satisfies(s -> {
            assertThat(s.id()).isEqualTo(expectedId);
            assertThat(s.filmTitle()).isEqualTo("Test Film");
            assertThat(s.hallName()).isEqualTo(LARGE_HALL);
            assertThat(s.category()).isEqualTo("PRIME_TIME");
            assertThat(s.start()).isEqualTo(LocalDateTime.of(2025, 9, 29, 14, 30));
        });
    }

    @Test
    void fixturePlansTheReferenceWeekWithBilingualContent() {
        // the module test does not bootstrap the common FixtureInstaller, so install the fixture here;
        // the fixed clock pins the reference day to 2025-03-19, i.e. calendar week 12/2025
        weeklyPlanFixture.install();

        var view = weeklyPlanQuery.getWeeklyPlan(2025, 12);

        assertThat(view.films()).hasSize(5);
        assertThat(view.films()).allSatisfy(f -> {
            assertThat(f.description().de()).isNotBlank().isNotEqualTo(f.description().en());
            assertThat(f.description().en()).isNotBlank();
            assertThat(f.genre().en()).isNotBlank();
            assertThat(f.language().en()).isIn("German", "English");
        });
        assertThat(view.films()).filteredOn(f -> f.title().equals("The Fast and the Curious")).singleElement()
                .satisfies(f -> assertThat(f.genre()).isEqualTo(LocalizedText.of("Action, Adventure, Tierfilm", "Action, Adventure, Animal Film")));

        assertThat(view.screenings()).hasSize(24);
        assertThat(view.screenings()).allSatisfy(s -> {
            assertThat(s.hallName()).isIn(LARGE_HALL, SMALL_HALL);
            assertThat(s.category()).isIn("STANDARD", "PRIME_TIME", "CHILDREN");
            assertThat(s.start().toLocalDate()).isBetween(LocalDate.of(2025, 3, 17), LocalDate.of(2025, 3, 23));
        });
    }

    @Test
    void rejectsUnplannedWeek() {
        assertThatThrownBy(() -> weeklyPlanQuery.getWeeklyPlan(2031, 1))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage("No weekly plan for calendar week 1/2031");
    }
}
