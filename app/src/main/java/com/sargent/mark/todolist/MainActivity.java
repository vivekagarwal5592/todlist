package com.sargent.mark.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sargent.mark.todolist.data.Contract;
import com.sargent.mark.todolist.data.DBHelper;

public class MainActivity extends AppCompatActivity implements AddToDoFragment.OnDialogCloseListener, UpdateToDoFragment.OnUpdateDialogCloseListener {

    private RecyclerView rv;


    private FloatingActionButton button;
    private DBHelper helper;
    private Cursor cursor;
    private SQLiteDatabase db;
    ToDoListAdapter adapter;
    private final String TAG = "mainactivity";

    //declaring the shared preference
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.d(TAG, "oncreate called in main activity");
        button = (FloatingActionButton) findViewById(R.id.addToDo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                AddToDoFragment frag = new AddToDoFragment();
                frag.show(fm, "addtodofragment");
            }
        });
        rv = (RecyclerView) findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));

        //get the shared preference file so that the values of category the user wants can be fetched
        settings = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "In on stop");
        super.onStop();
        if (db != null) db.close();
        if (cursor != null) cursor.close();
    }

    @Override
    protected void onStart() {
        super.onStart();

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();
        get_database();


        editor.putString("data", "All");
        editor.commit();

        adapter = new ToDoListAdapter(cursor, new ToDoListAdapter.ItemClickListener() {

            @Override
            public void onItemClick(int pos, String description, String duedate, long id, String category, int completed) {
                Log.d(TAG, "item click id: " + id);
                String[] dateInfo = duedate.split("-");
                int year = Integer.parseInt(dateInfo[0].replaceAll("\\s", ""));
                int month = Integer.parseInt(dateInfo[1].replaceAll("\\s", ""));
                int day = Integer.parseInt(dateInfo[2].replaceAll("\\s", ""));

                FragmentManager fm = getSupportFragmentManager();

                UpdateToDoFragment frag = UpdateToDoFragment.newInstance(year, month, day, description, id, category, completed);
                frag.show(fm, "updatetodofragment");
            }
        });

        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                Log.d(TAG, "passing id: " + id);
                removeToDo(db, id);

                get_database();
                adapter.swapCursor(cursor);
            }
        }).attachToRecyclerView(rv);
    }

    @Override
    public void closeDialog(int year, int month, int day, String description, String category, int completed) {
        addToDo(db, description, formatDate(year, month, day), category, completed);

        //calling the the common function which will decide which query needs to be called
        get_database();
        adapter.swapCursor(cursor);


    }

    public String formatDate(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }


    private Cursor getAllItems(SQLiteDatabase db) {


        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }


    //getting all the to do list for homework category
    private Cursor getItemsHomework(SQLiteDatabase db) {

        String selection = Contract.TABLE_TODO.COLUMN_NAME_CATEGORY + " = ?";
        String[] selectionArgs = {"Homework"};


        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }


    //getting all the to do list for assignment category
    private Cursor getItemsAssignment(SQLiteDatabase db) {

        String selection = Contract.TABLE_TODO.COLUMN_NAME_CATEGORY + " = ?";
        String[] selectionArgs = {"Assignment"};


        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }

    //getting all the to do list for groceries category
    private Cursor getItemsGroceries(SQLiteDatabase db) {

        String selection = Contract.TABLE_TODO.COLUMN_NAME_CATEGORY + " = ?";
        String[] selectionArgs = {"Groceries"};


        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }

    //getting all the to do list for bills category
    private Cursor getItemsBills(SQLiteDatabase db) {

        String selection = Contract.TABLE_TODO.COLUMN_NAME_CATEGORY + " = ?";
        String[] selectionArgs = {"Bills"};


        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }


    //added vew column category, completed
    private long addToDo(SQLiteDatabase db, String description, String duedate, String category, int completed) {
        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);

        //inserting vaue of category in table
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY, category);

        //inserting value of completed/not completed
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_COMPLETED, completed);

        return db.insert(Contract.TABLE_TODO.TABLE_NAME, null, cv);
    }

    private boolean removeToDo(SQLiteDatabase db, long id) {
        Log.d(TAG, "deleting id: " + id);
        return db.delete(Contract.TABLE_TODO.TABLE_NAME, Contract.TABLE_TODO._ID + "=" + id, null) > 0;
    }


    //updating the values of category and completed
    private int updateToDo(SQLiteDatabase db, int year, int month, int day, String description, long id, String category, int completed) {

        String duedate = formatDate(year, month - 1, day);

        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);

        //putting the values of category and completed inside the table
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY, category);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_COMPLETED, completed);
        return db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
    }

    //made changes in the method so that it can accept the values iof cateogry and completed
    @Override
    public void closeUpdateDialog(int year, int month, int day, String description, long id, String category, int completed) {


//updating the values of category and completed
        updateToDo(db, year, month, day, description, id, category, completed);


        //calling he common function whic h will decide which query needs to be fired
        get_database();
        adapter.swapCursor(cursor);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }

    //added the queries for various options selected from the menu. When the user clicks on the menu options, the common function will be called which wil decide which query to fire
//the value in the shared preference will be changed once the user selects the item from the menu and the common function get_database will call the query
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {


            case R.id.All:
                editor.putString("data", "All");
                editor.commit();
                get_database();
                adapter.swapCursor(cursor);
                break;


            case R.id.homework:
                editor.putString("data", "Homework");
                editor.commit();
                get_database();
                adapter.swapCursor(cursor);
                break;

            case R.id.bills:
                editor.putString("data", "Bills");
                editor.commit();
                get_database();
                adapter.swapCursor(cursor);
                break;

            case R.id.groceries:
                editor.putString("data", "Groceries");
                editor.commit();
                get_database();
                adapter.swapCursor(cursor);
                break;

            case R.id.assignment:
                editor.putString("data", "Assignment");
                editor.commit();
                get_database();
                adapter.swapCursor(cursor);
                break;


        }


        return super.onOptionsItemSelected(item);
    }


    //added the the function calls inside a common function


    public void get_database() {

        if (settings.getString("data", "All").equals("All")) {
            Log.e(TAG, settings.getString("data", "All"));
            cursor = getAllItems(db);

        } else if (settings.getString("data", "All").equals("Homework")) {
            Log.e(TAG, settings.getString("data", "All"));
            cursor = getItemsHomework(db);

        } else if (settings.getString("data", "All").equals("Assignment")) {
            Log.e(TAG, settings.getString("data", "All"));
            cursor = getItemsAssignment(db);

        } else if (settings.getString("data", "All").equals("Bills")) {
            Log.e(TAG, settings.getString("data", "All"));
            cursor = getItemsBills(db);

        } else if (settings.getString("data", "All").equals("Groceries")) {
            Log.e(TAG, settings.getString("data", "All"));
            cursor = getItemsGroceries(db);
        }



    }
}
