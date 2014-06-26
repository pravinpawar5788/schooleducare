package com.laboni.schooleducareparent.data;

public class ParentData {
private String p_Name;
private String p_Relation;
private String p_MobileNo;
private String p_ImagePath;
public String getP_Name() {
	return p_Name;
}
public void setP_Name(String p_Name) {
	this.p_Name = p_Name;
}
public String getP_Relation() {
	return p_Relation;
}
public void setP_Relation(String p_Relation) {
	this.p_Relation = p_Relation;
}
public ParentData(String p_Name, String p_Relation, String p_MobileNo,
		String p_ImagePath) {
	super();
	this.p_Name = p_Name;
	this.p_Relation = p_Relation;
	this.p_MobileNo = p_MobileNo;
	this.p_ImagePath = p_ImagePath;
}
public String getP_MobileNo() {
	return p_MobileNo;
}
public void setP_MobileNo(String p_MobileNo) {
	this.p_MobileNo = p_MobileNo;
}
public String getP_ImagePath() {
	return p_ImagePath;
}
public void setP_ImagePath(String p_ImagePath) {
	this.p_ImagePath = p_ImagePath;
}
}
