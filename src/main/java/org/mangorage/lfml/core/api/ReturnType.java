package org.mangorage.lfml.core.api;

public enum ReturnType {
    SUPER_BEFORE, // call super before we execute prototype func
    SUPER_AFTER,  // call super after we execute prototype func
    SUPER_NORMAL, // dont do anything special
    NO_SUPER, // don't do any super
    SUPER_OVERRIDE // return an object
}
