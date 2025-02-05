package com.example.springjavasdkdatadogtest.json;

import java.util.Optional;

import com.commercetools.api.json.ApiModuleOptions;
import com.commercetools.api.json.AtrributeDeserializer;
import com.commercetools.api.json.CustomFieldDeserializer;
import com.commercetools.api.json.ReplicaCartDraftMixin;
import com.commercetools.api.models.cart.ReplicaCartDraft;
import com.commercetools.api.models.product.AttributeImpl;
import com.commercetools.api.models.review.Review;
import com.commercetools.api.models.review.ReviewMixin;
import com.commercetools.api.models.type.FieldContainerImpl;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.vrap.rmf.base.client.utils.json.modules.ModuleOptions;


public class CustomJsonModule extends SimpleModule {

    public CustomJsonModule(ModuleOptions options) {
        boolean attributeAsDateString = Boolean.parseBoolean(
                Optional.ofNullable(options.getOption(ApiModuleOptions.DESERIALIZE_DATE_ATTRIBUTE_AS_STRING))
                        .orElse(System.getProperty(ApiModuleOptions.DESERIALIZE_DATE_ATTRIBUTE_AS_STRING)));
        boolean customFieldAsDateString = Boolean
                .parseBoolean(Optional.ofNullable(options.getOption(ApiModuleOptions.DESERIALIZE_DATE_FIELD_AS_STRING))
                        .orElse(System.getProperty(ApiModuleOptions.DESERIALIZE_DATE_FIELD_AS_STRING)));
        addDeserializer(AttributeImpl.class, new AtrributeDeserializer(attributeAsDateString));
        addDeserializer(FieldContainerImpl.class, new CustomFieldDeserializer(customFieldAsDateString));
        setMixInAnnotation(Review.class, ReviewMixin.class);
        setMixInAnnotation(ReplicaCartDraft.class, ReplicaCartDraftMixin.class);
    }
}
