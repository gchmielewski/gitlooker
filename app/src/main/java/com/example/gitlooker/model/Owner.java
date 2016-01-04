package com.example.gitlooker.model;

import java.util.HashMap;
import java.util.Map;

public class Owner {

  public String login;
  public int id;
  public String avatar_url;
  public String url;
  public String type;
  public boolean site_admin;
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
