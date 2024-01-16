package com.generalbytes.batm.server.extensions.examples.location;

import com.generalbytes.batm.server.extensions.IOrganization;

public class OrganizationExample implements IOrganization {

    private String id;
    private String name;

    public OrganizationExample() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
