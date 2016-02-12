package net.xas.vrs.api.resource;

import net.xas.vrs.domain.VideoRentalService;
import net.xas.vrs.model.Film;

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
 * Created by Xavi on 08/02/16.
 */
@Singleton
@Path("films")
@Produces(MediaType.APPLICATION_JSON)
public class FilmResource {

    private final VideoRentalService service;

    @Inject
    public FilmResource(VideoRentalService service) {
        this.service = service;
    }

    @GET
    @Valid
    @Path("/{filmId}")
    public Film retrieveFilm(@NotNull @PathParam("filmId") String filmId) {
        return service.retrieveFilm(filmId);
    }

    @GET
    @Valid
    @NotNull
    public Collection<Film> listFilms() {
        return service.listFilms();
    }

}
