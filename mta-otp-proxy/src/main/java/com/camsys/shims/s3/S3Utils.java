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
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import java.io.File;
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

    /**
     * This is a safer method of copying a file from S3 locally.
     * According to the AWS docs, S3.GetObject(bucket, key) returns a direct stream from S3 and may
     * result in the client running out of resources if used improperly.
     * See https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/AmazonS3.html#getObject-com.amazonaws.services.s3.model.GetObjectRequest-java.io.File-
     *
     * @returns true if successful
     */
    public static boolean copyFromS3ToFile(AmazonS3 s3, String url, String dest) {
        AmazonS3URI uri = new AmazonS3URI(url);
        GetObjectRequest request = new GetObjectRequest(uri.getBucket(), uri.getKey());
        File file = new File(dest);
        ObjectMetadata metadata = s3.getObject(request, file);
        return metadata != null;
    }

}
