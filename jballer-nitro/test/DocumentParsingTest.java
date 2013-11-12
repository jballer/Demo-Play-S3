import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import models.Tag;

import org.junit.Test;

import controllers.Files;
import play.test.UnitTest;


public class DocumentParsingTest extends UnitTest {
	
	@Test
    public void parseAllWords() {
    	
    	// Create a document associated with that user
    	String tagTestString = "five one three five five two five two five "
    			+ "six six three six six three six six";
    	
    	InputStream is = new ByteArrayInputStream(tagTestString.getBytes());
        
    	Map tags = Files.parseTagsFromStream(is);
    	
    	// Produce output for debugging
    	Iterator it = tags.keySet().iterator(); 
    	while(it.hasNext()) {
    		String key = (String) it.next(); 
    		System.out.println("Tag: " + key + " | Count: " + tags.get(key));
    	}
    	
    	// Check the result with assertions
    	assertEquals(1, tags.get("one"));
    	assertEquals(2, tags.get("two"));
    	assertEquals(3, tags.get("three"));
    	assertNull(tags.get("four"));
    	assertEquals(5, tags.get("five"));
    	assertEquals(6, tags.get("six"));
    }
	
	@Test
	public void parseLongWords() {
		// Create a document associated with that user
    	String tagTestString = "five one three five five two five two five "
    			+ "six six three six six three six six";
    	
    	InputStream is = new ByteArrayInputStream(tagTestString.getBytes());
        
    	Map tags = Files.parseTagsFromStream(is, 4, 0);
    	assertNull(tags.get("one"));
    	assertNull(tags.get("two"));
    	assertEquals(3, tags.get("three")); // the only word longer than 4 chars
    	assertNull(tags.get("five"));
    	assertNull(tags.get("six"));
	}
	
	@Test
	public void parseFrequentWords() {
		// Create a document associated with that user
    	String tagTestString = "five one three five five two five two five "
    			+ "six six three six six three six six";
    	
    	InputStream is = new ByteArrayInputStream(tagTestString.getBytes());
        
    	Map tags = Files.parseTagsFromStream(is, 0, 4);
    	assertNull(tags.get("one"));
    	assertNull(tags.get("two"));
    	assertNull(tags.get("three")); // doesn't occur at least 4 times
    	assertEquals(5, tags.get("five"));
    	assertEquals(6, tags.get("six"));
	}
	
	@Test
	public void parseSongForLongFrequentWords() {
		// Create a document associated with that user
    	File file = new File("test/ludacris.txt");
    	
    	InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	Map tags = Files.parseTagsFromStream(is, 4, 2);
    	// Produce output for debugging
    	Iterator it = tags.keySet().iterator(); 
    	while(it.hasNext()) {
    		String key = (String) it.next(); 
    		System.out.println("Tag: " + key + " | Count: " + tags.get(key));
    	}
    	
    	assertEquals(11,tags.get("fantasy"));
    	assertEquals(10, tags.get("floor"));
    	assertEquals(2, tags.get("chocolate"));
	}

	@Test
    public void createTags() {
		Tag.deleteAll();
		
    	Tag t = Tag.findOrCreateByName("afajlfda");
    	t.incrementCount(2);
    	assertEquals(2, t.count);
    	
    	t = Tag.findOrCreateByName("afajlfda");
    	t.incrementCount(2);
    	assertEquals(4, t.count);
    	
    }
}
