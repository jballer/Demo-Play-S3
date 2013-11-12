package models;

import java.io.File;
import java.io.InputStream;

import javax.persistence.*;
import play.db.jpa.*;

import play.data.validation.*;
import play.libs.MimeTypes;

@Entity
public class Document extends Model
{
	@ManyToOne
	public Account uploadedBy;
	
	public String fileName;
	public S3Blob file;
    public String comment;

    public Document(Account uploadedBy, InputStream is, String fileName, String comment) {
    	// Set properties
    	this.uploadedBy = uploadedBy;
    	this.fileName = fileName;
    	this.comment = comment;
    	
    	// If there's a file, save it as an S3Blob
    	if(is != null) {
    		this.file = new S3Blob();
    		this.file.set(is, MimeTypes.getContentType(fileName));
    	}
    }
    
    public Document delete() {
    	if(this.file != null) {
    		file.delete();
    	}
    	
    	this.uploadedBy.documents.remove(this);
    	
    	return super.delete();
    }
    
}
