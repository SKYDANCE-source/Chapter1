package com.byted.camp.todolist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;
import com.byted.camp.todolist.db.TodoContract.Entry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }
        });

        recyclerView.setAdapter(notesAdapter);
        notesAdapter.refresh(loadNotesFromDatabase());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase(){
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        TodoDbHelper dbHelper = new TodoDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Note> noteList = new ArrayList<Note>();

        String[] projection = {
                BaseColumns._ID,
                Entry.COLUMN_NAME_ID,
                Entry.COLUMN_NAME_DATE,
                Entry.COLUMN_NAME_STATE,
                Entry.COLUMN_NAME_CONTENT,
                Entry.COLUMN_NAME_PRIORITY
        };

                String sortOrder =
                        Entry.COLUMN_NAME_PRIORITY + " DESC";

        Cursor cursor = db.query(
                Entry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        while (cursor.moveToNext()) {
            String itemId = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_NAME_ID));
            String date = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_NAME_DATE));
            String state = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_NAME_STATE));
            String content = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_NAME_CONTENT));
            String priority = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_NAME_PRIORITY));

            long Long_id = Long.parseLong(itemId);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat
                    = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            Date Datetime = null;
            try {
                Datetime = simpleDateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int Int_priority = Integer.parseInt(priority);

            Note note1 = new Note(Long_id);
            note1.setDate(Datetime);
            if(state.equals("TODO")){
                note1.setState(State.TODO);
            }else if(state.equals("DONE")){
                note1.setState(State.DONE);
            }else{
                System.out.println("wrong");
            }
            note1.setContent(content);
            note1.setPriority(Int_priority);

            noteList.add(note1);
        }
        cursor.close();
        return noteList;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        TodoDbHelper dbHelper = new TodoDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        String date = simpleDateFormat.format(note.getDate());
        String selection = Entry.COLUMN_NAME_DATE + " LIKE ?";
        String[] selectionArgs = {date};
        int deletedRows = db.delete(Entry.TABLE_NAME, selection, selectionArgs);
    }

    private void updateNode(Note note) {
        // TODO 更新数据
        TodoDbHelper dbHelper = new TodoDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Entry.COLUMN_NAME_STATE,"DONE");
        values.put(Entry.COLUMN_NAME_PRIORITY,"0");

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        String date = simpleDateFormat.format(note.getDate());
        String selection = Entry.COLUMN_NAME_DATE + " LIKE ?";
        String[] selectionArgs = {date};

        int count = db.update(
                Entry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

}
