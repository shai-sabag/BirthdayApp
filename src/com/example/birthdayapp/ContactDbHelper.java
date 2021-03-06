package com.example.birthdayapp;

import java.util.ArrayList;
import java.util.List;

import com.example.birthdayapp.ContactEntryContract.ContactEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDbHelper extends SQLiteOpenHelper {
	private static final String SQL_CREATE_ENTRIES =
	    "CREATE TABLE " + ContactEntry.TABLE_NAME + " (" +
	    ContactEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY, " +
	    ContactEntry.COLUMN_NAME_EMAIL + " TEXT, " +
	    ContactEntry.COLUMN_NAME_NAME + " TEXT, " +
	    ContactEntry.COLUMN_NAME_BIRTH_DATE + " INTEGER, " +
	    ContactEntry.COLUMN_NAME_IMAGE + " TEXT" +
	    " )";
	
	private static final String SQL_DELETE_ENTRIES =
		    "DROP TABLE IF EXISTS " + ContactEntry.TABLE_NAME;
	
	public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Contacts.db";

    public ContactDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
	
    public long addEntry(ContactEntry entry) {
    	SQLiteDatabase db = getWritableDatabase();
    	// Create a new map of values, where column names are the keys
    	ContentValues values = new ContentValues();
    	values.put(ContactEntry.COLUMN_NAME_ENTRY_ID, entry.getEntryId());
    	values.put(ContactEntry.COLUMN_NAME_NAME, entry.getName());
    	values.put(ContactEntry.COLUMN_NAME_BIRTH_DATE, entry.getBirthDate());
    	values.put(ContactEntry.COLUMN_NAME_EMAIL, entry.getEmail());
    	if (entry.getImageFileName() != null)
    	values.put(ContactEntry.COLUMN_NAME_IMAGE, entry.getImageFileName());

    	// Insert the new row, returning the primary key value of the new row
    	return db.insert(ContactEntry.TABLE_NAME, null, values);
    }

	public void deleteEntry(long entryId) {
		SQLiteDatabase db = getWritableDatabase();
		// Define 'where' part of query.
		String selection = ContactEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
		// Specify arguments in placeholder order.
		String[] selectionArgs = { String.valueOf(entryId) };
		// Issue SQL statement.
		db.delete(ContactEntry.TABLE_NAME, selection, selectionArgs);
	}
	
	public Cursor getCursor() {
    	SQLiteDatabase db = getReadableDatabase();

    	// Define a projection that specifies which columns from the database
    	// you will actually use after this query.
    	String[] projection = {
    		ContactEntry.COLUMN_NAME_ENTRY_ID,
    	    ContactEntry.COLUMN_NAME_NAME,
    	    ContactEntry.COLUMN_NAME_BIRTH_DATE,
    	    ContactEntry.COLUMN_NAME_EMAIL,
    	    ContactEntry.COLUMN_NAME_IMAGE
    	    };

    	// How you want the results sorted in the resulting Cursor
    	String sortOrder =
    		ContactEntry.COLUMN_NAME_BIRTH_DATE + " ASC";

    	return db.query(
    		ContactEntry.TABLE_NAME,  // The table to query
    	    projection,                               // The columns to return
    	    null,                                // The columns for the WHERE clause
    	    null,                            // The values for the WHERE clause
    	    null,                                     // don't group the rows
    	    null,                                     // don't filter by row groups
    	    sortOrder                                 // The sort order
    	    );
	}
	public List<ContactEntry> getContacts() {
		Cursor cursor = getCursor();
		List<ContactEntry> contactsList = new ArrayList<ContactEntry>(cursor.getCount());
		if (!cursor.moveToFirst())
			return contactsList;
		do {
			contactsList.add(new ContactEntry(cursor));
		} while (cursor.moveToNext());
		return contactsList;
	}
}
