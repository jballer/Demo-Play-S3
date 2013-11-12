package models;

import java.io.File;
import java.io.InputStream;

import javax.persistence.*;
import play.db.jpa.*;

import play.data.validation.*;
import play.libs.MimeTypes;
import play.modules.s3blobs.S3Blob;

@Entity
public class Document extends Model
{
	@ManyToOne
	public Account uploader;
	
	public String fileName;
	public S3Blob blob;
    public String comment;

    public Document(Account uploadedBy, InputStream is, String fileName, String comment) {
    	// Set properties
    	this.uploader = uploadedBy;
    	this.fileName = fileName;
    	this.comment = comment;
    	
    	// If there's a file, save it as an S3Blob
    	if(is != null) {
    		this.blob = new S3Blob();
    		this.blob.set(is, MimeTypes.getContentType(fileName));
    	}
    }
    
    public Document delete() {
    	this.uploader.documents.remove(this);
    	
    	return super.delete();
    }
    
}
