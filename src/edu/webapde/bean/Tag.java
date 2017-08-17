package edu.webapde.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="tags")
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int tagId;
	@Column(nullable=false)
	private String tagname;
	
	public Tag() {
		// TODO Auto-generated constructor stub
	}
	
	public Tag(String tagname) {
		// TODO Auto-generated constructor stub
		this.tagname = tagname;
	}
	
	public int getTagId() {
		return tagId;
	}
	
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}
	
	public String getTagname() {
		return tagname;
	}
	
	public void setTagname(String tagname) {
		this.tagname = tagname;
	}

	public boolean equals(Tag t) {
		return t.getTagname().equalsIgnoreCase(tagname);
	}
	
	@Override
	public String toString() {
		return "Tag [tagId=" + tagId + ", tagname=" + tagname + "]";
	}
	
	
}
