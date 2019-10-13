package pl.tukanmedia.scrooge.model.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import pl.tukanmedia.scrooge.enums.OperationType;
import pl.tukanmedia.scrooge.model.entity.Entry;
import pl.tukanmedia.scrooge.model.entity.EntryFilter;
import pl.tukanmedia.scrooge.model.entity.EntryType;
import pl.tukanmedia.scrooge.model.entity.User;
import pl.tukanmedia.scrooge.model.repository.EntryRepository;
import pl.tukanmedia.scrooge.model.repository.UserRepository;

@Service
public class EntryController {
	
	@Autowired
	private EntryRepository entryRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	public BigDecimal getSumForTypeAndMonthAndYear(OperationType operationType, Long month, Long year) {
		User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		LocalDate date = LocalDate.of(year.intValue(), month.intValue(), 1);
		BigDecimal result = entryRepository.findSumForTypeAndDateRange(date.toString(), date.plusMonths(1).toString(), operationType.getSign(), user.getId());
		return result != null ? result : BigDecimal.ZERO;
	}
	
	public List<Entry> getAll() {
		User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		return entryRepository.findByUserId(user.getId());
	}
	
	public List<Entry> getLast10() {
		User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		return entryRepository.findTop10ByUserIdOrderByDateDesc(user.getId());
	}
	
	public List<Entry> getAllFromCurrentMonth() {
		StringBuilder sb = new StringBuilder();
		User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		sb.append(LocalDate.now().getYear()).append("-").append(LocalDate.now().getMonthValue()).append("-01");
		return entryRepository.findAllFromTimePeriod(sb.toString(), user.getId());
	}
	
	public List<Entry> getAllFromCurrentYear() {
		StringBuilder sb = new StringBuilder();
		User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		sb.append(LocalDate.now().getYear()).append("-01-01");
		return entryRepository.findAllFromTimePeriod(sb.toString(), user.getId());
	}
	
	public void saveOrUpdateEntry(Entry entryDAO) {
		Entry entry;
		if(entryDAO.getId()==null) {
			entry = new Entry();
		} else {
			entry = entryRepository.findOne(entryDAO.getId());	
		}
		entry.setDate(entryDAO.getDate());
		entry.setEntryType(entryDAO.getEntryType());
		entry.setAmount(entryDAO.getAmount());
		entry.setDescription(entryDAO.getDescription());
		entry.setUser(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
		entryRepository.save(entry);
	}
	
	public void delete(Long id) {
		entryRepository.delete(id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Entry> getAllWithCondition(EntryFilter obj) {
		StringBuilder sb = new StringBuilder();
		User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		sb.append("id_user = " + user.getId());
		
		if(obj.getAmountFrom() != null) {
			sb.append(" and ").append(" amount >= " + obj.getAmountFrom());
		}
		
		if(obj.getAmountTo() != null) {
			sb.append(" and ").append(" amount <= " + obj.getAmountTo());
		}
		
		if(obj.getDateFrom() != null) {
			sb.append(" and ").append(" date >= '" + obj.getDateFrom() + "'");
		}
		
		if(obj.getDateTo() != null) {
			sb.append(" and ").append(" date <= '" + obj.getDateTo() + "'");
		}
		
		if(obj.getDescription() != null) {
			sb.append(" and ").append(" description like '%" + obj.getDescription() + "%'");
		}
		if(!obj.getEntryTypes().isEmpty()) {
			StringBuilder idsType = new StringBuilder();
			for (EntryType et : obj.getEntryTypes()) {
				idsType.append(",").append(et.getId());
			}
			sb.append(" and ").append(" id_entry_type in (" + idsType.toString().replaceFirst(",", "") + ")");
		}
		
		if(Arrays.asList("insert", "update", "delete").contains(sb.toString().toLowerCase())) {
			throw new RuntimeException("Niedozwolone znaki w zapytaniu");
		} else {
			Query query = entityManager.createNativeQuery("select * from entry where " + sb.toString(), Entry.class);
			List<Entry> entries = query.getResultList();
			return entries;			
		}
		
	}
}
