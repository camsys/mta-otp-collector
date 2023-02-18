package com.camsys.shims.factory;

import org.onebusaway.cloud.api.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Factory for easy creating of multiple Credentials in xml files.
 */
public class CredentialsFactory implements FactoryBean<List<Credential>> {
  private static Logger _log = LoggerFactory.getLogger(CredentialFactory.class);
  public String getType() {
    return _type;
  }

  public void setType(String type) {
    this._type = type;
  }

  public String getKey() {
    return _key;
  }

  public void setKey(String key) {
    this._key = key;
  }

  public String getKeyName() {
    return _keyName;
  }

  public void setKeyName(String keyName) {
    this._keyName = keyName;
  }

  public String getValue() {
    return _value;
  }

  public void setValue(String value) {
    this._value = value;
  }

  public String getValueName() {
    return _valueName;
  }

  public void setValueName(String valueName) {
    this._valueName = valueName;
  }

  private String _type;
  private String _key;
  private String _keyName;
  private String _value;
  private String _valueName;

  @Override
  public List<Credential> getObject() throws Exception {
    CredentialFactory cf = new CredentialFactory();
    cf.setKey(_key);
    cf.setType(_type);
    cf.setKeyName(_keyName);
    cf.setValue(_value);
    cf.setValueName(_valueName);
    return Arrays.asList(cf.getObject());
  }

  @Override
  public Class<?> getObjectType() {
    return List.class;
  }
}
