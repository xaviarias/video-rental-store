package net.xas.vrs.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Created by Xavi on 08/02/16.
 */
public class Rental {

    public enum Status {
        ONGOING, RETURNED
    }

    private final String id;
    private final String orderId;
    private final int numberOfDays;
    private final LocalDateTime pickupDate;
    private final String filmId;

    private Status status;
    private Double price;
    private Double lateCharge;
    private LocalDateTime returnDate;

    public Rental(String id, String orderId, int numberOfDays,
                  LocalDateTime pickupDate, String filmId) {

        if (numberOfDays < 1)
            throw new IllegalArgumentException("numberOfDays");

        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(orderId, "orderId");
        Objects.requireNonNull(pickupDate, "pickupDate");
        Objects.requireNonNull(filmId, "filmId");

        this.id = id;
        this.orderId = orderId;
        this.status = Status.ONGOING;
        this.numberOfDays = numberOfDays;
        this.pickupDate = pickupDate;
        this.filmId = filmId;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getPickupDate() {
        return pickupDate;
    }

    public String getFilmId() {
        return filmId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getLateCharge() {
        return lateCharge;
    }

    public void setLateCharge(Double lateCharge) {
        this.lateCharge = lateCharge;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return status == Status.RETURNED;
    }

    public int getExtraDays() {
        int days = isReturned() ?
                (int) ChronoUnit.DAYS.between(pickupDate, returnDate) :
                (int) ChronoUnit.DAYS.between(pickupDate, LocalDateTime.now());

        return Math.max(0, days - numberOfDays);
    }

}
