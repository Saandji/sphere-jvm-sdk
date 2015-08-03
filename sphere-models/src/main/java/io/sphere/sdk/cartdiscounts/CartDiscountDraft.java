package io.sphere.sdk.cartdiscounts;

import io.sphere.sdk.models.Base;
import io.sphere.sdk.models.LocalizedStrings;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;

/**
 *
 * @see CartDiscountDraftBuilder
 */
public class CartDiscountDraft extends Base {
    private final LocalizedStrings name;
    @Nullable
    private final LocalizedStrings description;
    private final CartDiscountValue value;
    private final String cartPredicate;
    private final CartDiscountTarget target;
    private final String sortOrder;
    private final Boolean isActive;
    @Nullable
    private final ZonedDateTime validFrom;
    @Nullable
    private final ZonedDateTime validUntil;
    private final Boolean requiresDiscountCode;

    CartDiscountDraft(final LocalizedStrings name, final CartPredicate cartPredicate, final LocalizedStrings description, final CartDiscountValue value, final CartDiscountTarget target, final String sortOrder, final Boolean isActive, final ZonedDateTime validFrom, final ZonedDateTime validUntil, final Boolean requiresDiscountCode) {
        this.cartPredicate = cartPredicate.toSphereCartPredicate();
        this.name = name;
        this.description = description;
        this.value = value;
        this.target = target;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.requiresDiscountCode = requiresDiscountCode;
    }

    public String getCartPredicate() {
        return cartPredicate;
    }

    @Nullable
    public LocalizedStrings getDescription() {
        return description;
    }

    public Boolean isActive() {
        return isActive;
    }

    public LocalizedStrings getName() {
        return name;
    }

    public Boolean isRequiresDiscountCode() {
        return requiresDiscountCode;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public CartDiscountTarget getTarget() {
        return target;
    }

    @Nullable
    public ZonedDateTime getValidFrom() {
        return validFrom;
    }

    @Nullable
    public ZonedDateTime getValidUntil() {
        return validUntil;
    }

    public CartDiscountValue getValue() {
        return value;
    }
}