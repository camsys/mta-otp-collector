/**
 * Copyright (C) 2019 Cambridge Systematics, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.camsys.shims.factory;

import org.onebusaway.cloud.api.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * Spring Factory for easy creating of Credentials in xml files.
 */
public class CredentialFactory implements FactoryBean<Credential> {

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
    public Credential getObject() throws Exception {
        Credential c = new Credential();
        if (Credential.CredentialType.API_KEY_PARAM.toString().toLowerCase().equals(_type)) {
            _log.info("creating api key param for " + _key);
            return new Credential().createApiKeyParam(_key);
        } else if (Credential.CredentialType.API_KEY_HEADER.toString().toLowerCase().equals(_type)) {
            _log.info("creating api key header for " + _key + "/" + _keyName);
            return new Credential().createApiKeyHeader(_key, _keyName);
        } else if (Credential.CredentialType.EXTERNAL_PROFILE.toString().toLowerCase().equals(_type)) {
            _log.info("creating external profile for " + _key + "/" + _value);
            return new Credential().createExternalProfileKey(_key, _value);
        } else if (Credential.CredentialType.NO_OP.toString().toLowerCase().equals(_type)) {
            _log.info("creating no op credential");
            return new Credential().createNoOp();
        } else {
            final String msg = "Unsupported Credential Type " + _type
                    + ", expecting one of "
                    + Credential.CredentialType.API_KEY_PARAM.toString().toLowerCase() + ", "
                    + Credential.CredentialType.API_KEY_HEADER.toString().toLowerCase() + ", "
                    + Credential.CredentialType.EXTERNAL_PROFILE.toString().toLowerCase() + ".";
            throw new IllegalStateException(msg);
        }
    }

    @Override
    public Class<?> getObjectType() { return Credential.class; }

}
