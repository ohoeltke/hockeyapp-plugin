package net.hockeyapp.jenkins;

import hudson.ExtensionPoint;
import hudson.model.Describable;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Created by ungerts on 03.04.14.
 */
@ExportedBean
public abstract class RadioButtonSupport implements ExtensionPoint, Describable<RadioButtonSupport> {
}
