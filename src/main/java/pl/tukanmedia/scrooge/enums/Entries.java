package pl.tukanmedia.scrooge.enums;

public enum Entries {

	GET_ALL(1L),
	GET_CURRENT_YEAR(2L),
	GET_CURRENT_MONTH(3L),
	GET_LAST_10(4L);
	
	Long type;

	private Entries(Long type){
		this.type = type;
	}
	
}
