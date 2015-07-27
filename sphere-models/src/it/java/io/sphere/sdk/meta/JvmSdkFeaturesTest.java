package io.sphere.sdk.meta;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.queries.CartByCustomerIdFetch;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereRequest;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.queries.ProductByIdFetch;

import java.util.concurrent.CompletionStage;

public class JvmSdkFeaturesTest {
    private SphereClient getSphereClient() {
        return new SphereClient() {
            @Override
            public <T> CompletionStage<T> execute(final SphereRequest<T> sphereRequest) {
                return null;
            }

            @Override
            public void close() {

            }
        };
    }

    public void usageDummy() throws Exception {
        parallelExecutionDemo(getSphereClient());
        asyncExecutionDemo(getSphereClient());
        recoverDemo(getSphereClient());

    }

    public CompletionStage<Html> parallelExecutionDemo(final SphereClient client) {
        final CompletionStage<Product> productStage =
                client.execute(ProductByIdFetch.of("product-id"));
        final CompletionStage<Cart> cartStage =
                client.execute(CartByCustomerIdFetch.of("customer-id"));
        return productStage.thenCombine(cartStage, (Product product, Cart cart) -> {
            final String productData = "product: " + product;
            final String cartData = "cart: " + cart;
            return renderTemplate(productData + " " + cartData);
        });
    }

    public void asyncExecutionDemo(final SphereClient client) {
        final CompletionStage<Product> productStage =
                client.execute(ProductByIdFetch.of("product-id"));
        productStage.thenAccept((Product product) ->
                        System.err.println(
                                "(2) This will be printed when the product arrives," +
                                "maybe in another thread.")
        );
        System.err.println("(1) This thread is not blocked " +
                "and this text will be immediately printed even before (2).");
    }

    public void recoverDemo(final SphereClient client) {
        final CompletionStage<Product> productStage =
                client.execute(ProductByIdFetch.of("product-id"));
        final CompletionStage<Html> htmlStage = productStage
                .thenApply((Product product) -> renderTemplate("product: " + product));
        final CompletionStage<Html> failSafeHtmlStage =
                htmlStage.exceptionally(
                        (Throwable t) -> renderTemplate("Ooops, an error occured."));
    }

    private Html renderTemplate(final String s) {
        return new Html(s);
    }

    private static class Html {

        public Html(final String s) {

        }
    }
}