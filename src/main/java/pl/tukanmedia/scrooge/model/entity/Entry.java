package pl.tukanmedia.scrooge.model.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table
public class Entry {

	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;
	
	@Column(name="date")
	@Type(type="date")
	private Date date;
	
	@Column(name="amount")
	private BigDecimal amount;
	
	@Column(name="description")
	private String description;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_entry_type")
	private EntryType entryType;
	
	@ManyToOne
	@JoinColumn(name="id_user")
	private User user;
	
	public Entry() {
		amount = BigDecimal.ZERO;
		date = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Entry [id=" + id + ", date=" + date + ", amount=" + amount + ", description=" + description + "]";
	}

	public EntryType getEntryType() {
		return entryType;
	}

	public void setEntryType(EntryType entryType) {
		this.entryType = entryType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
