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
  public static final String TABLE_SESSION = "session";
  public static final String TABLE_SEARCH = "search";

  private static final String DATABASE_NAME = "gl.db";
  private static final int DATABASE_VERSION = 7;

  public GitLookerSQLiteOpenHelper(Context context)
  {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db)
  {
    db.execSQL("CREATE TABLE " + TABLE_REPO +
        "(" +
        "repo_id integer primary key, " +
        "name text, " +
        "avatar_url text, " +
        "login text, " +
        "_private integer, " +
        "description text, " +
        "stargazers_count integer, " +
        "watchers_count integer " +
        ");");

    db.execSQL("CREATE TABLE " + TABLE_SESSION +
        "(" +
        "created_at datetime not null, " +
        "login text not null " +
        ");");

    db.execSQL("CREATE TABLE " + TABLE_SEARCH +
        "(" +
        "search_id integer primary key autoincrement, " +
        "search_text text " +
        ");");

    db.execSQL("INSERT INTO session(created_at, login) VALUES('datetime()', 'guest')");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPO);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH);

    onCreate(db);
  }

}
