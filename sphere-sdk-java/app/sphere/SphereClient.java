package sphere;

import java.util.Currency;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import de.commercetools.internal.util.Log;
import de.commercetools.internal.util.Util;
import de.commercetools.sphere.client.SphereException;
import de.commercetools.sphere.client.shop.*;
import de.commercetools.sphere.client.shop.model.Cart;
import de.commercetools.sphere.client.shop.model.Customer;
import de.commercetools.sphere.client.shop.model.CustomerName;
import sphere.util.IdWithVersion;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import net.jcip.annotations.ThreadSafe;

import javax.annotation.Nullable;

/** Client for accessing all Sphere APIs for building a store.
 *  To obtain an instance of this class designed to be shared by all the controllers in your application,
 *  use the static method {@link Sphere#getClient}. */
@ThreadSafe
public class SphereClient {
    private final ShopClient underlyingClient;
    private final Currency shopCurrency;
    private final Cart.InventoryMode cartInventoryMode;

    /** API for fetching and searching Products. */
    public final ProductService products;
    /** API for fetching categories. */
    public final CategoryTree categories;
    /** API for working with orders. */
    public final OrderService orders;
    /** API for customer operations that don't require customer id/version.
     *  Use currentCustomer() for the id/version related operations. */
    public final BasicCustomerService customers;
    /** API for product reviews. */
    public final ReviewService reviews;
    /** API for product comments. */
    public final CommentService comments;
    /** API for product inventory. */
    public final InventoryService inventory;

    SphereClient(Config config, ShopClient shopClient) {
        this.underlyingClient = shopClient;
        shopCurrency = config.shopCurrency();
        cartInventoryMode = config.cartInventoryMode();
        products = underlyingClient.products();
        categories = underlyingClient.categories();
        orders = underlyingClient.orders();
        customers = underlyingClient.customers();
        comments = underlyingClient.comments();
        reviews = underlyingClient.reviews();
        inventory = underlyingClient.inventory();
    }

    /** Cart object for the current session.
     *  @return A cart object if a customer is logged in. Dummy cart object with default values otherwise.*/
    public CurrentCart currentCart() {
        return new CurrentCart(underlyingClient.carts(), shopCurrency, cartInventoryMode);
    }

    /** Returns true if a customer is currently logged in, false otherwise.
     * This is equivalent to checking for {@code currentCustomer() != null} but more readable. */
    public boolean isLoggedIn() {
        return currentCustomer() != null;
    }

    /** Customer object for the current session.
     *  @return The current customer if a customer is logged in, null otherwise. */
    public CurrentCustomer currentCustomer() {
       return CurrentCustomer.createFromSession(
               underlyingClient.customers(), underlyingClient.orders(),
               underlyingClient.comments(), underlyingClient.reviews());
    }

    /** Authenticates a customer and stores customer id in the session.
     *  Returns true if a customer with given email and password exists, false otherwise.
     *
     *  If this methods returns true, {@link #currentCustomer()} will keep returning a {@link CurrentCustomer} instance
     *  until {@link #logout()} is called. */
    public boolean login(String email, String password) {
        return Util.sync(loginAsync(email, password)).isPresent();
    }

    /** Authenticates an existing customer asynchronously and store customer id in the session when finished. */
    public ListenableFuture<Optional<AuthenticatedCustomerResult>> loginAsync(String email, String password) {
        if (Strings.isNullOrEmpty(email) || Strings.isNullOrEmpty(password)) {
            throw new IllegalArgumentException("Please provide a non-empty email and password.");
        }
        Log.trace(String.format("[login] Logging in user with email %s.", email));
        Session session = Session.current();
        IdWithVersion sessionCartId = session.getCartId();
        ListenableFuture<Optional<AuthenticatedCustomerResult>> future;
        if (sessionCartId == null) {
            future = underlyingClient.customers().byCredentials(email, password).fetchAsync();
        } else {
            future = underlyingClient.carts().loginWithAnonymousCart(
                    sessionCartId.id(), sessionCartId.version(), email, password).executeAsync();
        }
        return Session.withCustomerAndCartOptional(future, session);
    }

    /** Creates a new customer and authenticates the customer (you don't need  {@link #login}). */
    public Customer signup(String email, String password, CustomerName customerName) {
        return Util.sync(signupAsync(email, password, customerName));
    }

    /** Creates a new customer asynchronously and authenticates the customer (calls {@link #login}). */
    public ListenableFuture<Customer> signupAsync(String email, String password, CustomerName customerName) {
        Log.trace(String.format("[signup] Signing up user with email %s.", email));
        Session session = Session.current();
        IdWithVersion sessionCartId = session.getCartId();
        ListenableFuture<Customer> customerFuture;
        if (sessionCartId == null) {
             customerFuture = Session.withCustomerIdAndVersion(
                     underlyingClient.customers().signup(
                             email,
                             password,
                             customerName.getFirstName(),
                             customerName.getLastName(),
                             customerName.getMiddleName(),
                             customerName.getTitle()
                     ).executeAsync(),
                     session);
        } else {
            ListenableFuture<AuthenticatedCustomerResult> signupFuture = Session.withCustomerAndCart(
                    underlyingClient.customers().signupWithCart(
                            email,
                            password,
                            customerName.getFirstName(),
                            customerName.getLastName(),
                            customerName.getMiddleName(),
                            customerName.getTitle(),
                            sessionCartId.id(),
                            sessionCartId.version()
                    ).executeAsync(),
                    session);
            customerFuture = Futures.transform(signupFuture, new Function<AuthenticatedCustomerResult, Customer>() {
                public Customer apply(@Nullable AuthenticatedCustomerResult result) {
                    assert result != null;
                    return result.getCustomer();
                }
            });
        }
        return customerFuture;
    }

    /** Removes the customer and cart information from the session.
     *
     *  After logout, {@link #currentCustomer()} will return null.
     *  Make sure you don't keep old {@link CurrentCustomer} instances around because they become invalid after logout. */
    public void logout() {
        Session session = Session.current();
        session.clearCustomer();
        session.clearCart();
    }
}
