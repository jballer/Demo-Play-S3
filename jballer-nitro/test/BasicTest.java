import org.junit.*;

import controllers.Files;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;

import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

	@Test
	public void setup() {
		Fixtures.deleteDatabase();
	}
	
	@Test
    public void createUsers() {
    	int userCount = Account.findAll().size();
    	// Create an account
    	Account account = new Account("test@test.test", "test");
    	account.save();
    	assertEquals(userCount+1, Account.findAll().size());
    	
    	account = new Account("test2@test2.test2", "test2");
    	account.save();
    	assertEquals(userCount+2, Account.findAll().size());
    }
	
	@Test
	public void createDocuments() {
		
		int docCount = Document.findAll().size();
		
		// Create a document associated with that user
    	Account account = (Account) Account.findAll().get(0);
    	InputStream is = new ByteArrayInputStream("testing 1".getBytes());
        account.addDocument(is, "autogenerated-1.txt", "no comment");
        assertEquals(docCount+1, Document.findAll().size());
        
        // Create a second document for that user
        is = new ByteArrayInputStream("testing 2".getBytes());
        account.addDocument(is, "autogenerated-2.txt", "no comment");
        assertEquals(docCount+2, Document.findAll().size());
	}
	
    @Test
    public void deleteDocuments() {
    	createDocuments();
        
    	List<Document> docs = Document.findAll();
    	int docCount = docs.size();
    	
    	// Test document deletion
    	docs.get(0).delete();
        assertEquals(docCount-1, Document.findAll().size());
        
        docs.get(1).delete();
        assertEquals(docCount-2, Document.findAll().size());
    }
    
    @Test
    public void deleteAccountAndRelatedPosts() {
        createDocuments();
        
        List<Account> accounts = Account.findAll();
        int userCount = accounts.size();
        int docCount = Document.findAll().size();
        
        Account account = accounts.get(0);
        int userDocCount = account.documents.size();
        assertTrue(userDocCount > 1); //can't test cascading unless user has enough docs
        
    	// Test cascading deletion of a user's documents
        account.delete();
        assertEquals(userCount-1, Account.findAll().size());
        assertEquals(docCount-userDocCount, Document.findAll().size());
    }
    
}
