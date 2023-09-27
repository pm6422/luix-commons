
package com.luixtech.springbootframework.config.info;

import com.luixtech.springbootframework.config.LuixProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * An {@link org.springframework.boot.actuate.info.InfoContributor} that exposes the list of active spring profiles.
 */
public class AppInfoContributor implements InfoContributor {

    private final String  ribbonProfile;
    private final boolean apiDocsEnabled;

    /**
     * <p>Constructor for ActiveProfilesInfoContributor.</p>
     *
     * @param env                       a {@link ConfigurableEnvironment} object.
     * @param springDocConfigProperties a {@link SpringDocConfigProperties} object.
     * @param luixProperties            a {@link LuixProperties} object.
     */
    public AppInfoContributor(ConfigurableEnvironment env,
                              SpringDocConfigProperties springDocConfigProperties,
                              LuixProperties luixProperties) {
        apiDocsEnabled = springDocConfigProperties.getApiDocs().isEnabled();
        String[] profilesArray = env.getActiveProfiles().length == 0
                ? env.getDefaultProfiles()
                : env.getActiveProfiles();
        List<String> displayRibbonOnProfiles = luixProperties.getRibbon().getDisplayOnActiveProfiles();
        if (CollectionUtils.isEmpty(displayRibbonOnProfiles)) {
            ribbonProfile = StringUtils.EMPTY;
            return;
        }
        Collection<String> intersection = CollectionUtils.intersection(Arrays.asList(profilesArray),
                displayRibbonOnProfiles);
        ribbonProfile = intersection.isEmpty() ? StringUtils.EMPTY : intersection.iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("apiDocsEnabled", apiDocsEnabled);
        builder.withDetail("ribbonProfile", ribbonProfile);
    }
}
