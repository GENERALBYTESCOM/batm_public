package com.generalbytes.batm.server.extensions.examples.location;

import com.generalbytes.batm.server.extensions.IRestService;

public class LocationExampleIRestService implements IRestService {

    @Override
    public String getPrefixPath() {
        return "location-example";
    }

    @Override
    public Class getImplementation() {
        return LocationExampleRestService.class;
    }

}
