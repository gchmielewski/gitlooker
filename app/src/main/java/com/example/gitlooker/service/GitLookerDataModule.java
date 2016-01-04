package com.example.gitlooker.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.gitlooker.model.Repo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by grzesiek on 2016-01-02.
 */
public class GitLookerDataModule {

  private GitLookerSQLiteOpenHelper dbHelper;

  public GitLookerDataModule(Context context)  {
    dbHelper = new GitLookerSQLiteOpenHelper(context);
  }

  private void _addRepo(SQLiteDatabase database, Repo repo) {

    ContentValues values = new ContentValues();

    values.put("repo_id", repo.id);
    values.put("name", repo.name);
    values.put("avatar_url", repo.owner.avatar_url);
    values.put("login", repo.owner.login);
    values.put("_private", repo._private);
    values.put("description", repo.description);
    values.put("stargazers_count", repo.stargazers_count);
    values.put("watchers_count", repo.watchers_count);

    database.insert(GitLookerSQLiteOpenHelper.TABLE_REPO, null, values);
  }

  public ArrayList<Repo> getRepos()  {
    SQLiteDatabase database = dbHelper.getReadableDatabase();

    try  {
      ArrayList<Repo> result = new ArrayList<Repo>();

      String[] columns = new String[]{"repo_id", "name", "avatar_url", "login", "_private", "description", "stargazers_count", "watchers_count"};

      Cursor cursor = database.query(GitLookerSQLiteOpenHelper.TABLE_REPO, columns, null, null, null, null, "name");
      cursor.moveToFirst();

      while (!cursor.isAfterLast()) {
        result.add(new Repo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getInt(7)));

        cursor.moveToNext();
      }
      cursor.close();

      return result;
    }
    finally {
      database.close();
    }

  }

  public void saveRepos(List<Repo> items, String repoOwner) {

    SQLiteDatabase database = dbHelper.getWritableDatabase();

    try {

      database.beginTransaction();
      database.execSQL("delete from " + GitLookerSQLiteOpenHelper.TABLE_REPO);

      for (Repo r : items) {
        _addRepo(database, r);
      }

      String sql = "update " + GitLookerSQLiteOpenHelper.TABLE_SESSION + " set created_at = datetime(), login = '" + repoOwner + "'";
      database.execSQL(sql);

      database.setTransactionSuccessful();
      database.endTransaction();
    }
    finally {
      database.close();
    }
  }

  public String getSessionInfo(String repoOwner) {
    SQLiteDatabase database = dbHelper.getReadableDatabase();

    try
    {
      String[] columns = new String[]{"created_at", "login"};
      String result = "No session for " + repoOwner;

      Cursor cursor = database.query(GitLookerSQLiteOpenHelper.TABLE_SESSION, columns, null, null, null, null, null);
      cursor.moveToFirst();

      while (!cursor.isAfterLast()) {
        if (cursor.getString(1).toUpperCase().equals(repoOwner.toUpperCase())) {
          result = "Session saved: " + cursor.getString(0);
        }

        cursor.moveToNext();
      }
      cursor.close();

      return result;
    }
    finally {
      database.close();
    }
  }
}
