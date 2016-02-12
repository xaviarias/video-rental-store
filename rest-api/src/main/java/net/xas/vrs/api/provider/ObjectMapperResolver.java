package net.xas.vrs.api.provider;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * JSON object mapper customizations.
 */
@Provider
public class ObjectMapperResolver implements ContextResolver<ObjectMapper> {

    private final ObjectMapper defaultObjectMapper;

    public ObjectMapperResolver() {
        defaultObjectMapper = new ObjectMapper();
        defaultObjectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        defaultObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        defaultObjectMapper.registerModule(new VideoRentalModule());
        defaultObjectMapper.findAndRegisterModules(); // For JSR-310
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return defaultObjectMapper;
    }

    private static class VideoRentalModule extends SimpleModule {
        public VideoRentalModule() {
            addSerializer(Money.class, new JsonSerializer<Money>() {
                @Override
                public void serialize(Money money, JsonGenerator gen,
                                      SerializerProvider serializers) throws IOException {
                    serializers.defaultSerializeValue(money.toString(), gen);
                }
            });

            addDeserializer(Money.class, new JsonDeserializer<Money>() {
                @Override
                public Money deserialize(JsonParser p, DeserializationContext ctxt)
                        throws IOException {
                    return Money.parse(p.readValueAs(String.class));
                }
            });

            addSerializer(CurrencyUnit.class, new JsonSerializer<CurrencyUnit>() {
                @Override
                public void serialize(CurrencyUnit value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
                    serializers.defaultSerializeValue(value.getCurrencyCode(), gen);
                }
            });

            addDeserializer(CurrencyUnit.class, new JsonDeserializer<CurrencyUnit>() {
                @Override
                public CurrencyUnit deserialize(JsonParser p, DeserializationContext ctxt)
                        throws IOException {
                    return CurrencyUnit.of(p.readValueAs(String.class));
                }
            });
        }
    }

}