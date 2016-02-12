package net.xas.vrs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

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
    private final Collection<String> rentals;

    private Status status = Status.NEW;

    private Money totalPrice;
    private Money totalLateCharge;

    @JsonCreator
    public Order(@JsonProperty("id") String id,
                 @JsonProperty("customerId") String customerId,
                 @JsonProperty("currency") CurrencyUnit currency) {

        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(customerId, "customerId cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");

        this.id = id;
        this.customerId = customerId;
        this.currency = currency;
        this.rentals = new ArrayList<>();
        this.totalPrice = Money.zero(currency);
        this.totalLateCharge = Money.zero(currency);
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
        return Collections.unmodifiableCollection(rentals);
    }

    public void addRental(String rentalId) {
        Objects.requireNonNull(rentalId, "Rental ID be null");
        this.rentals.add(rentalId);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        Objects.requireNonNull(status, "Order status cannot be null");
        this.status = status;
    }

    @JsonIgnore
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

}
