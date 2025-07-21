package com.Acrobot.Breeze.Configuration.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a configuration value
 *
 * @author Acrobot
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigurationComment {
    /**
     * This option's comment
     *
     * @return Comment
     */
    public String value();
}
