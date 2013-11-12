package models;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.type.StringType;
import org.hibernate.usertype.UserType;

import play.Logger;
import play.Play;
import play.db.Model.BinaryField;
import play.exceptions.ConfigurationException;
import play.libs.Codec;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

public class S3Blob implements BinaryField, UserType {

        public static String s3Bucket;
        public static AmazonS3 s3Client;
        private String bucket;
        private String key;

        public S3Blob() {
        	if(s3Bucket == null || s3Client == null) {
    			Logger.info("Initializing S3");
    	        if (!Play.configuration.containsKey("aws.access.key")) {
    	                throw new ConfigurationException("Bad configuration for s3: no access key");
    	        } else if (!Play.configuration.containsKey("aws.secret.key")) {
    	                throw new ConfigurationException("Bad configuration for s3: no secret key");
    	        } else if (!Play.configuration.containsKey("s3.bucket")) {
    	                throw new ConfigurationException("Bad configuration for s3: no s3 bucket");
    	        }
    	        s3Bucket = Play.configuration.getProperty("s3.bucket");
    	        String accessKey = Play.configuration.getProperty("aws.access.key");
    	        String secretKey = Play.configuration.getProperty("aws.secret.key");
    	        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
    	        S3Blob.s3Client = new AmazonS3Client(awsCredentials);
    	        if (!S3Blob.s3Client.doesBucketExist(s3Bucket)) {
    	                S3Blob.s3Client.createBucket(s3Bucket);
    	        }
        	}
        }

        private S3Blob(String bucket, String s3Key) {
                this.bucket = bucket;
                this.key = s3Key;
        }

        @Override
        public InputStream get() {
                S3Object s3Object = s3Client.getObject(bucket, key);
                return s3Object.getObjectContent();
        }

        @Override
        public void set(InputStream is, String type) {
                this.bucket = s3Bucket;
                this.key = Codec.UUID();
                ObjectMetadata om = new ObjectMetadata();
                om.setContentType(type);
                s3Client.putObject(bucket, key, is, om);
        }

        /**
         * 
         * @return the S3 key for the deleted object
         */
        public String delete () {
                s3Client.deleteObject(s3Bucket, key);
                return key;
        }
        
        @Override
        public long length() {
                ObjectMetadata om = s3Client.getObjectMetadata(bucket, key);
                return om.getContentLength();
        }

        @Override
        public String type() {
                ObjectMetadata om = s3Client.getObjectMetadata(bucket, key);
                return om.getContentType();
        }

        @Override
        public boolean exists() {
                ObjectMetadata om = s3Client.getObjectMetadata(bucket, key);
                return om != null;
        }

        @Override
        public int[] sqlTypes() {
                return new int[] { Types.VARCHAR };
        }

        @Override
        public Class returnedClass() {
                return S3Blob.class;
        }

        @Override
        public boolean equals(Object o, Object o1) throws HibernateException {
                return o == null ? false : o.equals(o1);
        }

        @Override
        public int hashCode(Object o) throws HibernateException {
                return o.hashCode();
        }

        @Override
        public Object nullSafeGet(ResultSet rs, String[] names, Object o) throws HibernateException, SQLException {
                @SuppressWarnings("deprecation")
				String val = StringType.INSTANCE.nullSafeGet(rs, names[0]);
                if (val == null || val.length() == 0 || !val.contains("|")) {
                        return new S3Blob();
                }
                return new S3Blob(val.split("[|]")[0], val.split("[|]")[1]);
        }

        @Override
        public void nullSafeSet(PreparedStatement ps, Object o, int i) throws HibernateException, SQLException {
                if (o != null) {
                        ps.setString(i, ((S3Blob) o).bucket + "|" + ((S3Blob) o).key);
                } else {
                        ps.setNull(i, Types.VARCHAR);
                }
        }

        @Override
        public Object deepCopy(Object o) throws HibernateException {
                if (o == null) {
                        return null;
                }
                return new S3Blob(this.bucket, this.key);
        }

        @Override
        public boolean isMutable() {
                return true;
        }

        @Override
        public Serializable disassemble(Object o) throws HibernateException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object assemble(Serializable srlzbl, Object o) throws HibernateException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object replace(Object o, Object o1, Object o2) throws HibernateException {
                throw new UnsupportedOperationException("Not supported yet.");
        }
}
