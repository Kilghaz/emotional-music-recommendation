package de.ur.assistenz.emomusic;

import de.hijacksoft.oosql.DerbyAdapter;

public class DatabaseAdapter {

    private static final String DATABASE_LOCATION = ".database";

    private static DatabaseAdapter instance = null;
    private DerbyAdapter derby;

    private DatabaseAdapter(){
        derby = new DerbyAdapter(DATABASE_LOCATION);
        instance = this;
    }

    public static synchronized DatabaseAdapter getInstance(){
        return instance == null ? new DatabaseAdapter() : instance;
    }

    public DerbyAdapter getAdapter() {
        return this.derby;
    }

}
