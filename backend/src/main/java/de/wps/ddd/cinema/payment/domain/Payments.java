package de.wps.ddd.cinema.payment.domain;

import org.jmolecules.ddd.annotation.Repository;

@Repository
public interface Payments {

    void save(Payment payment);

    Payment get(PaymentReference reference);
}
