package io.sphere.sdk.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sphere.sdk.http.HttpResponse;
import io.sphere.sdk.json.SphereJsonUtils;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.utils.CompletableFutureUtils;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static io.sphere.sdk.utils.CompletableFutureUtils.successful;

/**
 * A factory to instantiate test double {@link SphereClient}s clients which use {@link CompletionStage} as future implementation.

 */
public final class TestDoubleSphereClientFactory extends Base {
    private TestDoubleSphereClientFactory() {
    }

    /**
     * Creates a test double for a SPHERE.IO client which enables to fake http responses from SPHERE.IO.
     * The client does not need an internet connection.
     *
     * {@include.example io.sphere.sdk.client.TestsDemo#withJson()}
     *
     * @param function a function which returns a matching object for a SPHERE.IO request.
     * @return sphere client test double
     */
    public static SphereClient createHttpTestDouble(final Function<HttpRequestIntent, HttpResponse> function) {
        return new SphereClient() {
            private final ObjectMapper objectMapper = SphereJsonUtils.newObjectMapper();

            @Override
            public <T> CompletionStage<T> execute(final SphereRequest<T> sphereRequest) {
                final HttpRequestIntent httpRequest = sphereRequest.httpRequestIntent();
                final HttpResponse httpResponse = function.apply(httpRequest);
                try {
                    final T t = SphereClientImpl.parse(sphereRequest, objectMapper, SphereApiConfig.of("fake-project-key-for-testing", "https://createHttpTestDouble.tld"), httpResponse);
                    return CompletableFutureUtils.successful(t);
                } catch (final Exception e) {
                    return CompletableFutureUtils.failed(e);
                }
            }

            @Override
            public void close() {
            }

            @Override
            public String toString() {
                return "SphereClientHttpTestDouble";
            }
        };
    }

    /**
     * Creates a test double for a SPHERE.IO client which enables to fake the results of the client as Java object.
     * The client does not need an internet connection.
     *
     * {@include.example io.sphere.sdk.client.TestsDemo#modelInstanceFromJson()}
     *
     * @param function a function which returns a matching http request for a SPHERE.IO request.
     * @return sphere client test double
     */
    @SuppressWarnings("unchecked")
    public static SphereClient createObjectTestDouble(final Function<HttpRequestIntent, Object> function) {
        return new SphereClient() {
            @Override
            public <T> CompletionStage<T> execute(final SphereRequest<T> sphereRequest) {
                final T result = (T) function.apply(sphereRequest.httpRequestIntent());
                return successful(result);
            }

            @Override
            public void close() {
            }

            @Override
            public String toString() {
                return "SphereClientObjectTestDouble";
            }
        };
    }
}
