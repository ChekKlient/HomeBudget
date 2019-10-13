package pl.tukanmedia.scrooge.model.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import pl.tukanmedia.scrooge.model.entity.PlannedEntry;
import pl.tukanmedia.scrooge.model.entity.User;
import pl.tukanmedia.scrooge.model.repository.EntryTypeRepository;
import pl.tukanmedia.scrooge.model.repository.PlannedEntryRepository;
import pl.tukanmedia.scrooge.model.repository.UserRepository;

@Controller
public class PlannedEntryController {

	@Autowired
	private PlannedEntryRepository plannedEntryRepository;
	
	@Autowired
	EntryTypeRepository entryTypeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public List<PlannedEntry> getAllForDate(Long year, Long month) {
		User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		return plannedEntryRepository.findByUserIdAndYearAndMonth(user.getId(), year, month);
	}
	
	public List<PlannedEntry> getAllPredictions() {
		User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		return plannedEntryRepository.findByUserIdAndMonthIsNullAndYearIsNull(user.getId());
	}
	
	public void save(PlannedEntry plannedEntry) {		
		User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		plannedEntry.setUser(user);
		plannedEntryRepository.save(plannedEntry);
	}
	
	public void delete(long id) {
		plannedEntryRepository.delete(id);
	}
	
}
