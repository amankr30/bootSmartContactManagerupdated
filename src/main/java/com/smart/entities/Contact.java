package com.smart.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="CONTACT")
public class Contact {

	@Id
	@SequenceGenerator(name="SequenceGenerator", allocationSize=1) 
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SequenceGenerator")
	private int Cid;
	
	@NotBlank(message = "User Name can not be empty !!")
	@jakarta.validation.constraints.Size(min = 3, max = 20, message = "3 - 20 characters Allowed !!")
	private String name;
	
	@Column(name = "nickName")
	private String secondName;
	private String work;
	@jakarta.validation.constraints.Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")
	private String email;
	@NotBlank(message = "Phone Number can't be Empty !!")
	private String phone;
	private String image;
	
	@Column(length = 5000)
	private String description;
	
	@ManyToOne
	private User user;

	public int getCid() {
		return Cid;
	}

	public void setCid(int cid) {
		Cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
}
