package net.xas.vrs.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.*;

/**
 * Generic exception mapper.
 */
class BaseExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        Map<String, String> message = new HashMap<>();
        message.put("message", exception.getMessage());
        Set<Map> content = Collections.singleton(message);

        if (exception instanceof NoSuchElementException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(content).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(content).build();
    }

}
