package de.ur.assistenz.emomusic.sql;

import de.hijacksoft.oosql.SQLColumn;
import de.hijacksoft.oosql.SQLPrimaryKey;
import de.hijacksoft.oosql.SQLTable;

@SQLTable("settings")
@SQLPrimaryKey("name")
public class Setting {

    public Setting(){}

    public Setting(String name, String value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    @SQLColumn("name")
    private String name;

    @SQLColumn("value")
    private String value;

    @SQLColumn("type")
    private String type;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}