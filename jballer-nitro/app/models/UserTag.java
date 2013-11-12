package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class UserTag extends Model {
	public Account	account;
	public Tag		tag;
	public long		userTagCount;
}
