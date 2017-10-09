package cn.dravvern.base;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserInfo {

    private UserInfo() {
    }

    private static class OperatorLoader {
        private static final UserInfo instance = new UserInfo();
    }

    public static UserInfo getInstance() {
        return OperatorLoader.instance;
    }
    private int userId;
    private String userLoginName;
    private String userName;
    private String userSex;
    private int userRoll;
    private String userLoginPwd;
    private int userStatus;
    private String acceptId;
    
    public String toString() {
        return "UserInfo [userId=" + userId + ", userLoginName=" + userLoginName + ", userName=" + userName
                + ", userSex=" + userSex + ", userRoll=" + userRoll + ", userLoginPwd=" + userLoginPwd + ", userStatus="
                + userStatus + ", acceptId=" + acceptId + "]";
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserLoginName() {
        if (userLoginName == null || userLoginName.length() <= 0) {
            userLoginName = "admin";
        }
        return userLoginName;
    }

    public void setUserLoginName(String userLoginName) {
        this.userLoginName = userLoginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public int getUserRoll() {
        return userRoll;
    }

    public void setUserRoll(int userRoll) {
        this.userRoll = userRoll;
    }

    public String getUserLoginPwd() {
        return userLoginPwd;
    }

    public void setUserLoginPwd(String userLoginPwd) {
        this.userLoginPwd = userLoginPwd;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public String getAcceptId() {
        if (acceptId == null || acceptId.length() <= 0) {
            DateTimeFormatter formate = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            acceptId = LocalDateTime.now().format(formate) + getRandomString(6);
        }
        return acceptId;
    }

    public void setAcceptId(String acceptId) {
        this.acceptId = acceptId;
    }

    public String checkLogin(String name, String pwd) {
        String sql = "select user_id, user_login_name, user_name, user_sex, user_roll, "
                + "user_login_pwd, user_status from tg_user  where user_login_name=? ";
        Object[] result = Dao.getInstance().QueryOnlyNote(sql, name);

        String errmsg = null;
        if (result != null) {
            this.userId = ((BigDecimal) result[0]).intValue();
            this.userLoginName = (String) result[1];
            this.userName = (String) result[2];
            this.userSex = (String) result[3];
            this.userRoll = ((BigDecimal) result[4]).intValue();
            this.userLoginPwd = (String) result[5];
            this.userStatus = ((BigDecimal) result[6]).intValue();
            if (this.userStatus != 1) {
                errmsg = "用户状态不正常，请确认后重新登录！";
            }
            if (!pwd.equals(this.userLoginPwd)) {
                errmsg = "登录密码错误，请确认后重新登录！";
            } else {
                
            }
        } else {
            errmsg = "用户不存在，请确认后重新登录！";
        }
        System.out.println(this.toString());
        return errmsg;
    }

    private static String getRandomString(int length) {
        String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer sb = new StringBuffer();
        int len = KeyString.length();
        for (int i = 0; i < length; i++) {
            sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
        }
        return sb.toString();
    }
}
