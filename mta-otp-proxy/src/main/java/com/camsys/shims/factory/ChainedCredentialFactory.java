package com.camsys.shims.factory;

import org.onebusaway.cloud.api.Credential;
import org.springframework.beans.factory.FactoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * support multiple credentials for an endpoint.
 */
public class ChainedCredentialFactory implements FactoryBean<List<Credential>> {

  private List<Credential> credentials = new ArrayList<>();

  public void setCredentials(ArrayList<Credential> credentials) {
    this.credentials.addAll(credentials);
  }

  @Override
  public List<Credential> getObject() throws Exception {
    return credentials;
  }

  @Override
  public Class<?> getObjectType() {
    return List.class;
  }
}
