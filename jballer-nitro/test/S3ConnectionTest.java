import org.junit.*;

import com.amazonaws.services.s3.AmazonS3;

import java.io.*;
import java.util.*;

import play.libs.MimeTypes;
import play.test.*;
import models.*;
import play.modules.s3blobs.S3Blob;

public class S3ConnectionTest extends UnitTest {

    @Test
    public void testS3Upload() {
    	// Create a blob
    	S3Blob blob = new S3Blob();
    	
    	// Put some data in it
    	InputStream is = new ByteArrayInputStream("testing!".getBytes());
    	blob.set(is, "text/plain");
    	
        // Get the data out of it
    	String s = getStringFromInputStream(blob.get());
    	assertEquals(s, "testing!");
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
