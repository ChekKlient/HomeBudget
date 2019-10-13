package pl.tukanmedia.scrooge.model.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import pl.tukanmedia.scrooge.model.entity.Savings;
import pl.tukanmedia.scrooge.model.entity.User;
import pl.tukanmedia.scrooge.model.repository.SavingsRepository;
import pl.tukanmedia.scrooge.model.repository.UserRepository;

@Controller
public class SavingsController {
	
	@Autowired
	private SavingsRepository savingsRepository;

	@Autowired
	private UserRepository userRepository;
	
	public List<Savings> getAllSavings() {
		User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		return savingsRepository.findAllByUserIdOrderByName(user.getId());
	}

	public void delete(Long id) {
		savingsRepository.delete(id);
	}

	public void saveOrUpdateEntry(Savings elementDAO) {
		Savings element;
		if(elementDAO.getId()==null) {
			element = new Savings();
		} else {
			element= savingsRepository.findOne(elementDAO.getId());	
		}
		element.setName(elementDAO.getName());
		element.setAmount(elementDAO.getAmount());
		element.setUser(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
		savingsRepository.save(element);
	}
	
}
