package com.openai36.aggregation.security;

import jakarta.annotation.security.RolesAllowed;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RolesAllowed(Roles.ADMIN)
public @interface Admin {
}
