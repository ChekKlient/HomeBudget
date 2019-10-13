package pl.tukanmedia.scrooge.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class EntryFilter {

	private LocalDate dateFrom;
	private LocalDate dateTo;
	private List<EntryType> entryTypes;
	private BigDecimal amountFrom;
	private BigDecimal amountTo;
	private String description;
	
	public LocalDate getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(LocalDate dateFrom) {
		this.dateFrom = dateFrom;
	}
	public LocalDate getDateTo() {
		return dateTo;
	}
	public void setDateTo(LocalDate dateTo) {
		this.dateTo = dateTo;
	}
	public List<EntryType> getEntryTypes() {
		return entryTypes;
	}
	public void setEntryTypes(List<EntryType> entryTypes) {
		this.entryTypes = entryTypes;
	}
	public BigDecimal getAmountFrom() {
		return amountFrom;
	}
	public void setAmountFrom(BigDecimal amountFrom) {
		this.amountFrom = amountFrom;
	}
	public BigDecimal getAmountTo() {
		return amountTo;
	}
	public void setAmountTo(BigDecimal amountTo) {
		this.amountTo = amountTo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
