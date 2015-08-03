package io.sphere.sdk.discountcodes.queries;

import io.sphere.sdk.discountcodes.DiscountCode;
import io.sphere.sdk.queries.DefaultModelQueryModelImpl;
import io.sphere.sdk.queries.QueryModel;
import io.sphere.sdk.queries.StringQuerySortingModel;

public class DiscountCodeQueryModel extends DefaultModelQueryModelImpl<DiscountCode> {

    public static DiscountCodeQueryModel of() {
        return new DiscountCodeQueryModel(null, null);
    }

    private DiscountCodeQueryModel(QueryModel<DiscountCode> parent, String pathSegment) {
        super(parent, pathSegment);
    }


    public StringQuerySortingModel<DiscountCode> code() {
        return stringModel("code");
    }
}