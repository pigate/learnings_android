//eg) last example NotesDbAdapter class. To see uses, find( last example uses)
// See Notepadv2.java for example of how to use this class
package com.example.bimbim.notepadv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by bimbim on 10/10/14.
 */
public class NotesDbAdapter {
    public static final String KEY_TITLE="title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";

    /*TAG for use in logging */
    private static final String TAG = "NotesDbAdapter";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE=
            "create table notes (_id integer primary key autoincrement," +
                    "title text not null, body text not null);";
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to version " + newVersion+". This will delete " +
                    "all old content.");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }
    /*
    Constructor- takes the context to allow database to be opened/created

    @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx){
        mCtx = ctx;
    }
    /*
    Open the notes database. If it cannot be opened try to create new instance of database.
    If it cannot be created, throw SQLException to signal the failure

    @return this (self reference, allows this to be chained in an initialization call

    @throws SQLException if dabase can neither be opened nor created
     */
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        mDbHelper.close();
    }

    /*
    Create a new note from the title and body provided.
    Returns new rowId if successful insert, -1 if failed to insert.

    @param title the title of the note
    @param body the body of the note

    @return rowId if success, else -1 if failed.
     */
    public long createNote(String title, String body){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    /*
    Delete the note with the given rowId

    @param rowId id of note to delete
    @return true if deleted, false otherwise.
     */
    public boolean deleteNote(long rowId){
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    /*
    Return a Cursor over the list of all notes in database

    @return Cursor over all notes
     */
    public Cursor fetchAllNotes(){
        return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE, KEY_BODY}, null, null, null, null, null);
    }
    /*
    Return Cursor positioned at note that matches rowId

    @param rowId id of note to position Cursor at
    @return Cursor positioned at note if found
    @throws SQLException if note could not be found
     */
    public Cursor fetchNote(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE, KEY_BODY},
                KEY_ROWID + "=" + rowId, null, null, null, null);
        if (mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /*
    Update the note using details provided.
    Note id is specified by rowId. Note is altered by title and body values passed in.

    @param rowId id of note to update
    @param title value to set note title to
    @param body value to set note body to
    @return true if note was successfully updated, else returns false otherwise.
     */
    public boolean updateNote(long rowId, String title, String body){
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}

