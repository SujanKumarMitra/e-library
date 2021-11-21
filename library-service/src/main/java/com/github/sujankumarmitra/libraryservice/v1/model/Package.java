package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Collection;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class Package {

    public abstract String getId();

    public abstract String getName();

    public abstract Collection<PackageItem> getItems();

}
