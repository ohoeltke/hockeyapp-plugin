package hockeyapp;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import hudson.Extension;
import hudson.Util;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import net.hockeyapp.jenkins.RadioButtonSupport;
import net.hockeyapp.jenkins.RadioButtonSupportDescriptor;
import net.hockeyapp.jenkins.releaseNotes.ChangelogReleaseNotes;
import net.hockeyapp.jenkins.releaseNotes.FileReleaseNotes;
import net.hockeyapp.jenkins.releaseNotes.ManualReleaseNotes;
import net.hockeyapp.jenkins.releaseNotes.NoReleaseNotes;
import net.hockeyapp.jenkins.uploadMethod.AppCreation;
import net.hockeyapp.jenkins.uploadMethod.VersionCreation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.export.ExportedBean;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ExportedBean
public class HockeyappApplication implements Describable<HockeyappApplication> {
    public static final long SCHEMA_VERSION_NUMBER = 1L;

    @XStreamAsAttribute
    public long schemaVersion;

    public String apiToken;
    public String appId;
    public boolean notifyTeam;
    public String filePath;
    public String dsymPath;
    public String libsPath;
    public String tags;
    public String teams;
    public boolean mandatory;
    public boolean downloadAllowed;
    public OldVersionHolder oldVersionHolder;
    public RadioButtonSupport releaseNotesMethod;
    public RadioButtonSupport uploadMethod;

    @DataBoundConstructor
    public HockeyappApplication(String apiToken, String appId, boolean notifyTeam,
                                String filePath, String dsymPath, String libsPath,
                                String tags, String teams, boolean mandatory, 
                                boolean downloadAllowed, OldVersionHolder oldVersionHolder,
                                RadioButtonSupport releaseNotesMethod, RadioButtonSupport uploadMethod) {
        this.schemaVersion = SCHEMA_VERSION_NUMBER;
        this.apiToken = Util.fixEmptyAndTrim(apiToken);
        this.appId = Util.fixEmptyAndTrim(appId);
        this.notifyTeam = notifyTeam;
        this.filePath = Util.fixEmptyAndTrim(filePath);
        this.dsymPath = Util.fixEmptyAndTrim(dsymPath);
        this.libsPath = Util.fixEmptyAndTrim(libsPath);
        this.tags = Util.fixEmptyAndTrim(tags);
        this.downloadAllowed = downloadAllowed;
        this.oldVersionHolder = oldVersionHolder;
        this.releaseNotesMethod = releaseNotesMethod;
        this.uploadMethod = uploadMethod;
        this.teams = Util.fixEmptyAndTrim(teams);
        this.mandatory = mandatory;
    }

    @SuppressWarnings("unused")
    public RadioButtonSupport getReleaseNotesMethod() {
        return releaseNotesMethod;
    }

    @SuppressWarnings("unused")
    public RadioButtonSupport getUploadMethod() {
        return uploadMethod;
    }

    public String getNumberOldVersions() {
        return oldVersionHolder == null ? null : oldVersionHolder.numberOldVersions;
    }

    public String getSortOldVersions() {
        return oldVersionHolder == null ? null : oldVersionHolder.sortOldVersions;
    }

    public String getStrategyOldVersions() {
        return oldVersionHolder == null ? null : oldVersionHolder.strategyOldVersions;
    }

    @Override
    public Descriptor<HockeyappApplication> getDescriptor() {
        return new DescriptorImpl();
    }

    public static class OldVersionHolder {
        private String numberOldVersions;
        // Defaults per https://support.hockeyapp.net/kb/api/api-versions#delete-multiple-versions
        private String sortOldVersions = "version";
        private String strategyOldVersions = "purge";

        @DataBoundConstructor
        public OldVersionHolder(String numberOldVersions, String sortOldVersions, String strategyOldVersions) {
            this.numberOldVersions = Util.fixEmptyAndTrim(numberOldVersions);
            this.sortOldVersions = Util.fixEmptyAndTrim(sortOldVersions);
            this.strategyOldVersions = Util.fixEmptyAndTrim(strategyOldVersions);
        }
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<HockeyappApplication> {
        @Override
        public String getDisplayName() {
            return Messages.APPLICATION();
        }

        @SuppressWarnings("unused")
        public List<RadioButtonSupportDescriptor> getReleaseNotesMethodList() {
            List<RadioButtonSupportDescriptor> releaseNotesMethods = new ArrayList<RadioButtonSupportDescriptor>(4);
            Jenkins jenkins = Jenkins.getInstance();
            if (jenkins != null) {
                releaseNotesMethods.add(
                        (RadioButtonSupportDescriptor) jenkins.getDescriptorOrDie(NoReleaseNotes.class));
                releaseNotesMethods.add(
                        (RadioButtonSupportDescriptor) jenkins.getDescriptorOrDie(ChangelogReleaseNotes.class));
                releaseNotesMethods.add(
                        (RadioButtonSupportDescriptor) jenkins.getDescriptorOrDie(FileReleaseNotes.class));
                releaseNotesMethods.add(
                        (RadioButtonSupportDescriptor) jenkins.getDescriptorOrDie(ManualReleaseNotes.class));
            }
            return releaseNotesMethods;
        }

        @SuppressWarnings("unused")
        public List<RadioButtonSupportDescriptor> getUploadMethodList() {
            List<RadioButtonSupportDescriptor> uploadMethods = new ArrayList<RadioButtonSupportDescriptor>(2);
            Jenkins jenkins = Jenkins.getInstance();
            if (jenkins != null) {
                uploadMethods.add((RadioButtonSupportDescriptor) jenkins.getDescriptorOrDie(AppCreation.class));
                uploadMethods.add((RadioButtonSupportDescriptor) jenkins.getDescriptorOrDie(VersionCreation.class));
            }
            return uploadMethods;
        }


        @SuppressWarnings("unused")
        public FormValidation doCheckApiToken(@QueryParameter String value) throws IOException, ServletException {
            if(value.isEmpty()) {
                HockeyappRecorder.DescriptorImpl hockeyappRecorderDescriptor =
                        (HockeyappRecorder.DescriptorImpl) Jenkins.getInstance().getDescriptorOrDie(
                                HockeyappRecorder.class);
                String defaultToken = hockeyappRecorderDescriptor.getDefaultToken();
                if (defaultToken != null && defaultToken.length() > 0) {
                    return FormValidation.warning("Default API Token is used.");
                } else {
                    return FormValidation.errorWithMarkup(
                            "You must enter an <a href=\"https://rink.hockeyapp.net/manage/auth_tokens\">API Token</a>.");
                }
            } else {
                return FormValidation.ok();
            }
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckNumberOldVersions(@QueryParameter String value) throws IOException, ServletException {
            if(value.isEmpty()) {
                return FormValidation.error("You must specify a positive Number.");
            } else {
                try {
                    int number = Integer.parseInt(value);
                    if (number > 0) {
                        return FormValidation.ok();
                    } else {
                        return FormValidation.error("You must specify a positive Number.");
                    }
                } catch (NumberFormatException e) {
                    return FormValidation.error("You must specify a positive Number.");
                }
            }
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckFilePath(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.error("You must enter a File Path.");
            } else {
                return FormValidation.ok();
            }
        }
    }
}
