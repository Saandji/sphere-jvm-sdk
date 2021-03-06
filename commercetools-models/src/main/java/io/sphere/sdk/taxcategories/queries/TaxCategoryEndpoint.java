package io.sphere.sdk.taxcategories.queries;

import io.sphere.sdk.client.JsonEndpoint;
import io.sphere.sdk.taxcategories.TaxCategory;

final class TaxCategoryEndpoint {
    static final JsonEndpoint<TaxCategory> ENDPOINT = JsonEndpoint.of(TaxCategory.typeReference(), "/tax-categories");
}
