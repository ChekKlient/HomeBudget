package pl.tukanmedia.scrooge.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.tukanmedia.scrooge.model.entity.Savings;

@Repository
public interface SavingsRepository extends JpaRepository<Savings, Long>{

	List<Savings> findAllByUserIdOrderByName(Long id);
	
}
