package pl.tukanmedia.scrooge.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.tukanmedia.scrooge.model.entity.EntryType;

@Repository
public interface EntryTypeRepository extends JpaRepository<EntryType, Long> {

	public List<EntryType> findAllByOrderByDescriptionAsc();
	
	public List<EntryType> findBySign(String sign);
	
	public List<EntryType> findBySignOrderByDescriptionAsc(String sign);
	
	public List<EntryType> findAllByOrderBySignAscDescriptionAsc();
	
}
