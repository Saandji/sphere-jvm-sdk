package io.sphere.sdk.meta;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.queries.CartByCustomerIdFetch;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereRequest;
import io.sphere.sdk.orders.queries.OrderQuery;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.queries.ProductByIdFetch;
import io.sphere.sdk.utils.MoneyImpl;
import org.junit.Test;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryFormats;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.concurrent.CompletionStage;

import static io.sphere.sdk.models.DefaultCurrencyUnits.*;
import static java.util.Locale.*;
import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void moneyDemo() {
        final MonetaryAmount money = MoneyImpl.ofCents(123456, "USD");
        assertThat(MonetaryFormats.getAmountFormat(GERMANY).format(money))
                .overridingErrorMessage("German decimal separator is used")
                .isEqualTo("1.234,56 USD");
        assertThat(MonetaryFormats.getAmountFormat(US).format(money))
                .overridingErrorMessage("in US currency comes first")
                .isEqualTo("USD1,234.56");

        assertThat(Monetary.getCurrency(GERMANY))
                .overridingErrorMessage("find default currency for a country")
                .isEqualTo(EUR);
        assertThat(Monetary.getCurrency(US)).isEqualTo(USD);
    }

    @Test
    public void dateTimeDemo() {
        final ZonedDateTime dateTime = ZonedDateTime.parse("2015-07-09T07:46:40.230Z");
        final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        assertThat(dateTime.withZoneSameInstant(ZoneId.of("Europe/Berlin"))
                .format(formatter))
                .isEqualTo("09.07.2015 09:46");
        assertThat(dateTime.withZoneSameInstant(ZoneId.of("America/New_York"))
                .format(formatter))
                .isEqualTo("09.07.2015 03:46");
    }

    @Test
    public void countryCodeDemo() {
        final CountryCode countryCode = CountryCode.US;
        assertThat(countryCode.toLocale().getDisplayCountry(US))
                .isEqualTo("United States");
        assertThat(countryCode.toLocale().getDisplayCountry(GERMANY))
                .isEqualTo("Vereinigte Staaten von Amerika");
        assertThat(countryCode.toLocale().getDisplayCountry(FRANCE))
                .isEqualTo("Etats-Unis");

        assertThat(countryCode.getAlpha2())
                .isEqualTo("US");
    }
}