package de.wps.ddd.cinema.ticketsale.application.domain.sales;

import lombok.Value;
import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.util.Assert;

@ValueObject
@Value(staticConstructor = "of")
public class MoneyAmount {

    int amount;
    Currency currency;

    private MoneyAmount(int amount, Currency currency) {
        Assert.isTrue(amount >= 0, "Amount must be greater than or equal to 0.");
        this.amount = amount;
        this.currency = currency;
    }

    public static MoneyAmount euroInCents(int cents) {
        return MoneyAmount.of(cents, Currency.EUR);
    }

    public static MoneyAmount euro(int euro, int cents) {
        return MoneyAmount.of(euro * 100 + cents, Currency.EUR);
    }

    public MoneyAmount plus(MoneyAmount other) {
        Assert.isTrue(this.currency.equals(other.currency), "Currencies must match.");
        return MoneyAmount.of(amount + other.amount, this.currency);
    }

    public MoneyAmount times(int factor) {
        Assert.isTrue(factor >= 0, "Factor must be greater than or equal to 0.");
        return MoneyAmount.of(amount * factor, currency);
    }

    public enum Currency {
        EUR
    }
}
