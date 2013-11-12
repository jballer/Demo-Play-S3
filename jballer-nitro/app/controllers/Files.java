package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.*;
import java.io.FileNotFoundException;
import java.util.List;

import javax.persistence.*;

import controllers.Secure.Security;
import models.Document;
import models.S3Blob;
import models.Account;
import models.Tag;
import models.UserTag;
import play.db.jpa.JPA;
import play.libs.MimeTypes;
import play.mvc.Controller;
import play.mvc.Util;

public class Files extends Controller
{
	public static void uploadForm()
	{
		render();
	}

	public static void doUpload(File file, String comment, int userID) throws FileNotFoundException
	{
		if(file == null){
			uploadForm();
		}
		else
		{
			Account uploadedBy = Account.findById(userID);
			InputStream is = new FileInputStream(file);
			String fileName = file.getName();
			final Document doc = new Document(uploadedBy, is, fileName, comment);
			doc.save();
	    
		    //TODO: add rendering for parseTags(file, doc);
		  }
	    
		  //TODO: add rendering for listUserUploads();
	  }

	public static void listAllUploads()
	{
		List<Document> docs = Document.findAll();
		render(docs);
	}
  
	public static void listUserUploads()
	{
		Account account = Account.accountForUsername(Security.connected());
		List<Document> docs = account.documents;
    
    //	TODO: implement template for user upload list
		renderTemplate("/Files/listUploads.html", docs, account.username);
	}

	public static void downloadFile(long id)
	{
		final Document doc = Document.findById(id);
		notFoundIfNull(doc);
		response.setContentTypeIfNotSet(doc.file.type());
		renderBinary(doc.file.get(), doc.fileName);
	}
  
	public static Document deleteFile(long id)
	{
		Document doc = Document.findById(id);
		return doc.delete();
	}

	public static Map parseTagsFromStream(InputStream fileStream) {
		return parseTagsFromStream (fileStream, 0, 0);
	}
	
	public static Map parseTagsFromFile(File file, int minWordLength, int minCount) {
		FileInputStream is = null;
		Map map = null;
		try {
			is = new FileInputStream(file);
			map = parseTagsFromStream(is, minWordLength, minCount);
			is.close();
		} catch (Exception e) {
			System.out.println("I/O Error: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		return map;
	}
	
	@Util
	public static Map parseTagsFromStream(InputStream fileStream, int minWordLength, int minCount)
	{
		//String for entire book
		String fullText = "";
		HashMap<String, Integer> tags = new HashMap<String, Integer>();
		
		//Read File to string
		BufferedInputStream bufferStream = null;
		
		BufferedReader reader = null;
		
		try
		{
			bufferStream = new BufferedInputStream(fileStream);
			reader = new BufferedReader(new InputStreamReader(bufferStream));
	  
			while (reader.ready()) 
			{
			  //populate string from file
				fullText+=reader.readLine() + " ";
			}
			reader.close();
			bufferStream.close();

		}
		catch (Exception x)
		{
			System.out.println("I/O Error: " + x.getLocalizedMessage());
		}

		//remove punctuation and numbers
		fullText = fullText.replaceAll("\\p{P}", " ");
		fullText = fullText.replaceAll("\\.|\\]|\\[|[0-9]|,|\\?|:|\\(|\\)|;|-|!"," ");
		
		//lower case all words
		fullText = fullText.toLowerCase();
		
		System.out.println("Full Text: "+fullText);
  
		//create pattern
		Pattern word = Pattern.compile("[\\w]+");
  
		//find pattern matches within file string
		Matcher m = word.matcher(fullText);
	  
		//create total word elementList, a unique word list, and a hash table
		ArrayList elementList = new ArrayList();
		ArrayList uniqueList = new ArrayList();
	  
		//for every match found populate total word array list
		while (m.find())
		{
			elementList.add(m.group());
		}
		//for every word found in total word list
		for( int i = 0; i < elementList.size(); i++){
			//first see if your word exists in your hashtable
			//if it doesn’t add it to your hash table as the key and setvalue to 1
			//if the word exists in your hash table increment the value
			if (uniqueList.contains(elementList.get(i))){
				int elementCount =
						Integer.parseInt(tags.get(elementList.get(i)).toString());
				elementCount++;
				tags.put((String) elementList.get(i), elementCount);
			}
			else{
				uniqueList.add(elementList.get(i));
				tags.put((String) elementList.get(i),1);
			}
		}
	  
		//output word lists
		System.out.println("unique words : " + uniqueList.size());
		System.out.println("total words : " + elementList.size());
	  
		// Remove unwanted tags
		Set<String> tagsToRemove = new HashSet<String>();
		
		// Flag short and infrequent words for removal
		if(minWordLength > 0 || minCount > 0) {
			Iterator iterator = tags.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				System.out.println("Tag: " + key + " | Count: " + tags.get(key));
				boolean flag = false;
				if(key.length() <= minWordLength) {
					flag = true;
				}
				else if(tags.get(key) < minCount) {
					flag = true;
				}
				
				if(flag) {
					tagsToRemove.add(key);
				}
				
			}
		}
		
		// Remove the unwanted words
		Iterator it = tagsToRemove.iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			tags.remove(key);
			System.out.println("removed tag " + key);
		}
		
		return tags;
	}
}
