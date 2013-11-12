package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

@Entity
public class Tag extends Model
{
	public String	name;
	public long		count;
	
	// Lazy creation
	public static Tag findOrCreateByName(String name) {
		Tag tag = find("byName", name).first();
		if(tag == null) {
			System.out.println("Creating a new tag");
			tag = new Tag(name, 0);
		}
		return tag;
	}
	
	private Tag(String name, long count) {
		this.name = name;
		this.count = count;
	}
	
	public long incrementCount(long amount) {
		count += amount;
		this.save();
		return count;
	}
}