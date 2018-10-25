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

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.S3Object;

import java.io.InputStream;

public class S3Utils {

    /**
     * Get an S3 client object with a user name and password
     */
    public static AmazonS3 getS3Client(String user, String pass) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(user, pass);
        return AmazonS3ClientBuilder.standard().withRegion("us-east-1")
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    /**
     * retrieve an input stream representing the contents of the url at the s3 location.
     * @param url
     * @return
     */
    public static InputStream getViaS3(AmazonS3 s3, String url) {
        AmazonS3URI uri = new AmazonS3URI(url);
        S3Object o = s3.getObject(uri.getBucket(), uri.getKey());

        if (o != null) {
            return o.getObjectContent();
        }
        return null;
    }
}
