package net.hockeyapp.jenkins;

import hudson.model.Descriptor;
import hudson.model.Saveable;

/**
 * Created by ungerts on 03.04.14.
 */
public abstract class RadioButtonSupportDescriptor<T extends RadioButtonSupport> extends Descriptor<RadioButtonSupport> implements Saveable {
}
