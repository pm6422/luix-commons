package com.luixtech.springbootframework.config.apidoc;

import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springdoc.core.properties.SpringDocConfigProperties.GroupConfig;
import static org.springdoc.core.utils.Constants.DEFAULT_GROUP_NAME;

/**
 * <p>OpenApiEndpoint class.</p>
 */
@WebEndpoint(id = "luixopenapigroups")
public class OpenApiEndpoint {

    private final SpringDocConfigProperties springDocConfigProperties;
    private final String                    appName;

    /**
     * <p>Constructor for OpenApiEndpoint.</p>
     *
     * @param springDocConfigProperties a {@link org.springdoc.core.properties.SpringDocConfigProperties} object.
     */
    public OpenApiEndpoint(SpringDocConfigProperties springDocConfigProperties, String appName) {
        this.springDocConfigProperties = springDocConfigProperties;
        this.appName = appName;
    }

    /**
     * GET /management/luixopenapigroups
     * <p>
     * Give openApi displayed on OpenApi page
     *
     * @return a Map with a String defining a category of openApi as Key and
     * another Map containing openApi related to this category as Value
     */
    @ReadOperation
    public List<Map<String, String>> allOpenApi() {
        return springDocConfigProperties.getGroupConfigs().stream().map(this::createGroupMap).collect(Collectors.toList());
    }

    private Map<String, String> createGroupMap(GroupConfig group) {
        Map<String, String> map = new HashMap<>();
        String groupName = group.getGroup();
        map.put("group", groupName);
        String description = appName + " (" + (DEFAULT_GROUP_NAME.equals(groupName) ? "default" : groupName) + ")";
        map.put("description", description);
        return map;
    }
}
