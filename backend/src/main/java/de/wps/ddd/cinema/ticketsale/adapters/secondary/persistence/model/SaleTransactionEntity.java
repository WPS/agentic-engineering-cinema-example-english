package de.wps.ddd.cinema.ticketsale.adapters.secondary.persistence.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sale_transactions", schema = "ticket_sale")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SaleTransactionEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID orderNumber;

    private UUID screeningId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "sale_transaction_seats",
            schema = "ticket_sale",
            joinColumns = @JoinColumn(name = "order_number")
    )
    private List<SeatEmbeddable> seats = new ArrayList<>();

    private int totalPriceInCents;

    @Embedded
    @AttributeOverride(name = "status", column = @Column(name = "payment_status"))
    private PaymentAttemptEmbeddable paymentAttempt;

    private int attempts;

    private String status;

    /** {@code rowNumber}/{@code seatNumber} instead of {@code row}/{@code number}: ROW is an SQL keyword. */
    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SeatEmbeddable {
        private int rowNumber;
        private int seatNumber;
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentAttemptEmbeddable {
        private UUID paymentAttemptId;
        private Integer attempt;
        private Integer amountInCents;
        private String status;
    }
}
