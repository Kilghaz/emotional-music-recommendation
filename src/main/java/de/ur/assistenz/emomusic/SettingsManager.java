package de.ur.assistenz.emomusic;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SettingsManager {

    private static final String DATABASE_FILE = "settings.db";
    private static final String TABLE_SETTINGS = "settings";
    private static final String COLUMN_VALUE = "value";
    private static final String COLUMN_NAME = "name";
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

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
        File databaseFile = new File(DATABASE_FILE);
        return databaseFile.exists();
    }

    private void createDatabaseConnection() {
        try {
            boolean needToInitializeDatabase = !doesDatabaseExist();
            Class.forName(DRIVER).newInstance();
            this.connection = DriverManager.getConnection("jdbc:derby:" + DATABASE_FILE + ";create=true");
            if(needToInitializeDatabase) {
                initializeDatabase();
            }
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initializeDatabase() {
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate("CREATE TABLE " + TABLE_SETTINGS + "(" +
                        COLUMN_NAME + " VARCHAR(256), " +
                        COLUMN_VALUE + " VARCHAR(1024)" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
