package com.example.springjavasdkdatadogtest.web;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartDraft;
import com.commercetools.api.models.cart.CartDraftBuilder;
import com.commercetools.api.models.common.LocalizedString;
import com.commercetools.api.models.type.FieldTypeBuilder;
import com.commercetools.api.models.type.ResourceTypeId;
import com.commercetools.api.models.type.Type;
import com.commercetools.api.models.type.TypeDraft;
import com.commercetools.api.models.type.TypeDraftBuilder;
import com.commercetools.api.models.type.TypeTextInputHint;
import io.vrap.rmf.base.client.ApiHttpResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.util.Arrays;

@Controller
public class AppController {

    @Autowired
    @Resource(name = "apiRoot")
    ProjectApiRoot apiRoot;

    @Autowired
    @Resource(name = "apiRootWithSerializer")
    ProjectApiRoot apiRootWithSerializer;

    private String cartId;

    @PostConstruct
    public void init() {
        cartId = apiRoot.carts().withKey("test-cart").get().execute().thenApply(ApiHttpResponse::getBody).thenApply(Cart::getId).join();

        if (cartId == null) {
            TypeDraft typeDraft = TypeDraftBuilder.of()
                    .key("my-type-key-" + Instant.now().getNano())
                    .name(LocalizedString.ofEnglish("my-type-name"))
                    .resourceTypeIds(Arrays.asList(
                            ResourceTypeId.ORDER))
                    .plusFieldDefinitions(
                            fieldDefinitionBuilder -> fieldDefinitionBuilder.type(FieldTypeBuilder::dateTimeBuilder)
                                    .name("ttl")
                                    .label(LocalizedString.ofEnglish("ttl"))
                                    .required(false)
                                    .inputHint(TypeTextInputHint.SINGLE_LINE))
                    .build();

            Type type = apiRoot.types().create(typeDraft).execute().thenApply(ApiHttpResponse::getBody).join();

            CartDraft cartDraft = CartDraftBuilder.of()
                    .currency("EUR").country("DE")
                    .key("test-cart")
                    .withCustom(customFieldsDraft -> customFieldsDraft.type(type.toResourceIdentifier())
                            .fields(fieldContainerBuilder -> fieldContainerBuilder.addValue("ttl", "2024-11-11T03:55:48.970653Z")).build())
                    .build();
            cartId = apiRoot.carts().post(cartDraft).execute().thenApply(ApiHttpResponse::getBody).thenApply(Cart::getId).join();
        }
    }


    @GetMapping("/")
    public String home(Model model) {
        Cart cart = apiRoot.carts().withId(cartId).get().execute().thenApply(ApiHttpResponse::getBody).join();
        Cart cartWithSerializer = apiRootWithSerializer.carts().withId(cartId).get().execute().thenApply(ApiHttpResponse::getBody).join();
        model.addAttribute("cart", cart);
        model.addAttribute("cartWithSerializer", cartWithSerializer);
        return "home";
    }
}
