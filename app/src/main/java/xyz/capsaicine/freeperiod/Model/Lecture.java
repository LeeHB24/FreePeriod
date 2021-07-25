package xyz.capsaicine.freeperiod.Model;

public class Lecture {

    private int id;
    private String lecture_info;

    public void setId(int id) {
        this.id = id;
    }

    public void setLecture_info(String lecture_info) {
        this.lecture_info = lecture_info;
    }

    public int getId() {
        return id;
    }

    public String getLecture_info() {
        return lecture_info;
    }

    public Lecture(int id, String lecture_info) {
        this.id = id;
        this.lecture_info = lecture_info;
    }

//    private int lectureId;
//    private String lectureName;
//    private ArrayList<ClassInfo> lectureClasses;
//    private String lectureProfessor;

//    public Lecture(int id, String name, String professor){
//        lectureId = id;
//        lectureName = name;
//        lectureClasses = new ArrayList<ClassInfo>();
//        lectureProfessor = professor;
//    }
//
//    public Lecture(int id, String name, ArrayList<ClassInfo> classes, String professor){
//        lectureId = id;
//        lectureName = name;
//        lectureClasses = classes;
//        lectureProfessor = professor;
//    }
//
//    public void addLectureClass(ClassInfo classInfo){
//        lectureClasses.add(classInfo);
//    }
//    public void resetLectrueClass(){
//        lectureClasses = new ArrayList<ClassInfo>();
//    }
//
//    public void setLectureName(String name){
//        lectureName = name;
//    }
//    public void setLectureProfessor(String professor){
//        lectureProfessor = professor;
//    }
//    public void setLectureClasses(ArrayList<ClassInfo> lectureClasses) {
//        this.lectureClasses = lectureClasses;
//    }
//
//    public int getLectureId(){
//        return lectureId;
//    }
//    public String getLectureName(){
//        return lectureName;
//    }
//    public ArrayList<ClassInfo> getLectureClasses(){
//        return lectureClasses;
//    }
//    public String getLectureProfessor(){
//        return lectureProfessor;
//    }
}
