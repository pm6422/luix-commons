package com.luixtech.springbootframework.config.api;

import com.luixtech.springbootframework.config.LuixProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.Getter;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.core.Ordered;

/**
 * An Api customizer to set up {@link OpenAPI}
 */
@Getter
public class ApiCustomizer implements OpenApiCustomiser, Ordered {
    private final int                    order = 0;
    private final LuixProperties.ApiDocs apiDocsProperties;
    private final String                 name;
    private final String                 title;
    private final String                 description;
    private final String                 version;

    public ApiCustomizer(LuixProperties.ApiDocs apiDocsProperties,
                         String name,
                         String title,
                         String description,
                         String version) {
        this.apiDocsProperties = apiDocsProperties;
        this.name = name;
        this.title = title;
        this.description = description;
        this.version = version;
    }

    public void customise(OpenAPI openApi) {
        Contact contact = new Contact()
                .name(apiDocsProperties.getContactName())
                .url(apiDocsProperties.getContactUrl())
                .email(apiDocsProperties.getContactEmail());

        openApi.info(new Info()
                .contact(contact)
                .title(title)
                .description(description)
                .version(version)
                .termsOfService(apiDocsProperties.getTermsOfServiceUrl())
                .license(new License().name(apiDocsProperties.getLicense()).url(apiDocsProperties.getLicenseUrl()))
        );

        for (LuixProperties.ApiDocs.Server server : apiDocsProperties.getServers()) {
            openApi.addServersItem(new Server().url(server.getUrl()).description(server.getDescription()));
        }
    }

    @Override
    public String toString() {
        return name;
    }
}