package net.xas.vrs.model;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An order of the video rental store.
 */
public class Order {

    public enum Status {
        NEW, CLOSED
    }

    private final String id;
    private final String customerId;
    private final CurrencyUnit currency;
    private final Map<String, String> rentals;

    private Status status;

    private Money totalPrice;
    private Money totalLateCharge;

    public Order(String id, String customerId, CurrencyUnit currency) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(customerId, "customerId cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");

        this.id = id;
        this.customerId = customerId;
        this.currency = currency;
        this.rentals = new ConcurrentHashMap<>();
        this.status = Status.NEW;
        this.totalPrice = Money.zero(getCurrency());
        this.totalLateCharge = Money.zero(getCurrency());
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public CurrencyUnit getCurrency() {
        return currency;
    }

    public Collection<String> getRentals() {
        return Collections.unmodifiableCollection(rentals.values());
    }

    public void addRental(Rental rental) {
        Objects.requireNonNull(rental, "Rental cannot be null");
        this.rentals.put(rental.getId(), rental.getId());
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        Objects.requireNonNull(status, "Order status cannot be null");
        this.status = status;
    }

    public boolean isClosed() {
        return status == Status.CLOSED;
    }

    public Money getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Money totalPrice) {
        Objects.requireNonNull(totalPrice);
        this.totalPrice = totalPrice;
    }

    public Money getTotalLateCharge() {
        return totalLateCharge;
    }

    public void setTotalLateCharge(Money lateCharge) {
        Objects.requireNonNull(lateCharge);
        this.totalLateCharge = lateCharge;
    }

    public Money getTotal() {
        return Money.total(getTotalPrice(), getTotalLateCharge());
    }

}
