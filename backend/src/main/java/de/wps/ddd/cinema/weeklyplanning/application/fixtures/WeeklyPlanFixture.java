package de.wps.ddd.cinema.weeklyplanning.application.fixtures;

import de.wps.ddd.cinema.common.fixtures.Fixture;
import de.wps.ddd.cinema.common.text.LocalizedText;
import de.wps.ddd.cinema.weeklyplanning.WeeklyPlanCreatedDto;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * On startup, builds the weekly plan of the current ISO calendar week (Mon–Sun) in code and then
 * publishes the {@link WeeklyPlanCreatedDto}. The reference day comes from the injected {@link Clock};
 * in production that is the system clock, so the demo always shows "this week". Tests pin the clock to
 * 2025-03-19 (calendar week 12/2025) and thus reproduce exactly the same schedule.
 * <p>
 * Replaces the former {@code data.sql} seeding: after the event, film selection and ticket sale fetch
 * their views themselves through the {@code WeeklyPlanQuery}.
 * <p>
 * Publishing must happen inside a transaction, otherwise the downstream
 * {@code @ApplicationModuleListener} (async after commit) does not fire.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyPlanFixture implements Fixture {

    private static final String STAR_BOARS = "Star Boars";
    private static final String GUARDIANS = "Guardians of the Lunacy";
    private static final String BACK_TO_THE_FUTURA = "Back to the Futura";
    private static final String CLOWN_WARS = "Clown Wars";
    private static final String FAST_AND_CURIOUS = "The Fast and the Curious";

    private static final HallName LARGE_HALL = new HallName(LocalizedText.of("großer Saal", "Large Hall"));
    private static final HallName SMALL_HALL = new HallName(LocalizedText.of("kleiner Saal", "Small Hall"));

    private static final LocalizedText GERMAN = LocalizedText.of("deutsch", "German");
    private static final LocalizedText ENGLISH = LocalizedText.of("englisch", "English");

    private final FilmCatalog filmCatalog;
    private final WeeklyPlans weeklyPlans;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    @Transactional
    @Override
    public void install() {
        var today = LocalDate.now(clock);
        var monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        int year = today.get(IsoFields.WEEK_BASED_YEAR);
        int calendarWeek = today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        log.info("Creating weekly plan for calendar week {}/{}...", calendarWeek, year);

        var films = createFilms(today);
        var weeklyPlan = createWeeklyPlan(year, calendarWeek, monday, films);

        events.publishEvent(new WeeklyPlanCreatedDto(year, calendarWeek));
        log.info("Weekly plan published: {} screenings", weeklyPlan.getScreenings().size());
    }

    private Map<String, Film> createFilms(LocalDate today) {
        // Generous licence period around the current week: from the first of the month to the end of the year.
        var licenceStart = today.withDayOfMonth(1);
        var licenceEnd = today.with(TemporalAdjusters.lastDayOfYear());
        var films = new LinkedHashMap<String, Film>();
        registerFilm(films, new Film(new FilmTitle(STAR_BOARS), 125, "assets/Star_Boars.jpeg",
                LocalizedText.of("""
                                In einer weit, weit entfernten Galaxie kämpfen mutige Wildschweine gegen das tyrannische Imperium.
                                Angeführt von Luke Stywalker, müssen sie sich mit Lichttrüffeln und telepathischen Grunzkraftfähigkeiten gegen den dunklen Lord Swineous behaupten.
                                Ein episches Sci-Fi-Abenteuer voller Action, Humor und intergalaktischem Speckduft!""",
                        """
                                In a galaxy far, far away, brave wild boars rise up against the tyrannical Empire.
                                Led by Luke Stywalker, they must stand their ground against the dark Lord Swineous, armed with nothing but light truffles and the telepathic power of the Grunt.
                                An epic sci-fi adventure full of action, humor and the intergalactic aroma of bacon!"""),
                LocalizedText.of("Sci-Fi, Comedy", "Sci-Fi, Comedy"), "Luke Stywalker", "George Laxus", GERMAN, AgeRating.FSK12,
                new DistributionDeal("Galactic Film Distribution", MoneyAmount.euro(1500, 0), licenceStart, licenceEnd)));

        registerFilm(films, new Film(new FilmTitle(GUARDIANS), 95, "assets/Guardians_Of_The_Lunacy.jpeg",
                LocalizedText.of("""
                                Eine Truppe aus abgedrehten Außenseitern wird widerwillig zum Schutz des Universums verpflichtet – und das ist kein gutes Zeichen.
                                Chris Plattfall und sein chaotisches Team aus galaktischen Verrückten stürzen sich in explosive Abenteuer voller skurriler Aliens,
                                dummer Sprüche und unerwarteter Heldentaten. Werden sie das Universum retten? Wahrscheinlich nicht. Aber es wird lustig!""",
                        """
                                A gang of unhinged misfits is reluctantly drafted to protect the universe – which is never a good sign.
                                Chris Plattfall and his chaotic crew of galactic lunatics hurl themselves into explosive adventures packed with bizarre aliens,
                                dumb one-liners and accidental heroics. Will they save the universe? Probably not. But it'll be a blast!"""),
                LocalizedText.of("Sci-Fi, Comedy", "Sci-Fi, Comedy"), "Chris Plattfall", "James Gunner", ENGLISH, AgeRating.FSK6,
                new DistributionDeal("Moonshine Pictures", MoneyAmount.euro(1200, 0), licenceStart, licenceEnd)));

        registerFilm(films, new Film(new FilmTitle(BACK_TO_THE_FUTURA), 116, "assets/Back_To_The_Futura.jpeg",
                LocalizedText.of("""
                                Als die junge Marty McGigawatts mit einer experimentellen Zeitmaschine in die Zukunft reist, findet sie sich in einer dystopischen Megacity wieder,
                                in der Roboter die Welt regieren. Mit der Hilfe eines exzentrischen Erfinders und einem Hoverboard muss sie den Lauf der Geschichte ändern,
                                bevor sie in einer Endlosschleife der Zeit gefangen bleibt.""",
                        """
                                When young Marty McGigawatts takes an experimental time machine for a spin into the future, she lands in a dystopian megacity
                                where robots run the world. With the help of an eccentric inventor and a hoverboard, she has to change the course of history
                                before she ends up stuck in an endless time loop."""),
                LocalizedText.of("Sci-Fi, Adventure", "Sci-Fi, Adventure"), "Marty McGigawatts", "Robert Zoomekis", GERMAN, AgeRating.FSK12,
                new DistributionDeal("Time Travel Distribution", MoneyAmount.euro(1400, 0), licenceStart, licenceEnd)));

        registerFilm(films, new Film(new FilmTitle(CLOWN_WARS), 105, "assets/Clown_Wars.jpeg",
                LocalizedText.of("""
                                Die Erde wird von einer Horde außerirdischer Clowns angegriffen, die nichts anderes wollen, als die Menschheit mit tödlichen Gags zu unterwerfen.
                                Nur eine Gruppe rebellischer Spaßmacher kann sich der Bedrohung entgegenstellen. Ein intergalaktisches Spektakel voller Ballontier-Kriege,
                                Killer-Jojos und einem epischen Showdown in der Zirkusarena des Todes.""",
                        """
                                Earth is under attack by a horde of alien clowns who want nothing more than to bring humanity to its knees with lethal gags.
                                Only a band of rebel jesters can stand up to the threat. An intergalactic spectacle packed with balloon-animal warfare,
                                killer yo-yos and an epic showdown in the circus ring of doom."""),
                LocalizedText.of("Sci-Fi, Horror, Comedy", "Sci-Fi, Horror, Comedy"), "Penny Wisecrack", "Tim Burtonisch", ENGLISH, AgeRating.FSK18,
                new DistributionDeal("Circus of Horrors Films", MoneyAmount.euro(1300, 0), licenceStart, licenceEnd)));

        registerFilm(films, new Film(new FilmTitle(FAST_AND_CURIOUS), 135, "assets/The_Fast_And_The_Curious.jpeg",
                LocalizedText.of("""
                                The Fast and the Curious ist ein actiongeladener Film über eine Bande von hochintelligenten Straßenkatzen, die illegale Straßenrennen fahren und geheime Raubüberfälle planen.
                                Angeführt von der waghalsigen und charismatischen Kätzin Velo, entdeckt das Team, dass eine rivalisierende Hunde-Gang versucht, die Straßen zu übernehmen.
                                Während atemberaubender Verfolgungsjagden, waghalsiger Stunts und cleverer Pläne müssen die Katzen nicht nur ihre Revierhoheit verteidigen, sondern auch ein letztes, spektakuläres Rennen gewinnen, um ihre Freiheit zu sichern.
                                Ein rasanter Mix aus Action, Humor und katzenhafter Cleverness!""",
                        """
                                The Fast and the Curious is an action-packed film about a gang of highly intelligent street cats who race illegally and plot secret heists.
                                Led by Velo, a daredevil feline with charisma to spare, the crew discovers that a rival dog gang is trying to take over the streets.
                                Through breathtaking chases, death-defying stunts and cunning schemes, the cats must not only defend their turf but also win one last, spectacular race to secure their freedom.
                                A high-speed mix of action, humor and purr-fectly feline cleverness!"""),
                LocalizedText.of("Action, Adventure, Tierfilm", "Action, Adventure, Animal Film"), "Cat Moss", "Rob Kitten", GERMAN, AgeRating.FSK16,
                new DistributionDeal("Purring Engines Distribution", MoneyAmount.euro(1600, 0), licenceStart, licenceEnd)));
        return films;
    }

    private @NonNull WeeklyPlan createWeeklyPlan(int year, int calendarWeek, LocalDate monday, Map<String, Film> films) {
        var weeklyPlan = WeeklyPlan.forWeek(new CalendarWeek(year, calendarWeek));
        scheduleScreenings(weeklyPlan, monday, films);
        weeklyPlans.save(weeklyPlan);
        return weeklyPlan;
    }

    // The schedule is a pattern of weekday + time of day; the actual dates follow from the Monday of the
    // reference week (see schedule(...)). This keeps the program relative to the current date.
    private void scheduleScreenings(WeeklyPlan weeklyPlan, LocalDate monday, Map<String, Film> films) {
        schedule(weeklyPlan, monday, films.get(GUARDIANS), SMALL_HALL, ScreeningCategory.CHILDREN, DayOfWeek.MONDAY, LocalTime.of(15, 0));
        schedule(weeklyPlan, monday, films.get(FAST_AND_CURIOUS), LARGE_HALL, ScreeningCategory.PRIME_TIME, DayOfWeek.MONDAY, LocalTime.of(20, 0));
        schedule(weeklyPlan, monday, films.get(STAR_BOARS), SMALL_HALL, ScreeningCategory.STANDARD, DayOfWeek.TUESDAY, LocalTime.of(15, 30));
        schedule(weeklyPlan, monday, films.get(BACK_TO_THE_FUTURA), LARGE_HALL, ScreeningCategory.STANDARD, DayOfWeek.TUESDAY, LocalTime.of(15, 0));
        schedule(weeklyPlan, monday, films.get(CLOWN_WARS), SMALL_HALL, ScreeningCategory.STANDARD, DayOfWeek.TUESDAY, LocalTime.of(22, 30));
        schedule(weeklyPlan, monday, films.get(GUARDIANS), LARGE_HALL, ScreeningCategory.CHILDREN, DayOfWeek.WEDNESDAY, LocalTime.of(14, 30));
        schedule(weeklyPlan, monday, films.get(FAST_AND_CURIOUS), SMALL_HALL, ScreeningCategory.STANDARD, DayOfWeek.WEDNESDAY, LocalTime.of(15, 0));
        schedule(weeklyPlan, monday, films.get(FAST_AND_CURIOUS), LARGE_HALL, ScreeningCategory.PRIME_TIME, DayOfWeek.WEDNESDAY, LocalTime.of(20, 30));
        schedule(weeklyPlan, monday, films.get(CLOWN_WARS), SMALL_HALL, ScreeningCategory.STANDARD, DayOfWeek.WEDNESDAY, LocalTime.of(22, 30));
        schedule(weeklyPlan, monday, films.get(GUARDIANS), SMALL_HALL, ScreeningCategory.CHILDREN, DayOfWeek.THURSDAY, LocalTime.of(14, 30));
        schedule(weeklyPlan, monday, films.get(BACK_TO_THE_FUTURA), LARGE_HALL, ScreeningCategory.STANDARD, DayOfWeek.THURSDAY, LocalTime.of(15, 30));
        schedule(weeklyPlan, monday, films.get(GUARDIANS), LARGE_HALL, ScreeningCategory.PRIME_TIME, DayOfWeek.THURSDAY, LocalTime.of(20, 0));
        schedule(weeklyPlan, monday, films.get(FAST_AND_CURIOUS), LARGE_HALL, ScreeningCategory.STANDARD, DayOfWeek.FRIDAY, LocalTime.of(15, 0));
        schedule(weeklyPlan, monday, films.get(BACK_TO_THE_FUTURA), SMALL_HALL, ScreeningCategory.STANDARD, DayOfWeek.FRIDAY, LocalTime.of(16, 15));
        schedule(weeklyPlan, monday, films.get(BACK_TO_THE_FUTURA), LARGE_HALL, ScreeningCategory.PRIME_TIME, DayOfWeek.FRIDAY, LocalTime.of(20, 30));
        schedule(weeklyPlan, monday, films.get(CLOWN_WARS), SMALL_HALL, ScreeningCategory.STANDARD, DayOfWeek.FRIDAY, LocalTime.of(22, 30));
        schedule(weeklyPlan, monday, films.get(BACK_TO_THE_FUTURA), LARGE_HALL, ScreeningCategory.STANDARD, DayOfWeek.SATURDAY, LocalTime.of(14, 30));
        schedule(weeklyPlan, monday, films.get(STAR_BOARS), SMALL_HALL, ScreeningCategory.STANDARD, DayOfWeek.SATURDAY, LocalTime.of(15, 30));
        schedule(weeklyPlan, monday, films.get(GUARDIANS), SMALL_HALL, ScreeningCategory.CHILDREN, DayOfWeek.SATURDAY, LocalTime.of(19, 30));
        schedule(weeklyPlan, monday, films.get(STAR_BOARS), LARGE_HALL, ScreeningCategory.PRIME_TIME, DayOfWeek.SATURDAY, LocalTime.of(20, 45));
        schedule(weeklyPlan, monday, films.get(FAST_AND_CURIOUS), SMALL_HALL, ScreeningCategory.STANDARD, DayOfWeek.SUNDAY, LocalTime.of(14, 30));
        schedule(weeklyPlan, monday, films.get(GUARDIANS), LARGE_HALL, ScreeningCategory.CHILDREN, DayOfWeek.SUNDAY, LocalTime.of(15, 45));
        schedule(weeklyPlan, monday, films.get(FAST_AND_CURIOUS), LARGE_HALL, ScreeningCategory.PRIME_TIME, DayOfWeek.SUNDAY, LocalTime.of(19, 30));
        schedule(weeklyPlan, monday, films.get(CLOWN_WARS), SMALL_HALL, ScreeningCategory.STANDARD, DayOfWeek.SUNDAY, LocalTime.of(22, 30));
    }

    private void schedule(WeeklyPlan weeklyPlan, LocalDate monday, Film film, HallName hall, ScreeningCategory category, DayOfWeek day, LocalTime timeOfDay) {
        var start = LocalDateTime.of(monday.plusDays(day.getValue() - 1), timeOfDay);
        weeklyPlan.scheduleScreening(Screening.schedule(film, hall, category, start));
    }

    private void registerFilm(Map<String, Film> films, Film film) {
        films.put(film.title().value(), film);
        filmCatalog.save(film);
    }
}
