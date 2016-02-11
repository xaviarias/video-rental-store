package net.xas.vrs.api;

import net.xas.vrs.domain.VideoRentalService;
import net.xas.vrs.model.Order;
import org.joda.money.CurrencyUnit;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Resource for rental management.
 */
@Singleton
@Path("orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    private VideoRentalService service;

    @POST
    @Valid
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Order createOrder(@NotNull @FormParam("customerId") String customerId,
                             @NotNull @FormParam("currency") String currencyCode) {
        return service.createOrder(customerId, CurrencyUnit.of(currencyCode));
    }

    @GET
    @Valid
    @Path("{orderId}")
    public Order retrieveOrder(@NotNull @PathParam("orderId") String orderId) {
        return service.retrieveOrder(orderId);
    }

    @PUT
    @Valid
    @Path("{orderId}")
    public Order billOrder(@NotNull @PathParam("orderId") String orderId) {
        return service.billOrder(orderId);
    }

    @Valid
    @Path("{orderId}/rentals")
    public Class<RentalResource> rentalsResource(@PathParam("orderId") String orderId) {
        return RentalResource.class;
    }

}