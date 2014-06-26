package com.laboni.schooleducareparent.util;

import java.util.ArrayList;

import com.laboni.schooleducareparent.data.AnnouncementData;
import com.laboni.schooleducareparent.data.AssignmentData;
import com.laboni.schooleducareparent.data.CommuincationData;
import com.laboni.schooleducareparent.data.ParentData;
import com.laboni.schooleducareparent.data.StudentData;
import com.laboni.schooleducareparent.data.TeacherList;



public class Constant {
	
	public static ArrayList<AssignmentData> assignmentData = new ArrayList<AssignmentData>();
	public static ArrayList<StudentData> childData = new ArrayList<StudentData>();
	public static ArrayList<ParentData> parentData = new ArrayList<ParentData>();
	public static ArrayList<TeacherList> teacherList = new ArrayList<TeacherList>();
	public static ArrayList<CommuincationData> communicatorData = new ArrayList<CommuincationData>();
	public static ArrayList<AnnouncementData> announcementData = new ArrayList<AnnouncementData>();
	public static ArrayList<AnnouncementData> announcementData1 = new ArrayList<AnnouncementData>();
	public static ArrayList<String> ImagePath = new ArrayList<String>();
	public static String BASEURL = "http://www.schooleducare.com/demo/AndroidDb/Parent/";
}
