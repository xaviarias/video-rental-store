package net.xas.vrs.api.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mapper for client HTTP errors.
 */
@Provider
public class ClientErrorExceptionMapper
        implements ExceptionMapper<ClientErrorException> {

    private static final Logger LOG = LoggerFactory.getLogger(ClientErrorExceptionMapper.class);

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(ClientErrorException exception) {
        LOG.info(uriInfo.getAbsolutePath() + ": " + exception.getMessage());
        return exception.getResponse();
    }

}
