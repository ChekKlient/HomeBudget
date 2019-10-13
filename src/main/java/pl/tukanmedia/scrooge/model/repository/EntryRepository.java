package pl.tukanmedia.scrooge.model.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.tukanmedia.scrooge.model.entity.Entry;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {
	
	List<Entry> findByUserId(Long id);
	
	List<Entry> findTop10ByUserIdOrderByDateDesc(Long id);

	@Query(nativeQuery=true, value="select * from entry where date between :firstDayOfMonth and current_date and id_user = :idUser order by date")
	List<Entry> findAllFromTimePeriod(@Param("firstDayOfMonth") String firstDayOfMonth, @Param("idUser") Long idUser);

	@Query(nativeQuery=true, value="select sum(e.amount) from entry e left join entry_type et on e.id_entry_type = et.id where e.date >= :dateFrom and e.date < :dateTo and et.sign = :sign and e.id_user = :idUser")
	BigDecimal findSumForTypeAndDateRange(@Param("dateFrom") String dateFrom, @Param("dateTo") String dateTo, @Param("sign") String sign, @Param("idUser") Long idUser);

}
