package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class UserTagCount extends Model {
	
	@ManyToOne
	public Account	account;
	
	@ManyToOne
	public Tag		tag;
	
	public long		userTagCount;
	
	public static UserTagCount findOrCreate(Account a, Tag t) {
		UserTagCount userTag = find("byAccountAndTag", a, t).first();
		
		if(userTag == null) {
			System.out.println("Creating new user count for this tag");
			userTag = new UserTagCount(a, t);
		}
		return userTag;
	}
	
	public long increment(long count) {
		userTagCount += count;
		return userTagCount;
	}
	
	private UserTagCount(Account account, Tag tag) {
		this.account = account;
		this.tag = tag;
	}
}
