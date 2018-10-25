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

/**
 * <p>Abstract AbstractS3CsvProvider class.</p>
 *
 */
public abstract class AbstractS3CsvProvider {
    private String user;

    private String pass;

    private String url;

    /**
     * <p>init.</p>
     */
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

    /**
     * <p>processRecord.</p>
     *
     * @param reader a {@link com.csvreader.CsvReader} object.
     * @throws java.io.IOException if any.
     */
    public abstract void processRecord(CsvReader reader) throws IOException;

    /**
     * <p>Setter for the field <code>user</code>.</p>
     *
     * @param user a {@link java.lang.String} object.
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * <p>Setter for the field <code>pass</code>.</p>
     *
     * @param pass a {@link java.lang.String} object.
     */
    public void setPass(String pass) {
        this.pass = pass;
    }

    /**
     * <p>Setter for the field <code>url</code>.</p>
     *
     * @param url a {@link java.lang.String} object.
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
