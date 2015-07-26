package io.sphere.sdk.orders.commands.updateactions;

import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.ReturnItemDraft;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.List;

/**

 {@include.example io.sphere.sdk.orders.commands.OrderUpdateCommandTest#addReturnInfo()}
 */
public class AddReturnInfo extends UpdateAction<Order> {
    @Nullable
    private final ZonedDateTime returnDate;
    @Nullable
    private final String returnTrackingId;
    private final List<ReturnItemDraft> items;

    private AddReturnInfo(final List<ReturnItemDraft> items, @Nullable final ZonedDateTime returnDate, @Nullable final String returnTrackingId) {
        super("addReturnInfo");
        this.returnDate = returnDate;
        this.returnTrackingId = returnTrackingId;
        this.items = items;
    }

    public static AddReturnInfo of(final List<ReturnItemDraft> items, @Nullable final ZonedDateTime returnDate, @Nullable final String returnTrackingId) {
        return new AddReturnInfo(items, returnDate, returnTrackingId);
    }

    public static AddReturnInfo of(final List<ReturnItemDraft> items) {
        return of(items, null, null);
    }

    public AddReturnInfo withReturnDate(final ZonedDateTime returnDate) {
        return of(items, returnDate, returnTrackingId);
    }

    public AddReturnInfo withReturnTrackingId(final String returnTrackingId) {
        return of(items, returnDate, returnTrackingId);
    }

    @Nullable
    public ZonedDateTime getReturnDate() {
        return returnDate;
    }

    @Nullable
    public String getReturnTrackingId() {
        return returnTrackingId;
    }

    public List<ReturnItemDraft> getItems() {
        return items;
    }
}