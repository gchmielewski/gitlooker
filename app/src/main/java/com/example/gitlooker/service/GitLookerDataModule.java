package com.example.gitlooker.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.gitlooker.model.Repo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by grzesiek on 2016-01-02a
 */
public class GitLookerDataModule {

    private GitLookerSQLiteOpenHelper dbHelper;

    public GitLookerDataModule(Context context) {
        dbHelper = new GitLookerSQLiteOpenHelper(context);
    }

    private void _addRepo(SQLiteDatabase database, Repo repo, long searchId) {

        ContentValues values = new ContentValues();

        values.put("id", repo.id);
        values.put("search_id", searchId);
        values.put("name", repo.name);
        values.put("avatar_url", repo.owner.avatar_url);
        values.put("login", repo.owner.login);
        values.put("_private", repo._private);
        values.put("description", repo.description);
        values.put("stargazers_count", repo.stargazers_count);
        values.put("watchers_count", repo.watchers_count);

        database.insert(GitLookerSQLiteOpenHelper.TABLE_REPO, null, values);
    }

    public ArrayList<Repo> getRepos(int type, String searchText) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        try {

            ArrayList<Repo> result = new ArrayList<Repo>();

            //String[] columns = new String[]{"repo_id", "name", "avatar_url", "login", "_private", "description", "stargazers_count", "watchers_count"};
            //Cursor cursor = database.query(GitLookerSQLiteOpenHelper.TABLE_REPO, columns, "search_id=", null, null, null, "name");

            Cursor cursor = database.rawQuery(
                    "select r.id, r.name, r.avatar_url, r.login, r._private, r.description, r.stargazers_count, r.watchers_count, r.search_id "
                            + "from repo r join search s on r.search_id = s.search_id "
                            + "where s.type = "
                            + type
                            + " and search_text = '"
                            + searchText
                            + "'", null);

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                result.add(new Repo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4),
                        cursor.getString(5), cursor.getInt(6), cursor.getInt(7), cursor.getInt(8)));

                cursor.moveToNext();
            }
            cursor.close();

            return result;
        }
        finally {
            database.close();
        }
    }

    public void saveRepos(List<Repo> items, int type, String searchText) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        try {
            database.beginTransaction();

            // delete earlier searches
            database.execSQL("delete from repo where search_id in (select search_id from search where type = " + type + " and search_text = '" + searchText + "')");
            database.execSQL("delete from search where type = " + type + " and search_text = '" + searchText + "'");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            ContentValues values = new ContentValues();
            values.put("created_at", dateFormat.format(date));
            values.put("type", type);
            values.put("search_text", searchText);

            long searchId = database.insert(GitLookerSQLiteOpenHelper.TABLE_SEARCH, null, values);

            for (Repo r : items) {
                _addRepo(database, r, searchId);
            }

            database.setTransactionSuccessful();
            database.endTransaction();
        }
        finally {
            database.close();
        }
    }

    public ArrayList<String> getSearchWords() {
        ArrayList<String> result = new ArrayList<>();

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        try {
            Cursor cursor = database.rawQuery("select distinct search_text from search", null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();

            return result;
        }
        finally {
            database.close();
        }
    }

    public String getSessionInfo(int searchId) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        try {
            String result = "No data ";

            Cursor cursor = database.rawQuery("select created_at, search_text, type from search where search_id = " + searchId, null);

            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                    result = "Session saved: " + cursor.getString(0) + " search phrase: " + cursor.getString(1);
            }
            cursor.close();

            return result;
        }
        finally {
            database.close();
        }
    }
}
