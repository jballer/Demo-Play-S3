import org.junit.*;
import org.junit.rules.ExpectedException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import java.io.*;
import java.util.*;

import play.libs.MimeTypes;
import play.test.*;
import models.S3Blob;

public class S3ConnectionTest extends UnitTest {

	@Test
    public void testS3UploadDownloadDelete() {
    	// Create a blob
    	S3Blob blob = new S3Blob();
    	
    	// Put some data in it
    	InputStream is = new ByteArrayInputStream("testing!".getBytes());
    	blob.set(is, "text/plain");
    	
        // Get the data out of it
    	String s = getStringFromInputStream(blob.get());
    	assertEquals(s, "testing!");
    	
    	// Delete the blob
    	String key = blob.delete();
    	
    	try {
    		S3Blob.s3Client.getObject(S3Blob.s3Bucket, key);
    		fail( "S3 file did not delete" );
    	} catch(AmazonS3Exception e) {
    		assertTrue(e.getMessage().contains("The specified key does not exist"));
    	}
    }
    
    private static String getStringFromInputStream(InputStream is) {
    	 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}

}
