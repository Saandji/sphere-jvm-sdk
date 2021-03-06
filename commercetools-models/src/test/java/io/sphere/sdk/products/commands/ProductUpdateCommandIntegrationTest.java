package io.sphere.sdk.products.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.messages.queries.MessageQuery;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.models.EnumValue;
import io.sphere.sdk.models.LocalizedEnumValue;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.MetaAttributes;
import io.sphere.sdk.models.Reference;
import io.sphere.sdk.products.CategoryOrderHints;
import io.sphere.sdk.products.Image;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.products.PriceDraft;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.ProductData;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductProjectionType;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeAccess;
import io.sphere.sdk.products.attributes.AttributeDraft;
import io.sphere.sdk.products.attributes.NamedAttributeAccess;
import io.sphere.sdk.products.commands.updateactions.AddExternalImage;
import io.sphere.sdk.products.commands.updateactions.AddPrice;
import io.sphere.sdk.products.commands.updateactions.AddToCategory;
import io.sphere.sdk.products.commands.updateactions.AddVariant;
import io.sphere.sdk.products.commands.updateactions.ChangeName;
import io.sphere.sdk.products.commands.updateactions.ChangePrice;
import io.sphere.sdk.products.commands.updateactions.ChangeSlug;
import io.sphere.sdk.products.commands.updateactions.MetaAttributesUpdateActions;
import io.sphere.sdk.products.commands.updateactions.Publish;
import io.sphere.sdk.products.commands.updateactions.RemoveFromCategory;
import io.sphere.sdk.products.commands.updateactions.RemoveImage;
import io.sphere.sdk.products.commands.updateactions.RemovePrice;
import io.sphere.sdk.products.commands.updateactions.RemoveVariant;
import io.sphere.sdk.products.commands.updateactions.RevertStagedChanges;
import io.sphere.sdk.products.commands.updateactions.SetAttribute;
import io.sphere.sdk.products.commands.updateactions.SetAttributeInAllVariants;
import io.sphere.sdk.products.commands.updateactions.SetCategoryOrderHint;
import io.sphere.sdk.products.commands.updateactions.SetDescription;
import io.sphere.sdk.products.commands.updateactions.SetMetaDescription;
import io.sphere.sdk.products.commands.updateactions.SetMetaKeywords;
import io.sphere.sdk.products.commands.updateactions.SetMetaTitle;
import io.sphere.sdk.products.commands.updateactions.SetPrices;
import io.sphere.sdk.products.commands.updateactions.SetProductPriceCustomField;
import io.sphere.sdk.products.commands.updateactions.SetProductPriceCustomType;
import io.sphere.sdk.products.commands.updateactions.SetSearchKeywords;
import io.sphere.sdk.products.commands.updateactions.SetTaxCategory;
import io.sphere.sdk.products.commands.updateactions.TransitionState;
import io.sphere.sdk.products.commands.updateactions.Unpublish;
import io.sphere.sdk.products.messages.ProductStateTransitionMessage;
import io.sphere.sdk.products.queries.ProductByIdGet;
import io.sphere.sdk.products.queries.ProductProjectionByIdGet;
import io.sphere.sdk.products.queries.ProductQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.search.SearchKeyword;
import io.sphere.sdk.search.SearchKeywords;
import io.sphere.sdk.search.tokenizer.CustomSuggestTokenizer;
import io.sphere.sdk.suppliers.TShirtProductTypeDraftSupplier.Colors;
import io.sphere.sdk.suppliers.TShirtProductTypeDraftSupplier.Sizes;
import io.sphere.sdk.taxcategories.TaxCategoryFixtures;
import io.sphere.sdk.test.IntegrationTest;
import io.sphere.sdk.test.SphereTestUtils;
import io.sphere.sdk.utils.MoneyImpl;
import org.junit.Test;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

import static io.sphere.sdk.models.DefaultCurrencyUnits.EUR;
import static io.sphere.sdk.products.ProductFixtures.withProductAndUnconnectedCategory;
import static io.sphere.sdk.products.ProductFixtures.withProductInCategory;
import static io.sphere.sdk.products.ProductFixtures.withUpdateablePricedProduct;
import static io.sphere.sdk.products.ProductFixtures.withUpdateableProduct;
import static io.sphere.sdk.states.StateFixtures.withStateByBuilder;
import static io.sphere.sdk.states.StateType.PRODUCT_STATE;
import static io.sphere.sdk.suppliers.TShirtProductTypeDraftSupplier.MONEY_ATTRIBUTE_NAME;
import static io.sphere.sdk.test.SphereTestUtils.DE;
import static io.sphere.sdk.test.SphereTestUtils.EURO_10;
import static io.sphere.sdk.test.SphereTestUtils.MASTER_VARIANT_ID;
import static io.sphere.sdk.test.SphereTestUtils.asList;
import static io.sphere.sdk.test.SphereTestUtils.randomKey;
import static io.sphere.sdk.test.SphereTestUtils.randomSlug;
import static io.sphere.sdk.types.TypeFixtures.STRING_FIELD_NAME;
import static io.sphere.sdk.types.TypeFixtures.withUpdateableType;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductUpdateCommandIntegrationTest extends IntegrationTest {
    public static final Random RANDOM = new Random();

    @Test
    public void addExternalImage() throws Exception {
        withUpdateableProduct(client(), product -> {
            assertThat(product.getMasterData().getStaged().getMasterVariant().getImages()).hasSize(0);

            final Image image = Image.ofWidthAndHeight("http://www.commercetools.com/assets/img/ct_logo_farbe.gif", 460, 102, "commercetools logo");
            final Product updatedProduct = client().executeBlocking(ProductUpdateCommand.of(product, AddExternalImage.of(image, MASTER_VARIANT_ID)));

            assertThat(updatedProduct.getMasterData().getStaged().getMasterVariant().getImages()).isEqualTo(asList(image));
            return updatedProduct;
        });
    }

    @Test
    public void addPrice() throws Exception {
        final PriceDraft expectedPrice = PriceDraft.of(MoneyImpl.of(123, EUR));
        testAddPrice(expectedPrice);
    }

    @Test
    public void addPriceYen() throws Exception {
        final PriceDraft expectedPrice = PriceDraft.of(MoneyImpl.of(new BigDecimal("12345"), "JPY"));
        testAddPrice(expectedPrice);
    }

    @Test
    public void addPriceWithValidityPeriod() throws Exception {
        final PriceDraft expectedPrice = PriceDraft.of(MoneyImpl.of(123, EUR))
                .withValidFrom(SphereTestUtils.now())
                .withValidUntil(SphereTestUtils.now().withZoneSameLocal(ZoneOffset.UTC).plusHours(2));
        testAddPrice(expectedPrice);
    }

    private void testAddPrice(final PriceDraft expectedPrice) throws Exception {
        withUpdateableProduct(client(), product -> {
            final Product updatedProduct = client()
                    .executeBlocking(ProductUpdateCommand.of(product, AddPrice.of(1, expectedPrice)));


            final List<Price> prices = updatedProduct.getMasterData().getStaged().getMasterVariant().getPrices();
            assertThat(prices).hasSize(1);
            final Price actualPrice = prices.get(0);

            assertThat(expectedPrice).isEqualTo(PriceDraft.of(actualPrice));

            return updatedProduct;
        });
    }

    @Test
    public void setPrices() throws Exception {
        final PriceDraft expectedPrice1 = PriceDraft.of(MoneyImpl.of(123, EUR));
        final PriceDraft expectedPrice2 = PriceDraft.of(MoneyImpl.of(123, EUR)).withCountry(CountryCode.DE);
        final List<PriceDraft> expectedPriceList = new ArrayList<>();
        expectedPriceList.add(expectedPrice1);
        expectedPriceList.add(expectedPrice2);

        withUpdateableProduct(client(), product -> {
            final Product updatedProduct = client()
                    .executeBlocking(ProductUpdateCommand.of(product, SetPrices.of(1, expectedPriceList)));


            final List<Price> prices = updatedProduct.getMasterData().getStaged().getMasterVariant().getPrices();
            assertThat(prices).hasSize(2);

            List<PriceDraft> draftPricesList = prices.stream().map(PriceDraft::of).collect(Collectors.toList());

            assertThat(draftPricesList).contains(expectedPrice1, expectedPrice2);

            return updatedProduct;
        });
    }

    @Test
    public void setPricesWithAlreadyExisting() {
        final PriceDraft expectedPrice1 = PriceDraft.of(MoneyImpl.of(123, EUR));
        final PriceDraft expectedPrice2 = PriceDraft.of(MoneyImpl.of(123, EUR)).withCountry(CountryCode.DE);
        final List<PriceDraft> expectedPriceList = new ArrayList<>();
        expectedPriceList.add(expectedPrice1);
        expectedPriceList.add(expectedPrice2);
        withUpdateablePricedProduct(client(), expectedPrice1, product -> {
            Price oldPrice = product.getMasterData().getStaged().getMasterVariant().getPrices().get(0);
            final Product updatedProduct = client()
                    .executeBlocking(ProductUpdateCommand.of(product, SetPrices.of(1, expectedPriceList)));


            final List<Price> newPrices = updatedProduct.getMasterData().getStaged().getMasterVariant().getPrices();
            assertThat(newPrices).hasSize(2);
            assertThat(newPrices).doesNotContain(oldPrice);

            List<PriceDraft> draftPricesList = newPrices.stream().map(PriceDraft::of).collect(Collectors.toList());

            assertThat(draftPricesList).contains(expectedPrice1, expectedPrice2);

            return updatedProduct;
        });
    }

    @Test
    public void setPricesEmptyList() {
        final List<PriceDraft> expectedPriceList = new ArrayList<>();
        withUpdateablePricedProduct(client(), product -> {
            final Product updatedProduct = client()
                    .executeBlocking(ProductUpdateCommand.of(product, SetPrices.of(1, expectedPriceList)));


            final List<Price> newPrices = updatedProduct.getMasterData().getStaged().getMasterVariant().getPrices();
            assertThat(newPrices).isEmpty();

            return updatedProduct;
        });
    }

    //and remove from category
    @Test
    public void addToCategory() throws Exception {
        withProductAndUnconnectedCategory(client(), (final Product product, final Category category) -> {
            assertThat(product.getMasterData().getStaged().getCategories()).isEmpty();

            final String orderHint = "0.123";
            final Product productWithCategory = client()
                    .executeBlocking(ProductUpdateCommand.of(product, AddToCategory.of(category, orderHint)));

            final Reference<Category> categoryReference = productWithCategory.getMasterData().getStaged().getCategories().stream().findAny().get();
            assertThat(categoryReference.referencesSameResource(category)).isTrue();
            assertThat(productWithCategory.getMasterData().getStaged().getCategoryOrderHints().get(category.getId())).isEqualTo(orderHint);

            final Product productWithoutCategory = client()
                    .executeBlocking(ProductUpdateCommand.of(productWithCategory, RemoveFromCategory.of(category)));

            assertThat(productWithoutCategory.getMasterData().getStaged().getCategories()).isEmpty();
        });
    }

    @Test
    public void setCategoryOrderHint() throws Exception {
        withProductInCategory(client(), (product, category) -> {
            final Product updatedProduct = client().executeBlocking(ProductUpdateCommand.of(product, SetCategoryOrderHint.of(category.getId(), "0.1234")));

            final CategoryOrderHints actual = updatedProduct.getMasterData().getStaged().getCategoryOrderHints();
            assertThat(actual).isEqualTo(CategoryOrderHints.of(category.getId(), "0.1234"));
            assertThat(actual.getAsMap()).isEqualTo(Collections.singletonMap(category.getId(), "0.1234"));
        });
    }

    @Test
    public void changeName() throws Exception {
        withUpdateableProduct(client(), product -> {
            final LocalizedString newName = LocalizedString.ofEnglish("newName " + RANDOM.nextInt());
            final Product updatedProduct = client().executeBlocking(ProductUpdateCommand.of(product, ChangeName.of(newName)));

            assertThat(updatedProduct.getMasterData().getStaged().getName()).isEqualTo(newName);
            return updatedProduct;
        });
    }

    @Test
    public void changePrice() throws Exception {
        withUpdateablePricedProduct(client(), product -> {
            final PriceDraft newPrice = PriceDraft.of(MoneyImpl.of(234, EUR));
            final List<Price> prices = product.getMasterData().getStaged().getMasterVariant()
                    .getPrices();
            assertThat(prices.stream().anyMatch(p -> p.equals(newPrice))).isFalse();

            final Product updatedProduct = client()
                    .executeBlocking(ProductUpdateCommand.of(product, ChangePrice.of(prices.get(0), newPrice)));

            final Price actualPrice = getFirstPrice(updatedProduct);
            assertThat(PriceDraft.of(actualPrice)).isEqualTo(newPrice);

            return updatedProduct;
        });
    }

    @Test
    public void changeSlug() throws Exception {
        withUpdateableProduct(client(), product -> {
            final LocalizedString newSlug = LocalizedString.ofEnglish("new-slug-" + RANDOM.nextInt());
            final Product updatedProduct = client().executeBlocking(ProductUpdateCommand.of(product, ChangeSlug.of(newSlug)));

            assertThat(updatedProduct.getMasterData().getStaged().getSlug()).isEqualTo(newSlug);
            return updatedProduct;
        });
    }

    @Test
    public void publish() throws Exception {
        withUpdateableProduct(client(), product -> {
            assertThat(product.getMasterData().isPublished()).isFalse();

            final Product publishedProduct = client().executeBlocking(ProductUpdateCommand.of(product, Publish.of()));
            assertThat(publishedProduct.getMasterData().isPublished()).isTrue();

            final Product unpublishedProduct = client().executeBlocking(ProductUpdateCommand.of(publishedProduct, Unpublish.of()));
            assertThat(unpublishedProduct.getMasterData().isPublished()).isFalse();
            return unpublishedProduct;
        });
    }

    @Test
    public void removeImage() throws Exception {
        final Image image = Image.ofWidthAndHeight("http://www.commercetools.com/assets/img/ct_logo_farbe.gif", 460, 102, "commercetools logo");
        withUpdateableProduct(client(), product -> {
            final Product productWithImage = client().executeBlocking(ProductUpdateCommand.of(product, AddExternalImage.of(image, MASTER_VARIANT_ID)));
            assertThat(productWithImage.getMasterData().getStaged().getMasterVariant().getImages()).isEqualTo(asList(image));

            final Product updatedProduct = client().executeBlocking(ProductUpdateCommand.of(productWithImage, RemoveImage.of(image, MASTER_VARIANT_ID)));
            assertThat(updatedProduct.getMasterData().getStaged().getMasterVariant().getImages()).hasSize(0);
            return updatedProduct;
        });
    }

    @Test
    public void removePrice() throws Exception {
        withUpdateablePricedProduct(client(), product -> {
            final Price oldPrice = getFirstPrice(product);

            final Product updatedProduct = client()
                    .executeBlocking(ProductUpdateCommand.of(product, RemovePrice.of(oldPrice)));

            assertThat(updatedProduct.getMasterData().getStaged().getMasterVariant()
                    .getPrices().stream().anyMatch(p -> p.equals(oldPrice))).isFalse();

            return updatedProduct;
        });
    }

    @Test
    public void setDescription() throws Exception {
        withUpdateableProduct(client(), product -> {
            final LocalizedString newDescription = LocalizedString.ofEnglish("new description " + RANDOM.nextInt());
            final ProductUpdateCommand cmd = ProductUpdateCommand.of(product, SetDescription.of(newDescription));
            final Product updatedProduct = client().executeBlocking(cmd);

            assertThat(updatedProduct.getMasterData().getStaged().getDescription()).isEqualTo(newDescription);
            return updatedProduct;
        });
    }

    @Test
    public void setMetaKeywords() throws Exception {
        withUpdateableProduct(client(), product -> {
            final LocalizedString metaKeywords = LocalizedString
                    .of(ENGLISH, "Platform-as-a-Service, e-commerce, http, api, tool");
            final SetMetaKeywords action = SetMetaKeywords.of(metaKeywords);

            final Product updatedProduct = client().executeBlocking(ProductUpdateCommand.of(product, action));

            assertThat(updatedProduct.getMasterData().getStaged().getMetaKeywords()).isEqualTo(metaKeywords);

            return updatedProduct;
        });
    }

    @Test
    public void setMetaDescription() throws Exception {
        withUpdateableProduct(client(), product -> {
            final LocalizedString metaDescription = LocalizedString
                    .of(ENGLISH, "SPHERE.IO&#8482; is the first Platform-as-a-Service solution for eCommerce.");
            final SetMetaDescription action = SetMetaDescription.of(metaDescription);

            final Product updatedProduct = client().executeBlocking(ProductUpdateCommand.of(product, action));

            assertThat(updatedProduct.getMasterData().getStaged().getMetaDescription()).isEqualTo(metaDescription);

            return updatedProduct;
        });
    }

    @Test
    public void setMetaTitle() throws Exception {
        withUpdateableProduct(client(), product -> {
            final LocalizedString metaTitle = LocalizedString
                    .of(ENGLISH, "commercetools SPHERE.IO&#8482; - Next generation eCommerce");
            final SetMetaTitle action = SetMetaTitle.of(metaTitle);

            final Product updatedProduct = client().executeBlocking(ProductUpdateCommand.of(product, action));

            assertThat(updatedProduct.getMasterData().getStaged().getMetaTitle()).isEqualTo(metaTitle);

            return updatedProduct;
        });
    }

    @Test
    public void setMetaAttributes() throws Exception {
        withUpdateableProduct(client(), product -> {
            final MetaAttributes metaAttributes = MetaAttributes.metaAttributesOf(ENGLISH,
                    "commercetools SPHERE.IO&#8482; - Next generation eCommerce",
                    "SPHERE.IO&#8482; is the first and leading Platform-as-a-Service solution for eCommerce.",
                    "Platform-as-a-Service, e-commerce, http, api, tool");
            final List<UpdateAction<Product>> updateActions =
                    MetaAttributesUpdateActions.of(metaAttributes);
            final Product updatedProduct = client().executeBlocking(ProductUpdateCommand.of(product, updateActions));

            final ProductData productData = updatedProduct.getMasterData().getStaged();
            assertThat(productData.getMetaTitle()).isEqualTo(metaAttributes.getMetaTitle());
            assertThat(productData.getMetaDescription()).isEqualTo(metaAttributes.getMetaDescription());
            assertThat(productData.getMetaKeywords()).isEqualTo(metaAttributes.getMetaKeywords());
            return updatedProduct;
        });
    }

    @Test
    public void productProjectionCanBeUsedToUpdateAProduct() throws Exception {
        withUpdateableProduct(client(), product -> {
            final MetaAttributes metaAttributes = MetaAttributes.metaAttributesOf(ENGLISH,
                    "commercetools SPHERE.IO&#8482; - Next generation eCommerce",
                    "SPHERE.IO&#8482; is the first and leading Platform-as-a-Service solution for eCommerce.",
                    "Platform-as-a-Service, e-commerce, http, api, tool");
            final List<UpdateAction<Product>> updateActions =
                    MetaAttributesUpdateActions.of(metaAttributes);

            final ProductProjection productProjection = client().executeBlocking(ProductProjectionByIdGet.of(product, ProductProjectionType.STAGED));

            final Product updatedProduct = client().executeBlocking(ProductUpdateCommand.of(productProjection, updateActions));

            final ProductData productData = updatedProduct.getMasterData().getStaged();
            assertThat(productData.getMetaTitle()).isEqualTo(metaAttributes.getMetaTitle());
            assertThat(productData.getMetaDescription()).isEqualTo(metaAttributes.getMetaDescription());
            assertThat(productData.getMetaKeywords()).isEqualTo(metaAttributes.getMetaKeywords());
            return updatedProduct;
        });
    }

    @Test
    public void setAttribute() throws Exception {
        withUpdateableProduct(client(), product -> {
            //the setter contains the name and a JSON mapper, declare it only one time in your project per attribute
            //example for MonetaryAmount attribute
            final String moneyAttributeName = MONEY_ATTRIBUTE_NAME;
            final NamedAttributeAccess<MonetaryAmount> moneyAttribute =
                    AttributeAccess.ofMoney().ofName(moneyAttributeName);
            final MonetaryAmount newValueForMoney = EURO_10;

            //example for LocalizedEnumValue attribute
            final NamedAttributeAccess<LocalizedEnumValue> colorAttribute = Colors.ATTRIBUTE;
            final LocalizedEnumValue oldValueForColor = Colors.GREEN;
            final LocalizedEnumValue newValueForColor = Colors.RED;

            assertThat(product.getMasterData().getStaged().getMasterVariant().findAttribute(moneyAttribute)).isEmpty();
            assertThat(product.getMasterData().getStaged().getMasterVariant().findAttribute(colorAttribute)).contains(oldValueForColor);

            final SetAttribute moneyUpdate = SetAttribute.of(MASTER_VARIANT_ID, moneyAttribute, newValueForMoney);
            final SetAttribute localizedEnumUpdate = SetAttribute.of(MASTER_VARIANT_ID, colorAttribute, newValueForColor);

            final Product updatedProduct = client().executeBlocking(ProductUpdateCommand.of(product, asList(moneyUpdate, localizedEnumUpdate)));
            assertThat(updatedProduct.getMasterData().getStaged().getMasterVariant().findAttribute(moneyAttribute)).contains(newValueForMoney);
            assertThat(updatedProduct.getMasterData().getStaged().getMasterVariant().findAttribute(colorAttribute)).contains(newValueForColor);

            final SetAttribute unsetAction = SetAttribute.ofUnsetAttribute(MASTER_VARIANT_ID, moneyAttribute);
            final Product productWithoutMoney = client().executeBlocking(ProductUpdateCommand.of(updatedProduct, unsetAction));

            assertThat(productWithoutMoney.getMasterData().getStaged().getMasterVariant().findAttribute(moneyAttribute)).isEmpty();

            return productWithoutMoney;
        });
    }

    @Test
    public void setAttributeInAllVariants() throws Exception {
        withUpdateableProduct(client(), product -> {
            //the setter contains the name and a JSON mapper, declare it only one time in your project per attribute
            //example for MonetaryAmount attribute
            final String moneyAttributeName = MONEY_ATTRIBUTE_NAME;
            final NamedAttributeAccess<MonetaryAmount> moneyAttribute =
                    AttributeAccess.ofMoney().ofName(moneyAttributeName);
            final MonetaryAmount newValueForMoney = EURO_10;

            //example for LocalizedEnumValue attribute
            final NamedAttributeAccess<LocalizedEnumValue> colorAttribute = Colors.ATTRIBUTE;
            final LocalizedEnumValue oldValueForColor = Colors.GREEN;
            final LocalizedEnumValue newValueForColor = Colors.RED;

            assertThat(product.getMasterData().getStaged().getMasterVariant().findAttribute(moneyAttribute)).isEmpty();
            assertThat(product.getMasterData().getStaged().getMasterVariant().findAttribute(colorAttribute)).contains(oldValueForColor);

            final SetAttributeInAllVariants moneyUpdate = SetAttributeInAllVariants.of(moneyAttribute, newValueForMoney);
            final SetAttributeInAllVariants localizedEnumUpdate = SetAttributeInAllVariants.of(colorAttribute, newValueForColor);

            final Product updatedProduct = client().executeBlocking(ProductUpdateCommand.of(product, asList(moneyUpdate, localizedEnumUpdate)));
            assertThat(updatedProduct.getMasterData().getStaged().getMasterVariant().findAttribute(moneyAttribute)).contains(newValueForMoney);
            assertThat(updatedProduct.getMasterData().getStaged().getMasterVariant().findAttribute(colorAttribute)).contains(newValueForColor);

            final SetAttributeInAllVariants unsetAction = SetAttributeInAllVariants.ofUnsetAttribute(moneyAttribute);
            final Product productWithoutMoney = client().executeBlocking(ProductUpdateCommand.of(updatedProduct, unsetAction));

            assertThat(productWithoutMoney.getMasterData().getStaged().getMasterVariant().findAttribute(moneyAttribute)).isEmpty();

            return productWithoutMoney;
        });
    }

    @Test
    public void revertStagedChanges() throws Exception {
        withUpdateableProduct(client(), product -> {
            //changing only staged and not current
            final LocalizedString oldDescriptionOption = product.getMasterData().getStaged().getDescription();
            final LocalizedString newDescription = LocalizedString.ofEnglish("new description " + RANDOM.nextInt());
            final ProductUpdateCommand cmd = ProductUpdateCommand.of(product, asList(Publish.of(), SetDescription.of(newDescription)));
            final Product updatedProduct = client().executeBlocking(cmd);
            assertThat(oldDescriptionOption).isNotEqualTo(newDescription);
            assertThat(updatedProduct.getMasterData().getStaged().getDescription()).isEqualTo(newDescription);
            assertThat(updatedProduct.getMasterData().getCurrent().getDescription()).isEqualTo(oldDescriptionOption);

            final Product revertedProduct = client().executeBlocking(ProductUpdateCommand.of(updatedProduct, RevertStagedChanges.of()));
            assertThat(revertedProduct.getMasterData().getStaged().getDescription()).isEqualTo(oldDescriptionOption);
            assertThat(revertedProduct.getMasterData().getCurrent().getDescription()).isEqualTo(oldDescriptionOption);

            return revertedProduct;
        });
    }

    @Test
    public void setTaxCategory() throws Exception {
        TaxCategoryFixtures.withTransientTaxCategory(client(), taxCategory ->
            withUpdateableProduct(client(), product -> {
                assertThat(product.getTaxCategory()).isNotEqualTo(taxCategory);
                final ProductUpdateCommand command = ProductUpdateCommand.of(product, SetTaxCategory.of(taxCategory));
                final Product updatedProduct = client().executeBlocking(command);
                assertThat(updatedProduct.getTaxCategory()).isEqualTo(taxCategory.toReference());
                return updatedProduct;
            })
        );
    }

    @Test
    public void setSearchKeywords() throws Exception {
        withUpdateableProduct(client(), product -> {
            final SearchKeywords searchKeywords = SearchKeywords.of(Locale.ENGLISH, asList(SearchKeyword.of("Raider", CustomSuggestTokenizer.of(singletonList("Twix")))));
            final ProductUpdateCommand command = ProductUpdateCommand.of(product, SetSearchKeywords.of(searchKeywords));
            final Product updatedProduct = client().executeBlocking(command);

            final SearchKeywords actualKeywords = updatedProduct.getMasterData().getStaged().getSearchKeywords();
            assertThat(actualKeywords).isEqualTo(searchKeywords);
            return updatedProduct;
        });
    }

    @Test
    public void addVariant() throws Exception {
        final NamedAttributeAccess<MonetaryAmount> moneyAttribute =
                AttributeAccess.ofMoney().ofName(MONEY_ATTRIBUTE_NAME);
        final AttributeDraft moneyAttributeValue = AttributeDraft.of(moneyAttribute, EURO_10);

        final NamedAttributeAccess<LocalizedEnumValue> colorAttribute = Colors.ATTRIBUTE;
        final LocalizedEnumValue color = Colors.RED;
        final AttributeDraft colorAttributeValue = AttributeDraft.of(colorAttribute, color);

        final NamedAttributeAccess<EnumValue> sizeAttribute = Sizes.ATTRIBUTE;
        final AttributeDraft sizeValue = AttributeDraft.of(sizeAttribute, Sizes.M);


        withUpdateableProduct(client(), product -> {
            assertThat(product.getMasterData().getStaged().getVariants()).isEmpty();

            final PriceDraft price = PriceDraft.of(MoneyImpl.of(new BigDecimal("12.34"), EUR)).withCountry(DE);
            final List<PriceDraft> prices = asList(price);
            final List<AttributeDraft> attributeValues = asList(moneyAttributeValue, colorAttributeValue, sizeValue);
            final ProductUpdateCommand addVariantCommand =
                    ProductUpdateCommand.of(product, AddVariant.of(attributeValues, prices, randomKey()));

            final Product productWithVariant = client().executeBlocking(addVariantCommand);
            final ProductVariant variant = productWithVariant.getMasterData().getStaged().getVariants().get(0);
            assertThat(variant.getId()).isEqualTo(2);
            assertThat(variant.findAttribute(moneyAttribute).get()).isEqualTo(EURO_10);
            assertThat(variant.findAttribute(colorAttribute).get()).isEqualTo(color);
            assertThat(variant.findAttribute(sizeAttribute).get()).isEqualTo(Sizes.M);

            final Product productWithoutVariant = client().executeBlocking(ProductUpdateCommand.of(productWithVariant, RemoveVariant.of(variant)));
            assertThat(productWithoutVariant.getMasterData().getStaged().getVariants()).isEmpty();

            return productWithoutVariant;
        });
    }

    private static class StagedWrapper extends Base implements UpdateAction<Product> {
        private final UpdateAction<Product> delegate;
        @JsonProperty("staged")
        private final boolean staged;

        private StagedWrapper(final UpdateAction<Product> action, final boolean staged) {
            this.delegate = action;
            this.staged = staged;
        }

        @Override
        public String getAction() {
            return delegate.getAction();
        }

        @JsonUnwrapped
        public UpdateAction<Product> getDelegate() {
            return delegate;
        }
    }

    @Test
    public void possibleToHackUpdateForStagedAndCurrent() throws Exception {
         withUpdateableProduct(client(), product -> {
             final LocalizedString newName = randomSlug();
             final UpdateAction<Product> stagedWrapper = new StagedWrapper(ChangeName.of(newName), false);
             final Product updatedProduct = client().executeBlocking(ProductUpdateCommand.of(product, asList(Publish.of(), stagedWrapper)));

             final Product fetchedProduct = client().executeBlocking(ProductByIdGet.of(product));
             assertThat(fetchedProduct.getMasterData().getCurrent().getName())
                     .isEqualTo(fetchedProduct.getMasterData().getStaged().getName())
                     .isEqualTo(newName);

             return updatedProduct;
         });
    }

    @Test
    public void transitionState() {
        withStateByBuilder(client(), builder -> builder.type(PRODUCT_STATE),  state -> {
            withUpdateableProduct(client(), product -> {
                assertThat(product.getState()).isNull();

                final Product updatedProduct = client().executeBlocking(ProductUpdateCommand.of(product, asList(TransitionState.of(state))));

                assertThat(updatedProduct.getState()).isEqualTo(state.toReference());

                final PagedQueryResult<ProductStateTransitionMessage> messageQueryResult = client().executeBlocking(MessageQuery.of()
                        .withPredicates(m -> m.resource().is(product))
                        .forMessageType(ProductStateTransitionMessage.MESSAGE_HINT));

                final ProductStateTransitionMessage message = messageQueryResult.head().get();
                assertThat(message.getState()).isEqualTo(state.toReference());

                //check query model
                final ProductQuery query = ProductQuery.of()
                        .withPredicates(m -> m.id().is(product.getId()).and(m.state().is(state)));
                final Product productByState = client().executeBlocking(query)
                        .head().get();
                assertThat(productByState).isEqualTo(updatedProduct);

                return updatedProduct;
            });
        });
    }

    @Test
    public void setProductPriceCustomTypeAndsetProductPriceCustomField() {
        withUpdateableType(client(), type -> {
            withUpdateablePricedProduct(client(), product -> {
                final String priceId = getFirstPrice(product).getId();
                final UpdateAction<Product> updateAction = SetProductPriceCustomType.
                        ofTypeIdAndObjects(type.getId(), STRING_FIELD_NAME, "a value", priceId);
                final ProductUpdateCommand productUpdateCommand = ProductUpdateCommand.of(product, updateAction);
                final Product updatedProduct = client().executeBlocking(productUpdateCommand);

                final Price price = getFirstPrice(updatedProduct);
                assertThat(price.getCustom().getFieldAsString(STRING_FIELD_NAME))
                        .isEqualTo("a value");

                final Product updated2 = client().executeBlocking(ProductUpdateCommand.of(updatedProduct, SetProductPriceCustomField.ofObject(STRING_FIELD_NAME, "a new value", priceId)));
                assertThat(getFirstPrice(updated2).getCustom().getFieldAsString(STRING_FIELD_NAME))
                        .isEqualTo("a new value");
                return updated2;
            });
            return type;
        });
    }

    private Price getFirstPrice(final Product product) {
        return product.getMasterData().getStaged().getMasterVariant().getPrices().get(0);
    }
}