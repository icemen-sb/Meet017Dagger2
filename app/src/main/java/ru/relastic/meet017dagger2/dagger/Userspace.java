package ru.relastic.meet017dagger2.dagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Scope;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Scope
@Retention(RUNTIME)
@Target({METHOD, CONSTRUCTOR, TYPE, PARAMETER, FIELD})
public @interface Userspace {}
