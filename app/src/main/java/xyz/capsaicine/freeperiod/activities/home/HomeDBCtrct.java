package xyz.capsaicine.freeperiod.activities.home;

public class HomeDBCtrct {

    public static final String homeDB = "dbHome.db";

    public static final String lectureIdStr = "lectureId";
    public static final String lectureColorStr = "lectureColor";


//    public static final String timeTableCellColorListTable = "timeTableCellColorList";
    public static final String lectureTimeTableCellColorListTable = "lectureTimeTableCellColorListTable";
//    public static final String partyTimeTableCellColorListTable = "partyTimeTableCellColorListTable";

    public static final String SQL_createLectureColorListTable = "CREATE TABLE IF NOT EXISTS " + lectureTimeTableCellColorListTable +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            HomeDBCtrct.lectureIdStr + " INTEGER," +
            HomeDBCtrct.lectureColorStr + " TEXT);";

    public static String SQL_insertLectureColor(int lectureId, String colorValueOfLectureTimeTableCell) {
        String insertQuery = "INSERT INTO " + HomeDBCtrct.lectureTimeTableCellColorListTable +
                " VALUES(" + HomeDBCtrct.lectureIdStr + lectureId +
                ",'" + HomeDBCtrct.lectureColorStr + colorValueOfLectureTimeTableCell + "')";
        return insertQuery;
    }

    public static String SQL_deleteLectureColor(int lectureId) {
        return "DELETE FROM " + HomeDBCtrct.lectureTimeTableCellColorListTable +
                " WHERE " + HomeDBCtrct.lectureIdStr + "=" + lectureId +";";
    }

    public static final String colorId = "colorId";
    public static final String isColorUsed = "isColorUsed";

}
