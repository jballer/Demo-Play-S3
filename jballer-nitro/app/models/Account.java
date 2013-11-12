package models;

import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Account extends Model
{	
	public String username;
	public String password;
	
	@OneToMany(mappedBy="uploadedBy", cascade=CascadeType.ALL)
	public List<Document> documents;
	
	public Account(String username, String password) {
		this.username = username;
		this.password = password;
		this.documents = new ArrayList<Document>();
	}
	
	public Document addDocument(InputStream is, String fileName, String comment) {
		Document doc = new Document(this, is, fileName, comment);
		documents.add(doc);
		this.save();
		
		return doc;
	}
	
	public String toString() {
		return username;
	}
	
	public static Account accountForUsername(String username)
	{
		return Account.find("byUsername",username).first();
	}
	
	public static Account accountForUserID(long userID)
	{
		return Account.findById(userID);
	}
	
	public static long idForUsername(String username)
	{
		return accountForUsername(username).id;
	}
	
	public Boolean setUsername(String newUsername)
	{
		// Check validity of username
		
		// TODO: Does it already exist?
		if(Account.accountForUsername(newUsername) != null)
		{
			// The account exists and we won't create a new one
			return false;
		}
		
		// TODO: Is it an email address?
		try
		{
			InternetAddress emailAddr = new InternetAddress(newUsername);
			emailAddr.validate();
		}
		catch (AddressException ex)
		{
		      return false;
		}
		
		// If we made it here, assume it's valid.
		// Set the ivar and return true
		username = newUsername;
		return true;
	}
	
	public Boolean setPassword(String newPassword)
	{
		// Don't allow blank password.
		if(newPassword.equals(""))
		{
			return false;
		}
		
		// Look for nonstandard chars
		Pattern p = Pattern.compile("[^a-zA-Z0-9]");
		if(p.matcher(newPassword).find())
		{
			return false;
		}
		
		// If we made it here, assume it's valid.
		// Set the ivar and return true
		
		password = newPassword;
		return true;
	}
}