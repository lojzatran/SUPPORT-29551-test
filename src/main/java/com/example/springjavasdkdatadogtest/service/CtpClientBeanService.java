package com.example.springjavasdkdatadogtest.service;

import com.commercetools.api.client.ApiInternalLoggerFactory;
import com.commercetools.api.client.ConcurrentModificationMiddleware;
import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.json.ApiModuleOptions;
import io.vrap.rmf.base.client.ResponseSerializer;
import io.vrap.rmf.base.client.http.ErrorMiddleware;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import io.vrap.rmf.base.client.utils.json.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class CtpClientBeanService {

    @Value(value = "${ctp.client.id}")
    private String clientId;

    @Value(value = "${ctp.client.secret}")
    private String clientSecret;

    @Value(value = "${ctp.project.key}")
    private String projectKey;

    private ClientCredentials credentials() {
        return ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build();
    }
//
//    @Bean
//    public ProjectApiRoot apiRoot() {
//        ApiRootBuilder builder = ApiRootBuilder.of()
//                .withErrorMiddleware(ErrorMiddleware.ExceptionMode.UNWRAP_COMPLETION_EXCEPTION)
//                .defaultClient(credentials());
//
//        final ProjectApiRoot build = ProjectApiRoot.fromClient(projectKey, builder.buildClient());
//        return build;
//    }

    @Bean
    public ProjectApiRoot apiRootWithSerializer() {
        ProjectApiRoot projectApiRoot =
                ApiRootBuilder.of()
                        .defaultClient(credentials())
                        .withProjectKey(projectKey)
                        .withSerializer(ResponseSerializer.of(
                                JsonUtils
                                        .createObjectMapper(ApiModuleOptions.of()
                                                .withDateAttributeAsString(true)
                                                .withDateCustomFieldAsString(true)
                                        )))
                        .addConcurrentModificationMiddleware()
                        .addAcceptGZipMiddleware()
                        .withMiddleware(ConcurrentModificationMiddleware.of(5))
                        .withInternalLoggerFactory(ApiInternalLoggerFactory::get)
                        .withErrorMiddleware(ErrorMiddleware.ExceptionMode.UNWRAP_COMPLETION_EXCEPTION)
                        .buildProjectRoot();
        return projectApiRoot;
    }
}
