
package com.luixtech.springbootframework.config.info;

import com.luixtech.springbootframework.config.LuixProperties;
import org.apache.commons.collections4.CollectionUtils;
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
    private final boolean apiDocsEnabled;
    private final String  ribbonProfile;

    /**
     * <p>Constructor for ActiveProfilesInfoContributor.</p>
     *
     * @param env            a {@link ConfigurableEnvironment} object.
     * @param apiDocsEnabled API enabled.
     * @param luixProperties a {@link LuixProperties} object.
     */
    public AppInfoContributor(ConfigurableEnvironment env,
                              boolean apiDocsEnabled,
                              LuixProperties luixProperties) {
        this.apiDocsEnabled = apiDocsEnabled;
        String[] profilesArray = env.getActiveProfiles().length == 0
                ? env.getDefaultProfiles()
                : env.getActiveProfiles();
        List<String> displayRibbonOnProfiles = luixProperties.getRibbon().getDisplayOnActiveProfiles();
        if (CollectionUtils.isEmpty(displayRibbonOnProfiles)) {
            ribbonProfile = null;
            return;
        }
        Collection<String> intersection = CollectionUtils.intersection(Arrays.asList(profilesArray),
                displayRibbonOnProfiles);
        ribbonProfile = intersection.isEmpty() ? null : intersection.iterator().next();
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
