package de.ur.assistenz.emomusic.sql;

import de.hijacksoft.oosql.SQLColumn;
import de.hijacksoft.oosql.SQLPrimaryKey;
import de.hijacksoft.oosql.SQLTable;

@SQLTable("library")
@SQLPrimaryKey("id")
public class Song {

    @SQLColumn(value = "id", autoIncrement = true)
    private int id;

    @SQLColumn("name")
    private String name;

    @SQLColumn("url")
    private String url;

    @SQLColumn("skip_count")
    private int skipCount;

    @SQLColumn("emotion")
    private String emotion;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSkipCount() {
        return skipCount;
    }

    public void setSkipCount(int skipCount) {
        this.skipCount = skipCount;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

}