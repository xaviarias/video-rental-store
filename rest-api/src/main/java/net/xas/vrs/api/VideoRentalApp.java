package net.xas.vrs.api;

import net.xas.vrs.commons.VideoRentalSettings;
import net.xas.vrs.domain.VideoRentalService;
import net.xas.vrs.store.InMemoryVideoRentalStore;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

/**
 * Video rental application class.
 */
@ApplicationPath("/api")
public class VideoRentalApp extends ResourceConfig {

    public VideoRentalApp() {
        final VideoRentalService service = new VideoRentalService(
                new InMemoryVideoRentalStore(), new VideoRentalSettings());

        // Register binder for service
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(new Factory<VideoRentalService>() {
                    @Override
                    public VideoRentalService provide() {
                        return service;
                    }

                    @Override
                    public void dispose(VideoRentalService instance) {
                    }
                }).to(VideoRentalService.class).in(Singleton.class);
            }
        });

        // Register resources
        registerClasses(CustomerResource.class,
                OrderResource.class,
                FilmResource.class);

        // Register providers and features
        registerClasses(BaseExceptionMapper.class,
                ObjectMapperProvider.class,
                JacksonFeature.class);

        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
    }

}
