package com.example.gitlooker.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by grzesiek on 2016-01-02a
 */
public class GitLookerSQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String TABLE_REPO = "repo";
    public static final String TABLE_SEARCH = "search";

    private static final String DATABASE_NAME = "gl.db";
    private static final int DATABASE_VERSION = 9;

    public GitLookerSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_REPO +
                "(" +
                "repo_id integer primary key autoincrement, " +
                "id integer not null, " +
                "search_id integer not null, " +
                "name text, " +
                "avatar_url text, " +
                "login text, " +
                "_private integer, " +
                "description text, " +
                "stargazers_count integer, " +
                "watchers_count integer " +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_SEARCH +
                "(" +
                "search_id integer primary key autoincrement, " +
                "created_at datetime not null, " +
                "type integer, " +
                "search_text text " +
                ");");

        db.execSQL("CREATE INDEX i_repo_search_id  ON repo (search_id);");
        db.execSQL("CREATE UNIQUE INDEX u_search_type_text ON search (type, search_text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPO);
        db.execSQL("DROP TABLE IF EXISTS session");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH);

        onCreate(db);
    }
}
