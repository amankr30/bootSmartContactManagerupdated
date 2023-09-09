package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	// Adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("USENAME/Email: " + userName);

		// get The user using email
		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER" + user);

		model.addAttribute("user", user);
	}

	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		System.out.println("Inside DashBoard handler");

		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String openAndaddContactForm(Model model, Principal principal) {
		System.out.println("Inside Add-Contact handler");
		model.addAttribute("title", "Add-Contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact";
	}

	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact, BindingResult result1,
			@RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session, Model m) {

		try {
			String userName = principal.getName();
			// get The user using email
			User user = userRepository.getUserByUserName(userName);

			// processing and uploading file
			// validation

			if (result1.hasErrors()) {

				System.out.println(result1.toString());

			}

			// file to folder and update the name of contact
			contact.setImage(file.getOriginalFilename());

			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			System.out.println("Image Uploaded");

			contact.setUser(user);// user ko contact do OR contact ko user
			user.getContacts().add(contact);
			this.userRepository.save(user);

			System.out.println("DATa" + contact);
			System.out.println("DATA added to DATABAse");

			// succes message
			session.setAttribute("message", new Message("Contact added !! Add more..", "alert-success"));

		} catch (Exception e) {

			e.printStackTrace();

			// erro message
			session.setAttribute("message", new Message("Something went Wrong !!" + e.getMessage(), "alert-danger"));

		}
		return "normal/add_contact";

	}
	
	
	
	//View contacts Handler
	@GetMapping("/view-contact/{page}")
	public String viewContactHandler(@PathVariable("page") Integer page,Model model, Principal principal) {
		System.out.println("Inside View-Contact handler");
		model.addAttribute("title", "View-Contact");
		
		
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		
		//currentPage=page
		//Contact per page= 4
		Pageable pageable = PageRequest.of(page, 4);
		
		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(),pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage",contacts.getTotalPages());

		return "normal/view_contact";
	}

}
