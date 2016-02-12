package net.xas.vrs.domain;

import net.xas.vrs.commons.VideoRentalSettings;
import net.xas.vrs.model.Customer;
import net.xas.vrs.model.Film;
import net.xas.vrs.model.Order;
import net.xas.vrs.model.Rental;
import net.xas.vrs.store.VideoRentalStore;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.InputStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Service facade for video rental operations.
 */
@Singleton
public class VideoRentalService {

    private static final Logger LOG = LoggerFactory.getLogger(VideoRentalService.class);

    private Clock clock;

    private final VideoRentalStore store;
    private final VideoRentalSettings settings;

    private final DmnEngine dmnEngine;
    private final Map<CurrencyUnit, DmnDecision> priceRules;

    public VideoRentalService(VideoRentalStore store,
                              VideoRentalSettings settings) {
        this.store = store;
        this.settings = settings;
        this.priceRules = new ConcurrentHashMap<>(10);

        this.dmnEngine = DmnEngineConfiguration
                .createDefaultDmnEngineConfiguration().buildEngine();
    }

    public Customer retrieveCustomer(String customerId) {
        Objects.requireNonNull(customerId);
        Customer customer = store.retrieveCustomer(customerId);

        if (customer == null) {
            String message = format("Customer [%s] not exists.", customerId);
            throw new NoSuchElementException(message);
        }

        return customer;
    }

    public Order createOrder(String customerId, CurrencyUnit currency) {
        Objects.requireNonNull(customerId, "customerId cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");

        Customer customer = store.retrieveCustomer(customerId);
        Objects.requireNonNull(customer, format("Customer [%s] not exists.", customerId));

        Order order = new Order(UUID.randomUUID().toString(), customerId, currency);
        store.save(order);

        LOG.info("Created new order [{}]", order.getId());
        return order;
    }

    public Order retrieveOrder(String orderId) {
        Objects.requireNonNull(orderId);
        Order order = store.retrieveOrder(orderId);

        if (order == null) {
            String message = format("Order [%s] not exists.", orderId);
            throw new NoSuchElementException(message);
        }

        return order;
    }

    public Rental retrieveRental(String rentalId) {
        Objects.requireNonNull(rentalId);
        Rental rental = store.retrieveRental(rentalId);

        if (rental == null) {
            String message = format("Rental [%s] not exists.", rentalId);
            throw new NoSuchElementException(message);
        }

        return rental;
    }

    public Film retrieveFilm(String filmId) {
        Objects.requireNonNull(filmId);
        Film film = store.retrieveFilm(filmId);

        if (film == null) {
            String message = format("Film [%s] not exists.", filmId);
            throw new NoSuchElementException(message);
        }

        return film;
    }

    public Collection<Film> listFilms() {
        return store.retrieveAllFilms();
    }


    public Rental rent(String orderId, String filmId, int numberOfDays) {
        Objects.requireNonNull(orderId, "orderId");
        Objects.requireNonNull(filmId, "filmId");

        Order order = store.retrieveOrder(orderId);
        Objects.requireNonNull(order, "orderId");

        if (order.isClosed()) {
            String message = format("Order [%s] is already closed.", orderId);
            throw new IllegalStateException(message);
        }

        Film film = store.retrieveFilm(filmId);
        Objects.requireNonNull(film, "filmId");

        Rental rental = new Rental(UUID.randomUUID().toString(), orderId,
                numberOfDays, LocalDateTime.now(getClock()), filmId);

        Customer customer = store.retrieveCustomer(order.getCustomerId());
        customer.addBonusPoints(film.getType() == Film.Type.NEW ? 2 : 1);

        order.addRental(rental);
        store.save(rental);

        LOG.info("New rental [{}].", rental.getId());
        return rental;
    }

    public Rental returnRental(String rentalId) {
        Objects.requireNonNull(rentalId, "rentalId");
        Rental rental = store.retrieveRental(rentalId);

        if (rental == null) {
            String message = format("Rental [%s] not exists.", rentalId);
            throw new NoSuchElementException(message);
        }

        if (rental.isReturned()) {
            String message = format("Rental [%s] is already returned.", rentalId);
            throw new IllegalStateException(format(message, rentalId));
        }

        rental.setReturnDate(LocalDateTime.now(getClock()));
        rental.setStatus(Rental.Status.RETURNED);

        Order order = store.retrieveOrder(rental.getOrderId());
        calculatePrice(rental, order.getCurrency());
        store.update(rental);

        LOG.info("Rental [{}] returned.", rentalId);
        return rental;
    }

    public Order billOrder(String orderId) {
        Objects.requireNonNull(orderId, "orderId");
        Order order = store.retrieveOrder(orderId);

        if (order.isClosed()) {
            String message = format("Order [%s] is already closed.", orderId);
            throw new IllegalStateException(message);
        }

        // Validate order rentals
        if (order.getRentals().parallelStream()
                .map(store::retrieveRental)
                .anyMatch(r -> !r.isReturned())) {
            String message = format("Order [%s] has ongoing rentals.", orderId);
            throw new IllegalStateException(message);
        }

        order.setStatus(Order.Status.CLOSED);
        calculatePrice(order);
        store.update(order);

        LOG.info("Order [{}] billed with price: {}", orderId, order.getTotal());
        return order;
    }

    Clock getClock() {
        return clock != null ? clock : Clock.systemDefaultZone();
    }

    void setClock(Clock clock) {
        this.clock = clock;
    }

    private void calculatePrice(Order order) {

        // Retrieve rentals from storage
        Collection<Rental> rentals = order.getRentals()
                .parallelStream()
                .map(store::retrieveRental)
                .collect(Collectors.toList());

        // Calculate total price
        double price = rentals.stream()
                .map(Rental::getPrice)
                .reduce(0d, Double::sum);

        order.setTotalPrice(Money.of(order.getCurrency(), price));

        // Calculate late charges
        double lateCharges = rentals.stream()
                .map(Rental::getLateCharge)
                .filter(Objects::nonNull)
                .reduce(0d, Double::sum);

        order.setTotalLateCharge(Money.of(order.getCurrency(), lateCharges));
    }

    private void calculatePrice(Rental rental, CurrencyUnit currency) {

        // Retrieve rental film
        Film film = store.retrieveFilm(rental.getFilmId());

        // Constant variables
        VariableMap variables = Variables.createVariables()
                .putValue("premiumPrice", settings.getPremiumPrice().getAmount().doubleValue())
                .putValue("basicPrice", settings.getBasicPrice().getAmount().doubleValue())
                .putValue("numberOfDays", rental.getNumberOfDays())
                .putValue("extraDays", rental.getExtraDays())
                .putValue("type", film.getType().name());

        // Calculate prices and charges using DMN decision table
        DmnDecision priceRule = getPriceRulesFor(currency);
        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(priceRule, variables);

        Double price = (Double) result.get(0).get("price");
        Double lateCharges = (Double) result.get(0).get("lateCharges");

        rental.setPrice(price != null ? price : 0);
        rental.setLateCharge(lateCharges != null ? lateCharges : 0);
    }

    private DmnDecision getPriceRulesFor(CurrencyUnit currency) {
        if (priceRules.get(currency) == null) {
            String rulesFile = String.format("price-rules_%s.dmn", currency);
            InputStream rulesData = getClass().getClassLoader().getResourceAsStream(rulesFile);

            DmnModelInstance rules = Dmn.readModelFromStream(rulesData);
            priceRules.put(currency, dmnEngine.parseDecision("priceDecision", rules));
        }

        return priceRules.get(currency);
    }

}
