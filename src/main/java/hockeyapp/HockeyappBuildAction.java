package hockeyapp;

import com.sun.org.apache.bcel.internal.generic.RET;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import hudson.model.Action;
import hudson.model.ProminentProjectAction;

import java.awt.*;

public class HockeyappBuildAction implements ProminentProjectAction
{
    public String iconFileName;
    public String displayName;
    public String urlName;

    public HockeyappBuildAction()
    {
    }

    public HockeyappBuildAction(Action action)
    {
        iconFileName = action.getIconFileName();
        displayName = action.getDisplayName();
        urlName = action.getUrlName();
    }

    public String getIconFileName() {
        return iconFileName;
    }

   public String getDisplayName() {
       return displayName;
   }

   public String getUrlName() {
       return urlName;
   }
}
