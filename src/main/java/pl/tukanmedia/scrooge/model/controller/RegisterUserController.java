package pl.tukanmedia.scrooge.model.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import pl.tukanmedia.scrooge.model.entity.User;
import pl.tukanmedia.scrooge.model.repository.UserRepository;

@Service
public class RegisterUserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public void save(String username, String password) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		userRepository.save(user);
	}
	
	public Boolean isUserExists(String username) {
		return userRepository.findByUsername(username) != null;
	}
}
