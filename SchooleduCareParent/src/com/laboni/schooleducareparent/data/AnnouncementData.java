package com.laboni.schooleducareparent.data;

public class AnnouncementData {
	private String subject;
	private String announcement;
	private String date;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getAnnouncement() {
		return announcement;
	}
	public void setAnnouncement(String announcement) {
		this.announcement = announcement;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public AnnouncementData(String subject, String announcement, String date) {
		super();
		this.subject = subject;
		this.announcement = announcement;
		this.date = date;
	}

}
