package net.xas.vrs.api;

import net.xas.vrs.api.provider.BaseExceptionMapper;
import net.xas.vrs.api.provider.ClientErrorExceptionMapper;
import net.xas.vrs.api.provider.ObjectMapperResolver;
import net.xas.vrs.api.resource.CustomerResource;
import net.xas.vrs.api.resource.FilmResource;
import net.xas.vrs.api.resource.OrderResource;
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
                bindFactory(new VideoRentalServiceFactory(service))
                        .to(VideoRentalService.class).in(Singleton.class);
            }
        });

        // Register resources
        registerClasses(CustomerResource.class,
                OrderResource.class,
                FilmResource.class);

        // Register providers and features
        registerClasses(BaseExceptionMapper.class,
                ClientErrorExceptionMapper.class,
                ObjectMapperResolver.class,
                JacksonFeature.class);

        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
    }

    private static class VideoRentalServiceFactory
            implements  Factory<VideoRentalService> {

        private final VideoRentalService service;

        VideoRentalServiceFactory(VideoRentalService service) {
           this.service = service;
        }

        @Override
        public VideoRentalService provide() {
            return service;
        }

        @Override
        public void dispose(VideoRentalService instance) {
        }
    }

}
