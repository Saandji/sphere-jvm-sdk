package io.sphere.sdk.products;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.utils.ListUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

class ProductCatalogDataImpl extends Base implements ProductCatalogData {
    @JsonProperty("published")
    private final boolean isPublished;
    @JsonProperty("hasStagedChanges")
    private final boolean hasStagedChanges;
    @Nullable
    private final ProductData current;
    private final ProductData staged;

    @JsonCreator
    ProductCatalogDataImpl(final boolean isPublished, final ProductData current, final ProductData staged,
                           final boolean hasStagedChanges) {
        this.isPublished = isPublished;
        this.current = current;
        this.staged = staged;
        this.hasStagedChanges = hasStagedChanges;
    }

    public boolean isPublished() {
        return isPublished;
    }

    @Nullable
    public ProductData getCurrent() {
        return isPublished ? current : null;
    }

    public ProductData getStaged() {
        return staged;
    }

    public boolean hasStagedChanges() {
        return hasStagedChanges;
    }

    public void setProductId(final String id) {
        final List<ProductData> currentAsList = Optional.ofNullable(current).map(c -> asList(c)).orElse(Collections.emptyList());
        ListUtils.listOf(currentAsList, staged).stream()
                .filter(x -> x instanceof ProductDataImpl)
                .forEach(x -> ((ProductDataImpl)x).setProductId(id));
    }
}