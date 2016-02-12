package net.xas.vrs.domain;

import net.xas.vrs.commons.VideoRentalSettings;
import net.xas.vrs.model.Customer;
import net.xas.vrs.model.Film;
import net.xas.vrs.model.Order;
import net.xas.vrs.model.Rental;
import net.xas.vrs.store.InMemoryVideoRentalStore;
import net.xas.vrs.store.VideoRentalStore;
import org.joda.money.CurrencyUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Video rental service tests.
 */
public class VideoRentalServiceTest {

    private VideoRentalStore store;
    private VideoRentalService service;

    @Before
    public void setUp() throws Exception {
        store = new InMemoryVideoRentalStore();
        service = new VideoRentalService(store, new VideoRentalSettings());
    }

    @After
    public void tearDown() throws Exception {
        service.setClock(null);
    }

    /**
     * Example order:
     * <ul>
     * <li>Matrix 11 (New release) 1 days 40 SEK</li>
     * <li>Spider Man (Regular rental) 5 days 90 SEK</li>
     * <li>Spider Man 2 (Regular rental) 2 days 30 SEK</li>
     * <li>Out of Africa (Old film) 7 days 90 SEK</li>
     * </ul>
     * Total price: <b>250 SEK</b>
     * <p/>
     * When returning films late:
     * <ul>
     * <li>Matrix 11 (New release) 2 extra days 80 SEK</li>
     * <li>Spider Man (Regular rental) 1 days 30 SEK</li>
     * </ul>
     * Total late charge: <b>110 SEK</b>
     */
    @Test
    public void testRentalPrice() {
        Collection<Film> films = store.retrieveAllFilms();
        Collection<Customer> customers = store.retrieveAllCustomers();

        Customer customer = customers.iterator().next();
        Order order = service.createOrder(customer.getId(), CurrencyUnit.of("SEK"));

        Rental rental = service.createRental(order.getId(), films.stream()
                .filter(film -> film.getName().equals("Wolverine 2"))
                .findFirst().get().getId(), 2);

        rental = service.returnRental(rental.getId());
        assertThat(rental.getPrice()).isEqualTo(30);
        assertThat(rental.getLateCharge()).isEqualTo(0);

        rental = service.createRental(order.getId(), films.stream()
                .filter(film -> film.getName().equals("Freaks"))
                .findFirst().get().getId(), 7);

        rental = service.returnRental(rental.getId());
        assertThat(rental.getPrice()).isEqualTo(90);
        assertThat(rental.getLateCharge()).isEqualTo(0);

        rental = service.createRental(order.getId(), films.stream()
                .filter(film -> film.getName().equals("Terminator 13"))
                .findFirst().get().getId(), 1);

        // Simulate three days after
        Clock offsetClock = Clock.offset(service.getClock(), Duration.ofDays(3));
        service.setClock(offsetClock);

        rental = service.returnRental(rental.getId());
        assertThat(rental.getPrice()).isEqualTo(40);
        assertThat(rental.getLateCharge()).isEqualTo(80);

        service.setClock(null); // Reset service clock
        rental = service.createRental(order.getId(), films.stream()
                .filter(film -> film.getName().equals("Wolverine"))
                .findFirst().get().getId(), 5);

        // Simulate six days after
        offsetClock = Clock.offset(service.getClock(), Duration.ofDays(6));
        service.setClock(offsetClock);

        rental = service.returnRental(rental.getId());
        assertThat(rental.getPrice()).isEqualTo(90);
        assertThat(rental.getLateCharge()).isEqualTo(30);

        // Finish the order by billing
        service.billOrder(order.getId());
        assertThat(order.getTotalPrice().getAmount().doubleValue()).isEqualTo(250);
        assertThat(order.getTotalLateCharge().getAmount().doubleValue()).isEqualTo(110);

        assertThat(customer.getBonusPoints()).isEqualTo(5);
    }
}
