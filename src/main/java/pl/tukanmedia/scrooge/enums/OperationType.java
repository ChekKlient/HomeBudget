package pl.tukanmedia.scrooge.enums;

import java.util.Arrays;
import java.util.List;

public enum OperationType {
	ALL(0L, "", "Wszystko"),
	INCOME(1L, "+", "Dochody"), 
	LOSS(2L, "-", "Koszty");

	private long id;
	private String sign;
	private String description;

	private OperationType(Long id, String sign, String description) {
		this.id = id;
		this.sign = sign;
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static List<OperationType> getAll() {
		return Arrays.asList(OperationType.values());
        
	}
	
}
