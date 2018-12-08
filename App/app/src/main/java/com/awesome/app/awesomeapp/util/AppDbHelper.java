package com.awesome.app.awesomeapp.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AppDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 7;
    private static final String DATABASE_NAME = "awesomeaistore.db";
    private HashMap<String, String> mDbAlterCommands;
    private Context mContext;

    public AppDbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);

        mContext = context;

    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB_CREATE", "Creating table " + AppDbSchema.EventChoiceTable.NAME);
        db.execSQL("create table " + AppDbSchema.EventChoiceTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        AppDbSchema.EventChoiceTable.Cols.LABEL_NAME + ", " +
                        AppDbSchema.EventChoiceTable.Cols.SELECTION +
                        ")"
                // ALERT!!! Make sure you have a comma to separate all names! Had a bug because I forgot it of ALL_DAY_SCHEDULE
        );
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DB_UPGRADE", "Updating " + AppDbSchema.EventChoiceTable.NAME + " table to version " +
                newVersion + " from version " + oldVersion);
        Cursor cursor = db.query(AppDbSchema.EventChoiceTable.NAME, null, null, null, null, null, null);
        ArrayList<String> existentColumns = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
        cursor.close();
        ArrayList<String> missingColumns = AppDbSchema.EventChoiceTable.Cols.NAME_LIST;
        missingColumns.removeAll(existentColumns);

        try {
            for (String columnName : missingColumns) {
                Log.d("DB_UPGRADE", "Adding column " + columnName + " to table using: " +
                        mDbAlterCommands.get(columnName));
                db.execSQL(mDbAlterCommands.get(columnName));
            }
        } catch (Exception e) {
            Log.e("DB_UPGRADE", "Failed to upgrade DB: " + e.getMessage());
            Toast.makeText(mContext, "App update has an issue! Please send logs to developer!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            super.onDowngrade(db, oldVersion, newVersion);
        } catch (Exception e) {
            Log.e("DB_DOWNGRADE", "Failed to downgrade DB: " + e.getMessage());
            Toast.makeText(mContext, "App update has an issue! Please send logs to developer!", Toast.LENGTH_LONG).show();
        }
    }
}
