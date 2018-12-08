package com.awesome.app.awesomeapp.util;

import java.util.ArrayList;
import java.util.Arrays;

public class AppDbSchema {
    public static final class EventChoiceTable {
        public static final String NAME = "eventNames";

        public static final class Cols {
            public static final String LABEL_NAME = "labelName";
            public static final String SELECTION = "selection";

            static final ArrayList<String> NAME_LIST = new ArrayList<>(Arrays.asList(
                    "_id",
                    LABEL_NAME,
                    SELECTION
            ));
        }
    }
}
