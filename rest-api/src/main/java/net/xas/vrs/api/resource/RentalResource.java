package net.xas.vrs.api.resource;

import net.xas.vrs.domain.VideoRentalService;
import net.xas.vrs.model.Order;
import net.xas.vrs.model.Rental;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by Xavi on 11/02/16.
 */
@RequestScoped
@Path("{orderId}/rentals")
@Produces(MediaType.APPLICATION_JSON)
public class RentalResource {

    private final VideoRentalService service;

    @Valid
    @NotNull
    @PathParam("orderId")
    private String orderId;

    @Inject
    public RentalResource(VideoRentalService service) {
        this.service = service;
    }

    @POST
    @Valid
    @NotNull
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Rental createRental(@NotNull @FormParam("filmId") String filmId,
                               @NotNull @FormParam("numberOfDays") @Min(1) int numberOfDays) {
        return service.createRental(orderId, filmId, numberOfDays);
    }


    @GET
    @Valid
    @NotNull
    @Path("{rentalId}")
    public Rental retrieveRental(@NotNull @PathParam("rentalId") String rentalId) {
        return service.retrieveRental(rentalId);
    }

    @GET
    @Valid
    @NotNull
    public Collection<Rental> listRentals() {
        Order order = service.retrieveOrder(orderId);
        return order.getRentals().parallelStream()
                .map(service::retrieveRental)
                .collect(Collectors.toList());
    }

    @PUT
    @Valid
    @NotNull
    @Path("{rentalId}")
    public Rental returnRental(@NotNull @PathParam("rentalId") String rentalId) {
        return service.returnRental(rentalId);
    }

}
