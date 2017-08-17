package edu.webapde.bean;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import edu.webapde.service.TagService;

@Entity(name = "photos")
public class Photo {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int photoId;
	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="userId")
	private User user;
	@Column(nullable=false)
	private String title;
	@Column
	private String description;
	@Column(nullable=false)
	private String filepath;
	@Column(nullable=false)
	private String privacy;
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(name="phototag", joinColumns={@JoinColumn(name="photoId")}, inverseJoinColumns={@JoinColumn(name="tagId")})
	private Set<Tag> tags;
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="alloweduser")
	@JoinTable(name="alloweduser", joinColumns={@JoinColumn(name="photoId")}, inverseJoinColumns={@JoinColumn(name="userId")})
	private Set<User> allowedUsers;

	public Photo() {
		// TODO Auto-generated constructor stub
	}

	public Photo(User user, String title, String filepath, String privacy) {
		// TODO Auto-generated constructor stub
		this.user = user;
		this.title = title;
		this.description = "";
		this.filepath = filepath;
		this.privacy = privacy;
		this.allowedUsers = new HashSet<>();
		this.tags = new HashSet<>();
	}
	
	public Photo(User user, String title, String description, String filepath, String privacy) {
		// TODO Auto-generated constructor stub
		this(user, title, filepath, privacy);
		this.description = description;
	}
	
	public int getPhotoId() {
		return photoId;
	}

	public void setPhotoId(int photoId) {
		this.photoId = photoId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getPrivacy() {
		return privacy;
	}

	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public Set<User> getAllowedUsers() {
		return allowedUsers;
	}

	public void setAllowedUsers(Set<User> allowedUsers) {
		this.allowedUsers = allowedUsers;
	}

	@Override
	public String toString() {
		return "Photo [photoId=" + photoId + ", user=" + user + ", title=" + title + ", description=" + description
				+ ", filepath=" + filepath + ", privacy=" + privacy + ", tags=" + tags + ", allowedUsers="
				+ allowedUsers + "]";
	}
/*
	private String toStringTags() {
		String str = "[";
		for(int i = 0; i < tags.size(); i++) {
			str += "\"" + tags.get(i) + "\"";
			if(i + 1 < tags.size())
				str += ", ";
		}
		str += "]";
		return str;
	}
	
	private String toStringAllowedUsers() {
		String str = "[";
		for(int i = 0; i < allowedUsers.size(); i++) {
			str += "\"" + allowedUsers.get(i) + "\"";
			if(i + 1 < allowedUsers.size())
				str += ", ";
		}
		str += "]";
		return str;
	}
*/
	public boolean addTag(String tag) {
		return addTag(new Tag(tag));
	}
	public boolean addTag(Tag tag) {
		if(!containInList((HashSet<Tag>) tags, tag) && !tag.getTagname().equals("")) {
			tags.add(TagService.addTag(tag));
			return true;
		}
		return false;
	}
	
	public boolean addAllowedUser(User u) {
		if(!containInList((HashSet<User>) allowedUsers, u) && !this.user.equals(u) && !u.getUsername().equals("")) {
			allowedUsers.add(u);
			return true;
		}
		return false;
	}
	
	private boolean containInList(HashSet<User> list, User user) {
		Iterator<User> i = list.iterator();
		while(i.hasNext()) {
			User u = i.next();
			if(user.equals(u))
				return true;
		}
		return false;
	}
	
	private boolean containInList(HashSet<Tag> list, Tag tag) {
		Iterator<Tag> i = list.iterator();
		while(i.hasNext()) {
			Tag t = i.next();
			if(tag.equals(t))
				return true;
		}
		return false;
	}
	
}
