package com.byted.camp.todolist.db;
import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Entry.TABLE_NAME + " (" +
                    Entry._ID + " INTEGER PRIMARY KEY," +
                    Entry.COLUMN_NAME_ID + " TEXT," +
                    Entry.COLUMN_NAME_DATE + " TEXT," +
                    Entry.COLUMN_NAME_STATE + " TEXT," +
                    Entry.COLUMN_NAME_CONTENT + " TEXT," +
                    Entry.COLUMN_NAME_PRIORITY + " TEXT)";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + Entry.TABLE_NAME;

    private TodoContract() {
    }

    public static class Entry implements BaseColumns {
        public static final String TABLE_NAME = "note_entry";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_PRIORITY = "priority";
    }
}
