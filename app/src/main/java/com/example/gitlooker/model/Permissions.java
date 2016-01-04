
package com.example.gitlooker.model;

import java.util.HashMap;
import java.util.Map;

public class Permissions {

    public boolean admin;
    public boolean push;
    public boolean pull;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
