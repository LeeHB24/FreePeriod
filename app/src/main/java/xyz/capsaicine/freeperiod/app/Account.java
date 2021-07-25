package xyz.capsaicine.freeperiod.app;

public class Account {
    private static int userId = -1;
    private static String userEmail;
    private static String userPassword;
    private static String userSchool;
    private static String userName;

    /* -----------------------------------
        for singleton Account class
     ----------------------------------- */
    private Account(){}

    private static class LazyHolder{
        public static final Account INSTANCE = new Account();
    }

    public static Account getInstance(){
        return LazyHolder.INSTANCE;
    }
    // end of singlethon setting

    public int getUserId() {
        if(userId < 0){
            userId = DB.getInstance().getUserIdByEmail(userEmail);
        }
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserSchool() {
        return userSchool;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserId(int userId) {
        Account.userId = userId;
    }

    public void setUserEmail(String userEmail) {
        Account.userEmail = userEmail;
    }

    public void setUserPassword(String userPassword) {
        Account.userPassword = userPassword;
    }

    public void setUserSchool(String userSchool) {
        Account.userSchool = userSchool;
    }

    public void setUserName(String userName) {
        Account.userName = userName;
    }
}
