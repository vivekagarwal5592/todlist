package com.sargent.mark.todolist.data;

import android.provider.BaseColumns;

/**
 * Created by mark on 7/4/17.
 */

public class Contract {

    public static class TABLE_TODO implements BaseColumns{
        public static final String TABLE_NAME = "todoitems";

        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DUE_DATE = "duedate";


        //added new column name category
        public static final String COLUMN_NAME_CATEGORY = "category";

        //added done/not done column
        public static final String COLUMN_NAME_COMPLETED = "COMPLETED";
    }
}
