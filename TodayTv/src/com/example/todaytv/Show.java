package com.example.todaytv;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Saves user information about a show.
 *
 */
public class Show {
        
        long id;
        String title;
        int rating;
        
        private Show() {}
        
        public Show(final String title, final int rating ) {
                this.title = title;
                this.rating = rating;
        }
        
        public void save(DatabaseHelper dbHelper) {
                final ContentValues values = new ContentValues();
                values.put(TITLE, this.title);
                values.put(RATING, this.rating);
                
                final SQLiteDatabase db = dbHelper.getReadableDatabase();
                this.id = db.insert(SHOWS_TABLE_NAME, null, values);
                db.close();
        }
        
        public int delete(DatabaseHelper dbHelper, String title){
        	final SQLiteDatabase db = dbHelper.getWritableDatabase();
        	//return db.delete(SHOWS_TABLE_NAME, ID + "=" + rowId, null) > 0;
        	return db.delete(SHOWS_TABLE_NAME, TITLE + "=?", new String[] { title });
        }
        
        public static Show[] getAll(final DatabaseHelper dbHelper) {
                 final List<Show> shows = new ArrayList<Show>();
                 final SQLiteDatabase db = dbHelper.getWritableDatabase();
                 final Cursor c = db.query(SHOWS_TABLE_NAME,
                                 new String[] { ID, TITLE, RATING}, null, null, null, null, null);
                 // make sure you start from the first item
                 c.moveToFirst();
                 while (!c.isAfterLast()) {
                         final Show show = cursorToShow(c);
                     shows.add(show);
                     c.moveToNext();
                 }
                 // Make sure to close the cursor
                 c.close();
                 return shows.toArray(new Show[shows.size()]);
        }
        
        
        public static String[] getAllNames(final DatabaseHelper dbHelper){
        	final List<String> showNames = new ArrayList<String>();
            final SQLiteDatabase db = dbHelper.getWritableDatabase();
            final Cursor c = db.query(SHOWS_TABLE_NAME,
                            new String[] { ID, TITLE, RATING}, null, null, null, null, null);
            // make sure you start from the first item
            c.moveToFirst();
            while (!c.isAfterLast()) {
                    final Show show = cursorToShow(c);
                    showNames.add(show.getTitle());
                c.moveToNext();
            }
            // Make sure to close the cursor
            c.close();
            return showNames.toArray(new String[showNames.size()]);
        }
        
        public static Show cursorToShow(Cursor c) {
                final Show show = new Show();
                show.setTitle(c.getString(c.getColumnIndex(TITLE)));
                show.setRating(c.getInt(c.getColumnIndex(RATING)));
                return show;
        }

        
        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public int getRating() {
                return rating;
        }

        public void setRating(int rating) {
                this.rating = rating;
        }
        

        public static final String SHOWS_TABLE_NAME = "shows";
        // column names
        static final String ID = "id"; // 
        static final String TITLE = "title";
        static final String RATING = "rating";
        // SQL statement to create our table
        public static final String SHOWS_CREATE_TABLE = "CREATE TABLE " + Show.SHOWS_TABLE_NAME + " ("
                                                        + Show.ID + " INTEGER PRIMARY KEY,"
                                                        + Show.TITLE + " TEXT,"
                                                        + Show.RATING + " INTEGER"
                                                        + ");";

        
}