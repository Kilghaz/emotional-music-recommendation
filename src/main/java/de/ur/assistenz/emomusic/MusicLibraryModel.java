package de.ur.assistenz.emomusic;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MusicLibraryModel {

    private static final String TABLE_LIBRARY = "library";
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    private static final String COLUMN_FILE_URL = "file_url";
    private static final String COLUMN_LISTEN_COUNT = "listen_count";
    private static final String COLUMN_SKIP_COUNT = "skip_count";
    private static final String COLUMN_EMOTION = "emotion";


    private static MusicLibraryModel instance = null;

    private Connection connection = null;

    private MusicLibraryModel(){
        instance = this;
    }

    public static synchronized MusicLibraryModel getInstance() {
        return instance == null ? new MusicLibraryModel() : instance;
    }

    private void initializeDatabase() {
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate("CREATE TABLE " + TABLE_LIBRARY + "(" +
                    COLUMN_FILE_URL + " VARCHAR(256) PRIMARY KEY, " +
                    COLUMN_LISTEN_COUNT + " INT, " +
                    COLUMN_SKIP_COUNT + " INT, " +
                    COLUMN_EMOTION + " VARCHAR(256)" +
                ")");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
