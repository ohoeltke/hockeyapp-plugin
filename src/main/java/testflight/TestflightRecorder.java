package testflight;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.search.Search;
import hudson.tasks.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.HttpClient;
import org.json.simple.parser.JSONParser;
import org.kohsuke.stapler.DataBoundConstructor;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TestflightRecorder extends Recorder
{
    private String apiToken;
    private String teamToken;
    private Boolean notifyTeam;
    private String file;
    private String buildNotes;
    private String filePath;

    @DataBoundConstructor
    public TestflightRecorder(String apiToken, String teamToken, Boolean notifyTeam, String file, String buildNotes, String filePath)
    {
        this.teamToken = teamToken;
        this.apiToken = apiToken;
        this.notifyTeam = notifyTeam;
        this.file = file;
        this.buildNotes = buildNotes;
        this.filePath = filePath;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    public BuildStepMonitor getRequiredMonitorService( )
    {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener)
    {
        if (build.getResult().isWorseOrEqualTo(Result.FAILURE))
            return false;

        listener.getLogger().println("Uploading to testflight");

        try
        {
            EnvVars vars = build.getEnvironment(listener);

            File file = new File(vars.expand(filePath));
            listener.getLogger().println(file);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://testflightapp.com/api/builds.json");
            FileBody fileBody = new FileBody(file);

            MultipartEntity entity = new MultipartEntity();
            entity.addPart("api_token", new StringBody(apiToken));
            entity.addPart("team_token", new StringBody(teamToken));
            entity.addPart("notes", new StringBody(vars.expand(buildNotes)));
            entity.addPart("file", fileBody);

            httpPost.setEntity(entity);

            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();

            InputStream is = resEntity.getContent();
            JSONParser parser = new JSONParser();

            final Map parsedMap = (Map)parser.parse(new BufferedReader(new InputStreamReader(is)));

            build.addAction( new ProminentProjectAction() {
                public String getIconFileName() {
                    return "new-package.gif";
                }

                public String getDisplayName() {
                    return "Testflight Install Link";
                }

                public String getUrlName() {
                    return (String)parsedMap.get("install_url");
                }
            } );

            build.addAction( new ProminentProjectAction() {
                public String getIconFileName() {
                    return "gear.gif";
                }

                public String getDisplayName() {
                    return "Testflight Configuration Link";
                }

                public String getUrlName() {
                    return (String)parsedMap.get("config_url");
                }
            } );
        }
        catch (Exception e)
        {
            listener.getLogger().println(e);
            return false;
        }

        return true;
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher>
    {
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Upload to Testflight";
        }
    }
}
