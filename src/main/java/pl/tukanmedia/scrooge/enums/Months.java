package pl.tukanmedia.scrooge.enums;

public enum Months  {
	
	JANUARY(1L, "Styczeń"),
	FEBRUARY(2L, "Luty"),
	MARCH(3L, "Marzec"),
	APRIL(4L, "Kwiecień"),
	MAY(5L, "Maj"),
	JUNE(6L, "Czerwiec"),
	JULY(7L, "Lipiec"),
	AUGUST(8L, "Sierpień"),
	SEPTEMBER(9L, "Wrzesień"),
	OCTOBER(10L, "Październik"),
	NOVEMBER(11L, "Listopad"),
	DECEMBER(12L, "Grudzień");
	
	private Long id;
	private String name;
	
	private Months(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
