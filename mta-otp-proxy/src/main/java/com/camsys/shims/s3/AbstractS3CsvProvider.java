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

import com.amazonaws.services.s3.AmazonS3;
import com.csvreader.CsvReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class AbstractS3CsvProvider {
    private String user;

    private String pass;

    private String url;

    public void init() {
        if (!url.startsWith("s3://")) {
            throw new UnsupportedOperationException("protocol in url " + url + " no supported!");
        }
        AmazonS3 s3 = S3Utils.getS3Client(user, pass);
        InputStream stream = S3Utils.getViaS3(s3, url);
        CsvReader reader = new CsvReader(new InputStreamReader(stream));
        try {
            reader.readHeaders();
            while (reader.readRecord()) {
                processRecord(reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader.close();
    }

    public abstract void processRecord(CsvReader reader) throws IOException;

    public void setUser(String user) {
        this.user = user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
