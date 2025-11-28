package com.luixtech.springbootframework.config.apidoc;

import com.luixtech.springbootframework.config.LuixProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.Getter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.core.Ordered;

/**
 * AnApi customizer to set up {@link io.swagger.v3.oas.models.OpenAPI}.
 */
@Getter
public class NamedApiCustomizer implements OpenApiCustomizer, Ordered {

    /**
     * The default order for the customizer.
     */
    private       int                    order;
    private final LuixProperties.ApiDocs apiDocsProperties;
    private final String                 name;
    private final String                 title;
    private final String                 description;
    private final String                 version;

    /**
     * <p>Constructor for OpenApiCustomizer.</p>
     *
     * @param apiDocsProperties a {@link LuixProperties.ApiDocs} object.
     * @param name              name
     * @param title             title
     * @param description       description
     * @param version           version
     * @param order             order
     */
    public NamedApiCustomizer(LuixProperties.ApiDocs apiDocsProperties,
                              String name,
                              String title,
                              String description,
                              String version,
                              int order) {
        this.apiDocsProperties = apiDocsProperties;
        this.name = name;
        this.title = title;
        this.description = description;
        this.version = version;
        this.order = order;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void customise(OpenAPI openAPI) {
        Contact contact = new Contact()
                .name(apiDocsProperties.getContactName())
                .url(apiDocsProperties.getContactUrl())
                .email(apiDocsProperties.getContactEmail());

        openAPI.info(new Info()
                .contact(contact)
                .title(title)
                .description(description)
                .version(version)
                .termsOfService(apiDocsProperties.getTermsOfServiceUrl())
                .license(new License().name(apiDocsProperties.getLicense()).url(apiDocsProperties.getLicenseUrl()))
        );

        for (LuixProperties.ApiDocs.Server server : apiDocsProperties.getServers()) {
            openAPI.addServersItem(new Server().url(server.getUrl()).description(server.getDescription()));
        }
    }
}
