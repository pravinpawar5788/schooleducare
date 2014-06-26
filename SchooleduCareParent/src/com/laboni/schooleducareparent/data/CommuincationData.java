package com.laboni.schooleducareparent.data;

public class CommuincationData {
	
	private String message;
	private String fromflag;
	private String seenflag;
	
	public CommuincationData(String message, String fromflag, String seenflag) {
		super();
		this.message = message;
		this.fromflag = fromflag;
		this.seenflag = seenflag;
	}
	public String getSeenflag() {
		return seenflag;
	}
	public void setSeenflag(String seenflag) {
		this.seenflag = seenflag;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getFromflag() {
		return fromflag;
	}
	public void setFromflag(String fromflag) {
		this.fromflag = fromflag;
	}

}
