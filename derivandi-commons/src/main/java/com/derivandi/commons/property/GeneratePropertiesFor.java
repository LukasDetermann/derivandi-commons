package com.derivandi.commons.property;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface GeneratePropertiesFor
{
   Class<?>[] value();
}