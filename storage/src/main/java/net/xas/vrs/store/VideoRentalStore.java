package net.xas.vrs.store;

import net.xas.vrs.model.Customer;
import net.xas.vrs.model.Film;
import net.xas.vrs.model.Order;
import net.xas.vrs.model.Rental;

import java.util.Collection;

/**
 * Created by Xavi on 08/02/16.
 */
public interface VideoRentalStore {

    void save(Order order);

    void update(Order rental);

    Order retrieveOrder(String orderId);

    void save(Rental rental);

    void update(Rental rental);

    Rental retrieveRental(String rentalId);

    Collection<Rental> retrieveAllRentals();

    Film retrieveFilm(String filmId);

    Collection<Film> retrieveAllFilms();

    Customer retrieveCustomer(String customerId);

    Collection<Customer> retrieveAllCustomers();

}
