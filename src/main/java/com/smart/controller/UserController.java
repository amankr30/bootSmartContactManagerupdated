package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
	private BCryptPasswordEncoder passwordEncoder;

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

			if (file.isEmpty()) {
				System.out.println("File is Empty");
				contact.setImage("contact.png");
			} else {

				// file to folder and update the name of contact
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image Uploaded");

			}

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

	// View contacts Handler
	@GetMapping("/view-contact/{page}")
	public String viewContactHandler(@PathVariable("page") Integer page, Model model, Principal principal) {
		System.out.println("Inside View-Contact handler");
		model.addAttribute("title", "View-Contact");

		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);

		// currentPage=page
		// Contact per page= 4
		Pageable pageable = PageRequest.of(page, 4);

		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", contacts.getTotalPages());

		return "normal/view_contact";
	}

	// Showing individual contact in Detail

	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		System.out.println("Inside Indivdial contcat handler");

		Optional<Contact> contactOptinal = this.contactRepository.findById(cId);
		Contact contact = contactOptinal.get();

		// user can see own contacts
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", "Contact-InDeatails");

		}

		return "normal/contact_InDetail";
	}
//DElete handler
	@GetMapping("/delete/{cId}")
	public String deleteString(@PathVariable("cId") Integer cId, Model model, HttpSession session,Principal principal) {
		System.out.println("Inside Delete Contact handler");

		Contact contact = this.contactRepository.findById(cId).get();

		// unlinking from user
		User user=this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		this.userRepository.save(user);
		
		
		
		System.out.println("DELETED");

		session.setAttribute("message", new Message(" Contact Deleted Successfully !!", "alert-success"));

		return "redirect:/user/view-contact/0";
	}

	@PostMapping("/update-contact/{cId}")
	public String updateContacthandler(@PathVariable("cId") Integer cId, Model model, HttpSession session) {
		System.out.println("Inside Upadate-Contact Contact handler");

		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);

		return "normal/update-contact";
	}
	
	//updating and saving
	@PostMapping("/update-contact")
	public String updateContactHandler(@Valid @ModelAttribute Contact contact,BindingResult result,
			@RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session, Model m) {

		
		try {
			
			
			//old contact details
			Contact oldContactDetails=this.contactRepository.findById(contact.getCid()).get();
			
			//image
			if(!file.isEmpty()) {
				//file work
				//rewrite
				//delete old photo and
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContactDetails.getImage());
				file1.delete();
				
				
				
				//upadte new one
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
				
			}
			else {
				contact.setImage(oldContactDetails.getImage());
			}
			
			
			
			User user=this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			// succes message
			session.setAttribute("message", new Message(" Your Contact is Updated !!", "alert-success"));
			
			
		} catch (Exception e) {
			e.printStackTrace();		
			}
	
		System.out.println("UPDATED CONTACT NAME :"+contact.getName());
		System.out.println("CONTACT ID :"+contact.getCid());
		
		return "redirect:/user/"+contact.getCid()+"/contact";

	}
	
	//User profile handler
	@GetMapping("/profile")
	public String yourProfileHandler( Model model) {
		model.addAttribute("title", "Your-Profile");
		return "normal/your_profile";
	}
	
	//User Settings handler
		@GetMapping("/setting")
		public String settingHandler(Model model) {
			model.addAttribute("title", "Your-Setting");
			return "normal/setting";
		}
		
//change password handler
		@PostMapping("/change-password")
		public String changePasswordHandler(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword ,HttpSession session,Principal principal) {
		System.out.println(" Inside Old-password Handler");
		System.out.println("Old-password:  "+oldPassword);
		System.out.println("New-password: "+newPassword);
		
		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		System.out.println(currentUser.getPassword());
		
		if(this.passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
			
			//change password
			currentUser.setPassword(this.passwordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message(" Password Changed !!", "alert-success"));
			
			
		}
		else {
			session.setAttribute("message", new Message("Enter Correct Password !!", "alert-danger"));
			return "redirect:/user/setting"; 
		}
		
		
		return "redirect:/user/index";
			
		}
}
