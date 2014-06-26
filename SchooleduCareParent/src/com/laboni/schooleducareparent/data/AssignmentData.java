package com.laboni.schooleducareparent.data;

public class AssignmentData {

	private String subject;
	private String assignmentDecription;
	private String assignmentPath;
	private String assignmentsubmissionDate;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getAssignmentDecription() {
		return assignmentDecription;
	}
	public void setAssignmentDecription(String assignmentDecription) {
		this.assignmentDecription = assignmentDecription;
	}
	public String getAssignmentPath() {
		return assignmentPath;
	}
	public void setAssignmentPath(String assignmentPath) {
		this.assignmentPath = assignmentPath;
	}
	public String getAssignmentsubmissionDate() {
		return assignmentsubmissionDate;
	}
	public void setAssignmentsubmissionDate(String assignmentsubmissionDate) {
		this.assignmentsubmissionDate = assignmentsubmissionDate;
	}
	public AssignmentData(String subject, String assignmentDecription,
			String assignmentPath, String assignmentsubmissionDate) {
		super();
		this.subject = subject;
		this.assignmentDecription = assignmentDecription;
		this.assignmentPath = assignmentPath;
		this.assignmentsubmissionDate = assignmentsubmissionDate;
	}
}
