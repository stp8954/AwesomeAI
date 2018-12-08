package com.awesome.app.awesomeapp.util;

import android.database.Cursor;
import android.database.CursorWrapper;

public class EventSelectionCursorWrapper extends CursorWrapper {


    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public EventSelectionCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public EventSelection getEventSelection() {
        String labelName = getString(getColumnIndex(AppDbSchema.EventChoiceTable.Cols.LABEL_NAME));
        int isSelected        = getInt(getColumnIndex(AppDbSchema.EventChoiceTable.Cols.SELECTION));
        // TODO: Super weird bug during db upgrade is causing this column to NOT be present in db even after adding it
        // But this happens only on first run. After that, it appears to be fine.

        return new EventSelection(labelName,
                isSelected != 0);
    }
}
