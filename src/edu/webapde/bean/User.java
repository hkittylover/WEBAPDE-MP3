package edu.webapde.bean;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity(name="users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	@Column(nullable=false)
	private String username;
	@Column(nullable=false)
	private char[] password;
	@Column
	private String description;
	/*
	@OneToMany(cascade=CascadeType.ALL, mappedBy="user")
	private Set<Photo> photos;
	@ManyToMany(cascade=CascadeType.ALL, mappedBy="allowedUsers")
	private Set<Photo> sharedPhotos;
	*/
	
	public User() {
		// TODO Auto-generated constructor stub
		
	}
	
	public User(String username, char[] password) {
		// TODO Auto-generated constructor stub
		this.username = username;
		this.password = password;
		this.description = "";
	}
	
	public User(String username, String password) {
		// TODO Auto-generated constructor stub
		this(username, password.toCharArray());
	}
	
	public User(String username, char[] password, String description) {
		// TODO Auto-generated constructor stub
		this(username, password);
		this.description = description;
	}
	
	public User(String username, String password, String description) {
		// TODO Auto-generated constructor stub
		this(username, password.toCharArray(), description);
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPasswordEqual(char[] password) {
		if(this.password.length == password.length) {
			for(int i = 0; i < password.length; i++) {
				if(this.password[i] != password[i])
					return false;
				return true;
			}
		}
		return false;
	}
	
	public boolean isPasswordEqual(String password) {
		return isPasswordEqual(password.toCharArray());
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", username=" + username + ", password=" + Arrays.toString(password)
				+ ", description=" + description + "]";
	}

	public boolean equals(User u) {
		return this.username.equalsIgnoreCase(u.getUsername());
	}
	
}
