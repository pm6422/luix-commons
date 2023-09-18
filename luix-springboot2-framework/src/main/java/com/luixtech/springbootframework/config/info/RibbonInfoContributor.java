
package com.luixtech.springbootframework.config.info;

import com.luixtech.springbootframework.config.LuixProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * An {@link org.springframework.boot.actuate.info.InfoContributor} that exposes the list of active spring profiles.
 */
public class RibbonInfoContributor implements InfoContributor {

    private final boolean ribbonEnabled;
    private final String  ribbonProfile;

    /**
     * <p>Constructor for ActiveProfilesInfoContributor.</p>
     *
     * @param env a {@link org.springframework.core.env.ConfigurableEnvironment} object.
     */
    public RibbonInfoContributor(ConfigurableEnvironment env, LuixProperties luixProperties) {
        String[] profilesArray = env.getActiveProfiles().length == 0
                ? env.getDefaultProfiles()
                : env.getActiveProfiles();
        List<String> displayRibbonOnProfiles = luixProperties.getRibbon().getDisplayOnActiveProfiles();
        if (CollectionUtils.isEmpty(displayRibbonOnProfiles)) {
            ribbonEnabled = false;
            ribbonProfile = StringUtils.EMPTY;
            return;
        }
        Collection<String> intersection = CollectionUtils.intersection(Arrays.asList(profilesArray),
                displayRibbonOnProfiles);
        ribbonEnabled = !intersection.isEmpty();
        ribbonProfile = intersection.isEmpty() ? StringUtils.EMPTY : intersection.iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("ribbonEnabled", ribbonEnabled);
        builder.withDetail("ribbonProfile", ribbonProfile);
    }
}
