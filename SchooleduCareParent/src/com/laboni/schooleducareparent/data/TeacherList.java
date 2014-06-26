package com.laboni.schooleducareparent.data;

public class TeacherList {
private String Teacherd;
private String TeacherFname;
private String TeacherLname;
public String getTeacherd() {
	return Teacherd;
}
public void setTeacherd(String teacherd) {
	Teacherd = teacherd;
}
public String getTeacherFname() {
	return TeacherFname;
}
public void setTeacherFname(String teacherFname) {
	TeacherFname = teacherFname;
}
public String getTeacherLname() {
	return TeacherLname;
}
public void setTeacherLname(String teacherLname) {
	TeacherLname = teacherLname;
}
public TeacherList(String teacherd, String teacherFname, String teacherLname) {
	super();
	Teacherd = teacherd;
	TeacherFname = teacherFname;
	TeacherLname = teacherLname;
}
}
