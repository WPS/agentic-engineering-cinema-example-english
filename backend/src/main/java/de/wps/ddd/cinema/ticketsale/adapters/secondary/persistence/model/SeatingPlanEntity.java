package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "seating_plans", schema = "ticket_sale")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SeatingPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false, updatable = false, unique = true)
    private UUID screeningId;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER,
            mappedBy = "seatingPlan"
    )
    private List<SeatEntity> seats = new ArrayList<>();

    public SeatingPlanEntity(Integer id, UUID screeningId) {
        this.id = id;
        this.screeningId = screeningId;
    }

    public void addSeat(int rowNumber, int seatNumber, String category, boolean sold) {
        seats.add(new SeatEntity(this, rowNumber, seatNumber, category, sold));
    }
}
