package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seats", schema = "ticket_sale")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SeatEntity {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private Id id;
    private String category;
    private boolean sold;

    @Getter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("seatingPlanId") // <-- tells Hibernate to fill FK from parent
    private SeatingPlanEntity seatingPlan;

    public SeatEntity(SeatingPlanEntity seatingPlan, int rowNumber, int seatNumber, String category, boolean sold) {
        this.seatingPlan = seatingPlan;
        this.id = new Id(seatingPlan.getId(), rowNumber, seatNumber);
        this.category = category;
        this.sold = sold;
    }

    /** {@code rowNumber}/{@code seatNumber} instead of {@code row}/{@code number}: ROW is an SQL keyword. */
    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Id {
        private Integer seatingPlanId;
        private int rowNumber;
        private int seatNumber;
    }
}
