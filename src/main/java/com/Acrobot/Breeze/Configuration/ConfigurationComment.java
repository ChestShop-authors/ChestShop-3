package com.Acrobot.Breeze.Configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for a configuration value
 *
 * @author Acrobot
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationComment {
    /**
     * This option's comment
     *
     * @return Comment
     */
    public String value();
}
