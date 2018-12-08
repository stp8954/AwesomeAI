package com.awesome.app.awesomeapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.EventLog;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventStore {
    private static EventStore sAppSelectionsStore;

    private SQLiteDatabase mDatabase;
    private List<String> mSelectedEventNames = new ArrayList<>();

    private EventStore(Context context) {
        Context c = context.getApplicationContext();
        try {
            mDatabase = new AppDbHelper(c).getWritableDatabase();
        } catch (SQLException e) {
            Log.wtf("DB_OPEN_ERROR", "Unable to open the database for read/write: " + e.getMessage());
            Toast.makeText(c, "ERROR! Unable to store app settings because there is no free space! Please create more free space!", Toast.LENGTH_LONG).show();
        }
    }

    public static EventStore get(Context context) {
        if (sAppSelectionsStore == null) {
            sAppSelectionsStore = new EventStore(context);
        }
        return sAppSelectionsStore;
    }

    public List<String> getSelectedEvents()
    {
        List<String> selectedEvents = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                AppDbSchema.EventChoiceTable.NAME,
                null, // Columns - null selects all columns
                null,
                null,
                null, // groupBy
                null, // having
                null  // orderBy
        );
        EventSelectionCursorWrapper eCursor = new EventSelectionCursorWrapper(cursor);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                EventSelection appSelection = eCursor.getEventSelection();
                if (appSelection.isSelected()) {
                    selectedEvents.add(appSelection.getEventName());
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
       // Eventse
        return selectedEvents;
    }

    public List<String> getRegisteredEvents()
    {
        List<String> selectedEvents = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                AppDbSchema.EventChoiceTable.NAME,
                null, // Columns - null selects all columns
                null,
                null,
                null, // groupBy
                null, // having
                null  // orderBy
        );
        EventSelectionCursorWrapper eCursor = new EventSelectionCursorWrapper(cursor);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                EventSelection appSelection = eCursor.getEventSelection();
                selectedEvents.add(appSelection.getEventName());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        // Eventse
        return selectedEvents;
    }

    public void registerEvents(List<String> events)
    {
        for(int i = 0; i < events.size(); i++)
        {
            EventSelection sel = new EventSelection(events.get(i), false);
            addEventSelection(sel);
        }
    }

    public void addEventSelection(EventSelection eventSelection) throws SQLException
    {
        ContentValues values = getContentValues(eventSelection);
        mDatabase.insertOrThrow(AppDbSchema.EventChoiceTable.NAME, null, values);
    }

    public void updateEventSelection(EventSelection appSelection) {
        ContentValues values = getContentValues(appSelection);
        mDatabase.update(AppDbSchema.EventChoiceTable.NAME, values, AppDbSchema.EventChoiceTable.Cols.LABEL_NAME + " = ?", new String[]{appSelection.getEventName()});
    }

    private static ContentValues getContentValues(EventSelection eventSelection) {
        ContentValues values = new ContentValues();
        values.put(AppDbSchema.EventChoiceTable.Cols.LABEL_NAME, eventSelection.getEventName());
        values.put(AppDbSchema.EventChoiceTable.Cols.SELECTION, eventSelection.isSelected() );
        return values;
    }

}
