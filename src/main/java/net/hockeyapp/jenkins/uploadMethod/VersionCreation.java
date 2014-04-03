package net.hockeyapp.jenkins.uploadMethod;

import hudson.Extension;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;
import net.hockeyapp.jenkins.RadioButtonSupport;
import net.hockeyapp.jenkins.RadioButtonSupportDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;

import java.util.logging.Logger;

/**
 * Created by ungerts on 03.04.14.
 */
public class VersionCreation extends RadioButtonSupport {

    private static final Logger logger = Logger.getLogger(VersionCreation.class.getName());

    @Exported
    private String appId;

    @DataBoundConstructor
    public VersionCreation(String appId) {

    }

    public String getAppId() {
        logger.info("Called 'getAppId'");
        return appId;
    }

    public Descriptor<RadioButtonSupport> getDescriptor() {
        return Jenkins.getInstance() == null ? null : Jenkins.getInstance().getDescriptorOrDie(this.getClass());
    }

    @Extension
    public static class DescriptorImpl extends RadioButtonSupportDescriptor<VersionCreation> {

        @Override
        public String getDisplayName() {
            return "Upload Version";
        }
    }

}