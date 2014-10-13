//eg) see NotesDbAdapter 

package com.example.bimbim.notepadv2;

import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

import java.sql.SQLException;

public class Notepadv2 extends ListActivity {
        ...

    private NotesDbAdapter mDbHelper;
    private Cursor mNotesCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list);
        mDbHelper = new NotesDbAdapter(this);
        try {
            mDbHelper.open();
        } catch (SQLException e){

        };
        fillData();
        //each list item can activate the context menu
        registerForContextMenu(getListView());
    }
    private void fillData(){
      mNotesCursor = mDbHelper.fetchAllNotes();
      startManagingCursor(mNotesCursor);

      String[] from = new String[] { NotesDbAdapter.KEY_TITLE};
        //R.id.text1 refers to receptacle for data
      int[] to = new int[] {R.id.text1};

      SimpleCursorAdapter notes =
              new SimpleCursorAdapter(this, R.layout.notes_row, mNotesCursor, from, to);

      setListAdapter(notes);
    }
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item){
        switch(item.getItemId()){
            case INSERT_ID:
                createNote();
                return true;
            case R.id.menu_insert:
                createNote();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }
    private void createNote(){
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);
        //Cursor will point to note that user clicks on
        Cursor c  = mNotesCursor;
        c.moveToPosition(position);
        Intent i = new Intent(this, NoteEdit.class);
        //retrieve information from note cursor pointst o
        //use putExtra to pass in rowId, title, and body of note we want to edit to NoteEdit stage
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        i.putExtra(NotesDbAdapter.KEY_TITLE, c.getString(
                c.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
        i.putExtra(NotesDbAdapter.KEY_BODY, c.getString(
                c.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
        startActivityForResult(i, ACTIVTY_EDIT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();
        switch(requestCode){
            case ACTIVITY_CREATE:
                String title = extras.getString(NotesDbAdapter.KEY_TITLE);
                String body = extras.getString(NotesDbAdapter.KEY_BODY);
                mDbHelper.createNote(title, body);
                fillData();
                break;
            case ACTIVTY_EDIT:
                Long mRowId = extras.getLong(NotesDbAdapter.KEY_ROWID);
                if (mRowId != null) {
                    String editTitle = extras.getString(NotesDbAdapter.KEY_TITLE);
                    String editBody = extras.getString(NotesDbAdapter.KEY_BODY);
                    mDbHelper.updateNote(mRowId, editTitle, editBody);
                }
                fillData();
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

