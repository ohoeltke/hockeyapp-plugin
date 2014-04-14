package net.hockeyapp.jenkins.uploadMethod;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import net.hockeyapp.jenkins.RadioButtonSupport;
import net.hockeyapp.jenkins.RadioButtonSupportDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.export.Exported;

import javax.servlet.ServletException;
import java.io.IOException;
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
        this.appId = appId;
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

        public DescriptorImpl() {
            super();
            logger.info("Called 'DescriptorImpl'");
            load();
        }

        @Override
        public String getDisplayName() {
            return "Upload Version";
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckAppId(@QueryParameter String value) throws IOException, ServletException {
//            if(value.isEmpty()) {
//                return FormValidation.error("You must enter an App ID!");
//            } else if(value.length() != 32) {
//                return FormValidation.error("App ID length must be 32!");
//            } else {
//                if (value.matches("[0-9A-Fa-f]{32}")) {
//                    return FormValidation.ok();
//                } else {
//                    return FormValidation.warning("Check correctness of App ID!");
//                }
//            }
            if(value.isEmpty()) {
                return FormValidation.error("You must enter an App ID!");
            } else {
                return FormValidation.ok();
            }

        }
    }

}