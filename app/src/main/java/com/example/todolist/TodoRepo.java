package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TodoRepo extends SQLiteOpenHelper {

    private static final String TAG = "TodoRepo"; // Tag để lọc log

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Todo.db";
    public static final String TABLE_NAME = "todos";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DEADLINE = "deadline";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_IS_COMPLETED = "is_completed";
    public static final String COLUMN_CONTACTS = "contacts";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " TEXT PRIMARY KEY," +
            COLUMN_TITLE + " TEXT," +
            COLUMN_DESCRIPTION + " TEXT," +
            COLUMN_DEADLINE + " TEXT," +
            COLUMN_STATUS + " TEXT," +
            COLUMN_IS_COMPLETED + " INTEGER," +
            COLUMN_CONTACTS + " TEXT)";

    public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private final Gson gson = new Gson();

    public TodoRepo(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Creating database table.");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: Upgrading database. Deleting old table.");
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    public void addTodo(TodoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, item.getId());
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_DEADLINE, item.getDeadline());
        values.put(COLUMN_STATUS, item.getStatus());
        values.put(COLUMN_IS_COMPLETED, item.isCompleted() ? 1 : 0);
        values.put(COLUMN_CONTACTS, gson.toJson(item.getContacts()));

        db.insert(TABLE_NAME, null, values);
        db.close();
        Log.d(TAG, "addTodo: Added new todo -> " + item.getTitle());
    }

    public boolean updateTodo(TodoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_DEADLINE, item.getDeadline());
        values.put(COLUMN_STATUS, item.getStatus());
        values.put(COLUMN_IS_COMPLETED, item.isCompleted() ? 1 : 0);
        values.put(COLUMN_CONTACTS, gson.toJson(item.getContacts()));

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{item.getId()});
        db.close();
        Log.d(TAG, "updateTodo: Updated todo -> " + item.getTitle() + ". Rows affected: " + rowsAffected);
        return rowsAffected > 0;
    }

    public boolean deleteTodo(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{id});
        db.close();
        Log.d(TAG, "deleteTodo: Deleted todo with id: " + id + ". Rows affected: " + rowsAffected);
        return rowsAffected > 0;
    }

    public ArrayList<TodoItem> getAllTodos() {
        ArrayList<TodoItem> todoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            int idCol = cursor.getColumnIndex(COLUMN_ID);
            int titleCol = cursor.getColumnIndex(COLUMN_TITLE);
            int descCol = cursor.getColumnIndex(COLUMN_DESCRIPTION);
            int deadlineCol = cursor.getColumnIndex(COLUMN_DEADLINE);
            int statusCol = cursor.getColumnIndex(COLUMN_STATUS);
            int isCompletedCol = cursor.getColumnIndex(COLUMN_IS_COMPLETED);
            int contactsCol = cursor.getColumnIndex(COLUMN_CONTACTS);

            Type contactListType = new TypeToken<ArrayList<Contact>>() {}.getType();

            do {
                if (idCol != -1 && titleCol != -1) {
                    String id = cursor.getString(idCol);
                    String title = cursor.getString(titleCol);
                    String desc = cursor.getString(descCol);
                    String deadline = cursor.getString(deadlineCol);
                    String status = cursor.getString(statusCol);
                    boolean isCompleted = cursor.getInt(isCompletedCol) == 1;
                    String contactsJson = cursor.getString(contactsCol);

                    ArrayList<Contact> contacts = gson.fromJson(contactsJson, contactListType);
                    if (contacts == null) contacts = new ArrayList<>();
                    
                    todoList.add(new TodoItem(id, title, desc, deadline, status, isCompleted, contacts));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getAllTodos: Loaded " + todoList.size() + " todos from DB.");
        return todoList;
    }
}
