/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package com.camsys.shims.s3;

import com.csvreader.CsvReader;

import org.onebusaway.cloud.api.ExternalResult;
import org.onebusaway.cloud.api.ExternalServices;
import org.onebusaway.cloud.api.ExternalServicesBridgeFactory;
import org.onebusaway.cloud.api.InputStreamConsumer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public abstract class AbstractS3CsvProvider {
    private String url;

    private ExternalServices _externalServices;

    // null is default
    private String _profile;

    private String file;
    private String targetUrl;
    private String localPath;


    public void init() throws Exception {
        _externalServices = new ExternalServicesBridgeFactory().getExternalServices();

        // for testing/debugging
        if (url.startsWith("file://")) {
        
        	InputStream stream = new FileInputStream(url.substring("file://".length()));
            CsvReader reader = new CsvReader(new InputStreamReader(stream));
            reader.readHeaders();
            while (reader.readRecord()) {
                processRecord(reader);
            }
            reader.close();

        } else if (!url.startsWith("s3://")) {
            throw new UnsupportedOperationException("protocol in url " + url + " no supported!");
        }

        update();
    }

    public void update() throws Exception {
        ExternalResult r = _externalServices.getFileAsStream(url, new InputStreamConsumer() {
            @Override
            public void accept(InputStream stream) throws IOException {
                CsvReader reader = new CsvReader(new InputStreamReader(stream));
                reader.readHeaders();
                while (reader.readRecord()) {
                    processRecord(reader);
                }
                reader.close();
            }
        }, _profile);
        
        if(!r.getSuccess()) {
        	throw new Exception(r.getErrorMessage());
        }
    }

    public abstract void processRecord(CsvReader reader) throws IOException;

    public void setUrl(String url) {
        this.url = url;
    }
    public void setProfile(String profile) {
        _profile = profile;
    }
    public void setFile(String file) {
        this.file = file;
    }
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }


    public String getUrl() { return url; }
    public String getProfile() { return _profile; }
    public String getFile() { return file; }
    public String getTargetUrl() { return targetUrl; }
    public String getLocalPath() { return localPath; }
}