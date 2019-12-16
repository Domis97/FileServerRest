package storage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Db_api {
    public String function_get_user_location(String userName, Connection conn) {


        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement("select * from get_user_location('" + userName + "');");
            return anwser(preparedStatement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public boolean function_select_user(String userName, Connection conn) {


        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement("select * from select_user('" + userName + "');");
            return anwser(preparedStatement.executeQuery()).equals("t");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void procedure_add_user(String userName, Connection conn) {

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement("call add_user" + "('" + userName + "');");
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void procedure_add_log(String userName,String log, Connection conn) {

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement("call add_log" + "('" + userName + "','" + log + "');");
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void procedure_change_location(String userName, String changeLocation, Connection conn) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement("call change_location" + "('" + userName + "','" + changeLocation + "');");
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private String anwser(ResultSet rs) {

        String answer = null;
        try {
            answer = "";
            int count = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= count; i++) {
                    answer += (rs.getString(i));

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return answer;
    }
}
