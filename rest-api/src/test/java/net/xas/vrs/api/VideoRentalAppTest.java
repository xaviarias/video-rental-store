package net.xas.vrs.api;

import net.xas.vrs.api.provider.ObjectMapperResolver;
import net.xas.vrs.model.Customer;
import net.xas.vrs.model.Film;
import net.xas.vrs.model.Order;
import net.xas.vrs.model.Rental;
import org.glassfish.jersey.test.JerseyTest;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Video rental application REST API tests.
 */
public class VideoRentalAppTest extends JerseyTest {

    private final CurrencyUnit SEK = CurrencyUnit.of("SEK");
    private final Entity<String> EMPTY = Entity.text("");

    public VideoRentalAppTest() {
        super(new VideoRentalApp());
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        client().register(ObjectMapperResolver.class);
    }

    @Test
    public void testNominal() throws Exception {
        Order order = createOrder(findAnyCustomer());
        assertThat(order.getStatus()).isEqualTo(Order.Status.NEW);

        Rental rental = createRental(order, findAnyFilm());
        assertThat(rental.getStatus()).isEqualTo(Rental.Status.ONGOING);

        rental = returnRental(rental);
        assertThat(rental.getStatus()).isEqualTo(Rental.Status.RETURNED);

        order = billOrder(order);
        assertThat(order.getStatus()).isEqualTo(Order.Status.CLOSED);
    }

    @Test
    public void testErrors() throws Exception {

        // Retrieve nonexistent order
        Response response = target("orders/orderId").request().get();
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);

        // Retrieve nonexistent film
        response = target("films/filmId").request().get();
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);

        // Retrieve nonexistent customer
        response = target("customers/customerId").request().get();
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);

        // Retrieve nonexistent rental
        Order order = createOrder(findAnyCustomer());
        response = target("orders/{orderId}/rentals/rentalId")
                .resolveTemplate("orderId", order.getId())
                .request().get();

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);

        // Return nonexistent rental
        response = target("orders/{orderId}/rentals/rentalId")
                .resolveTemplate("orderId", order.getId())
                .request().put(EMPTY);

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);

        // Rent nonexistent film
        response = target("orders/{orderId}/rentals")
                .resolveTemplate("orderId", order.getId())
                .request().post(Entity.form(new Form()
                        .param("filmId", "filmId")
                        .param("numberOfDays", "1")));

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);

        Rental rental = createRental(order, findAnyFilm());
        returnRental(rental);

        // Return film more than once
        response = target("orders/{orderId}/rentals/{rentalId}")
                .resolveTemplate("orderId", rental.getOrderId())
                .resolveTemplate("rentalId", rental.getId())
                .request().put(EMPTY);

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);

        billOrder(order);

        // Bill order more than once
        response = target("orders/{orderId}")
                .resolveTemplate("orderId", order.getId())
                .request().put(EMPTY);

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);

        // Rental after billing
        response = target("orders/{orderId}/rentals")
                .resolveTemplate("orderId", order.getId())
                .request().post(Entity.form(new Form()
                        .param("filmId", findAnyFilm().getId())
                        .param("numberOfDays", "1")));

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
    }

    private Order createOrder(Customer customer) {
        return target("orders").request().post(Entity.form(new Form()
                        .param("customerId", customer.getId())
                        .param("currency", SEK.getCurrencyCode())),
                Order.class);
    }

    private Rental createRental(Order order, Film film) {
        return target("orders/{orderId}/rentals")
                .resolveTemplate("orderId", order.getId())
                .request().post(Entity.form(new Form()
                                .param("filmId", film.getId())
                                .param("numberOfDays", "1")),
                        Rental.class);
    }

    private Rental returnRental(Rental rental) {
        return target("orders/{orderId}/rentals/{rentalId}")
                .resolveTemplate("orderId", rental.getOrderId())
                .resolveTemplate("rentalId", rental.getId())
                .request().put(EMPTY, Rental.class);
    }

    private Order billOrder(Order order) {
        return target("orders/{orderId}")
                .resolveTemplate("orderId", order.getId())
                .request().put(EMPTY, Order.class);
    }

    private Customer findAnyCustomer() {
        Collection<Customer> customers = target("customers").request()
                .get(new GenericType<Collection<Customer>>() {
                });

        return customers.stream().findAny().get();
    }

    private Film findAnyFilm() {
        Collection<Film> films = target("films").request()
                .get(new GenericType<Collection<Film>>() {
                });

        return films.stream().findAny().get();
    }

}
