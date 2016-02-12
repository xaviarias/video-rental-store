package net.xas.vrs.api.resource;

import net.xas.vrs.domain.VideoRentalService;
import net.xas.vrs.model.Customer;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

/**
 * Resource for customers.
 */
@Singleton
@Path("customers")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {

    private final VideoRentalService service;

    @Inject
    public CustomerResource(VideoRentalService service) {
        this.service = service;
    }

    @GET
    @Valid
    @NotNull
    @Path("{customerId}")
    public Customer retrieveCustomer(@NotNull @PathParam("customerId") String customerId) {
        return service.retrieveCustomer(customerId);
    }

    @GET
    @Valid
    @NotNull
    public Collection<Customer> listCustomers() {
        return service.listCustomers();
    }

}
