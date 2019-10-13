package pl.tukanmedia.scrooge.model.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.tukanmedia.scrooge.enums.OperationType;
import pl.tukanmedia.scrooge.model.entity.EntryType;
import pl.tukanmedia.scrooge.model.repository.EntryTypeRepository;

@Service
public class EntryTypeController {

	@Autowired
	private EntryTypeRepository repository;
	
	public List<EntryType> getAllBySign(OperationType operationType) {
		if(operationType == OperationType.ALL) {
			return repository.findAllByOrderBySignAscDescriptionAsc();
		}
		return repository.findBySignOrderByDescriptionAsc(operationType.getSign());
	}
	
	public List<EntryType> getAll() {
		return repository.findAll();
	}
	
}
