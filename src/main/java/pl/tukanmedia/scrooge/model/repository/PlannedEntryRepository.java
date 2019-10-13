package pl.tukanmedia.scrooge.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.tukanmedia.scrooge.model.entity.PlannedEntry;

@Repository
public interface PlannedEntryRepository extends JpaRepository<PlannedEntry, Long> {

	List<PlannedEntry> findByUserIdAndYearAndMonth(Long id, Long year, Long month);
	
	//List<PlannedEntry> findByUserId(Long id);
	List<PlannedEntry> findByUserIdAndMonthIsNullAndYearIsNull(Long id);
	
}
