package pl.tukanmedia.scrooge.model.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pl.tukanmedia.scrooge.helper.CustomUserDetails;
import pl.tukanmedia.scrooge.model.entity.User;
import pl.tukanmedia.scrooge.model.repository.UserRepository;

@Service
public class UserController implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		return new CustomUserDetails(user.getUsername(), 
									 user.getPassword(), 
									 user.isEnabled(), 
									 user.isAccountNonExpired(), 
									 user.isCredentialsNonExpired(), 
									 user.isAccountNonLocked(), 
									 user.getAuthorities());
	}
	
	public Boolean isUserExists(String username) {
		return userRepository.findByUsername(username) != null;
	}

}
