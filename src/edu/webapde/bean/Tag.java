package edu.webapde.bean;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity(name = "tags")
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int tagId;
	@Column(nullable = false)
	private String tagname;

	/*
	 * @ManyToMany(cascade=CascadeType.ALL, mappedBy="tags") private Set<Photo>
	 * tags;
	 */
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

	@Override
	public int hashCode() {
		return (this.tagname.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		System.out.println("HEYYY");
		if (obj instanceof Tag) {
			Tag t = (Tag) obj;

			return t.getTagname().equalsIgnoreCase(tagname);
		} else
			return false;
	}

	@Override
	public String toString() {
		return "Tag [tagId=" + tagId + ", tagname=" + tagname + "]";
	}

}
