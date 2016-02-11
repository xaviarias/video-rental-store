package net.xas.vrs.store;

import net.xas.vrs.model.Customer;
import net.xas.vrs.model.Film;
import net.xas.vrs.model.Order;
import net.xas.vrs.model.Rental;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.unmodifiableCollection;

/**
 * In-memory storage for tests.
 */
public class InMemoryVideoRentalStore
        implements VideoRentalStore {

    private Map<String, Film> films = new ConcurrentHashMap<>();
    private Map<String, Order> orders = new ConcurrentHashMap<>();
    private Map<String, Rental> rentals = new ConcurrentHashMap<>();
    private Map<String, Customer> customers = new ConcurrentHashMap<>();

    public InMemoryVideoRentalStore() {
        initFilms();
        initCustomers();
    }

    @Override
    public void save(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        orders.put(order.getId(), order);
    }

    @Override
    public void update(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");

        if (rentals.get(order.getId()) == null) {
            throw new NoSuchElementException();
        }

        save(order);
    }

    @Override
    public Order retrieveOrder(String orderId) {
        Objects.requireNonNull(orderId, "Order cannot be null");
        return orders.get(orderId);
    }

    @Override
    public void save(Rental rental) {
        Objects.requireNonNull(rental, "Rental cannot be null");
        rentals.put(rental.getId(), rental);
    }

    @Override
    public void update(Rental rental) {
        Objects.requireNonNull(rental, "Rental cannot be null");

        if (rentals.get(rental.getId()) == null) {
            throw new NoSuchElementException();
        }

        save(rental);
    }

    @Override
    public Rental retrieveRental(String rentalId) {
        Objects.requireNonNull(rentalId, "Rental cannot be null");
        return rentals.get(rentalId);
    }

    public Collection<Rental> retrieveAllRentals() {
        return unmodifiableCollection(rentals.values());
    }

    @Override
    public Film retrieveFilm(String filmId) {
        return films.get(filmId);
    }

    public Collection<Film> retrieveAllFilms() {
        return unmodifiableCollection(films.values());
    }

    @Override
    public Customer retrieveCustomer(String customerId) {
        return customers.get(customerId);
    }

    @Override
    public Collection<Customer> retrieveAllCustomers() {
        return unmodifiableCollection(customers.values());
    }

    private void initCustomers() {
        String customerId = UUID.randomUUID().toString();
        customers.put(customerId, new Customer(customerId, "Patty"));

        customerId = UUID.randomUUID().toString();
        customers.put(customerId, new Customer(customerId, "Tom"));
    }

    private void initFilms() {
        String filmId = UUID.randomUUID().toString();
        films.put(filmId, new Film(filmId, "Terminator 13", Film.Type.NEW));

        filmId = UUID.randomUUID().toString();
        films.put(filmId, new Film(filmId, "Wolverine", Film.Type.REGULAR));

        filmId = UUID.randomUUID().toString();
        films.put(filmId, new Film(filmId, "Wolverine 2", Film.Type.REGULAR));

        filmId = UUID.randomUUID().toString();
        films.put(filmId, new Film(filmId, "Freaks", Film.Type.OLD));
    }

}
