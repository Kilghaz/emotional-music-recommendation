package de.ur.assistenz.emomusic;

import java.io.File;
import java.sql.*;

public class SettingsManager {

    private static final String DATABASE_LOCATION = ".settings";
    private static final String TABLE_SETTINGS = "settings";
    private static final String COLUMN_VALUE = "value";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TYPE = "type";
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    private static final String TYPE_NUMBER = "Number";
    private static final String TYPE_TEXT = "Text";
    private static final String TYPE_BOOLEAN = "Boolean";

    private static SettingsManager instance = null;

    private Connection connection = null;

    private SettingsManager() {
        instance = this;
        createDatabaseConnection();
    }

    public static synchronized SettingsManager getInstance() {
        return instance == null ? new SettingsManager() : instance;
    }

    private boolean doesDatabaseExist() {
        File databaseFile = new File(DATABASE_LOCATION);
        return databaseFile.exists();
    }

    private void createDatabaseConnection() {
        try {
            boolean needToInitializeDatabase = !doesDatabaseExist();
            Class.forName(DRIVER).newInstance();
            this.connection = DriverManager.getConnection("jdbc:derby:" + DATABASE_LOCATION + ";create=true");
            if(needToInitializeDatabase) {
                initializeDatabase();
            }
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void save(String name, String value, String type) {
        try {
            Statement statement = this.connection.createStatement();
            if(doesDatabaseExist()) {
                String query = "INSERT INTO " + TABLE_SETTINGS +
                        "(" + COLUMN_NAME + ", " + COLUMN_VALUE + ", " + COLUMN_TYPE + ") " +
                        "VALUES ('" + name + "', '" + value + "', '" + type + "')";
                statement.executeUpdate(query);
            }
            else {
                statement.executeQuery("UPDATE " + TABLE_SETTINGS +
                        " SET " + COLUMN_VALUE + "=" + value + ", " + COLUMN_TYPE + "=" + type +
                        " WHERE " + COLUMN_NAME + "=" + name
                );
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ResultValue load(String name) {
        if(!doesSettingExist(name)) {
            return null;
        }
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_NAME + "=" + name);
            resultSet.first();
            ResultValue result = new ResultValue(resultSet.getString(COLUMN_VALUE), resultSet.getString(COLUMN_TYPE));
            statement.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean doesSettingExist(String name){
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_NAME + "=" + name);
            boolean isNotEmpty = resultSet.first();
            statement.close();
            return isNotEmpty;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void initializeDatabase() {
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate("CREATE TABLE " + TABLE_SETTINGS + "(" +
                        COLUMN_NAME + " VARCHAR(256), " +
                        COLUMN_VALUE + " VARCHAR(1024), " +
                        COLUMN_TYPE + " VARCHAR(256)" +
                    ")");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String loadText(String name) {
        ResultValue resultValue = load(name);
        if(resultValue == null) return null;
        return resultValue.value;
    }

    public Double loadNumber(String name) {
        ResultValue resultValue = load(name);
        if(resultValue == null) return null;
        return Double.parseDouble(resultValue.value);
    }

    public Boolean loadBoolean(String name) {
        ResultValue resultValue = load(name);
        if(resultValue == null) return null;
        return Boolean.parseBoolean(resultValue.value);
    }

    public void saveText(String name, String value) {
        save(name, value, TYPE_TEXT);
    }

    public void saveNumber(String name, Double value) {
        save(name, value.toString(), TYPE_NUMBER);
    }

    public void saveBoolean(String name, Boolean value) {
        save(name, value.toString(), TYPE_BOOLEAN);
    }

    private class ResultValue {

        public String value;
        public String type;

        public ResultValue(String value, String type) {
            this.value = value;
            this.type = type;
        }

    }

}
