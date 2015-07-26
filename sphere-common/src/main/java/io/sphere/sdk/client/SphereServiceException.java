package io.sphere.sdk.client;

import com.fasterxml.jackson.databind.JsonNode;
import io.sphere.sdk.json.SphereJsonUtils;
import io.sphere.sdk.models.SphereException;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 *
 * <span id="exception-summary">Exception thrown when SPHERE.IO responds<br>with a status code other than HTTP 2xx.</span>
 *
 */
public abstract class SphereServiceException extends SphereException {
    static final long serialVersionUID = 0L;
    private final int statusCode;

    public SphereServiceException(final int statusCode) {
        this.statusCode = statusCode;
    }

    public SphereServiceException(final String message, final int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public SphereServiceException(final Throwable cause, final int statusCode) {
        super(cause);
        this.statusCode = statusCode;
    }

    public SphereServiceException(final String message, final Throwable cause, final int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Nullable
    public final JsonNode getJsonBody() {
        return Optional.ofNullable(httpResponse)
                .map(r -> r.getResponseBody())
                .map(body -> SphereJsonUtils.parse(body))
                .orElse(null);
    }
}