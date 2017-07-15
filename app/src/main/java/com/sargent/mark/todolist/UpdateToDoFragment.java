package com.sargent.mark.todolist;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by mark on 7/5/17.
 */

public class UpdateToDoFragment extends DialogFragment {

    private EditText toDo;
    private DatePicker dp;
    private Button add;
    private final String TAG = "updatetodofragment";
    private long id;

    //declaring new fields
    private Spinner spinner;
    private CheckBox completed_task;
    private int completed;

    public UpdateToDoFragment() {
    }

    public static UpdateToDoFragment newInstance(int year, int month, int day, String descrpition, long id, String category, int completed) {
        UpdateToDoFragment f = new UpdateToDoFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        args.putLong("id", id);
        args.putString("description", descrpition);

        //added to new fields in bundle
        args.putString("category", category);
        args.putInt("completed", completed);

        f.setArguments(args);

        return f;
    }

    //To have a way for the activity to get the data from the dialog
    public interface OnUpdateDialogCloseListener {

        //updated the method so that it can accept calues of category and task completed/not completec
        void closeUpdateDialog(int year, int month, int day, String description, long id, String category, int completed);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_adder, container, false);
        toDo = (EditText) view.findViewById(R.id.toDo);
        dp = (DatePicker) view.findViewById(R.id.datePicker);
        add = (Button) view.findViewById(R.id.add);

      //  parsing the  element from XML
        completed_task = (CheckBox) view.findViewById(R.id.completed);
        int year = getArguments().getInt("year");
        int month = getArguments().getInt("month");
        int day = getArguments().getInt("day");
        id = getArguments().getLong("id");
        String description = getArguments().getString("description");


        //getting two new fields from the bundle
        String category_name = getArguments().getString("category");
        completed = getArguments().getInt("completed");

        dp.updateDate(year, month, day);

        toDo.setText(description);

        //initialized the spinner
        spinner = (Spinner) view.findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

//set the adapter to the spinner
        switch (category_name) {
            case "Homework":
                spinner.setSelection(0);
                break;

            case "Groceries":
                spinner.setSelection(1);
                break;

            case "Bills":
                spinner.setSelection(2);
                break;

            case "Assignment":
                spinner.setSelection(3);
                break;
        }

        //setting the value of checkbox as stored by the user
        if (completed == 1) {
            completed_task.setChecked(true);
        }

        completed_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (completed_task.isChecked()) {
                    completed = 1;
                } else {
                    completed = 0;
                }
            }
        });


        add.setText("Update");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateToDoFragment.OnUpdateDialogCloseListener activity = (UpdateToDoFragment.OnUpdateDialogCloseListener) getActivity();
                Log.d(TAG, "checked: " + completed);

                //sending the updated values to the main activity
                activity.closeUpdateDialog(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), toDo.getText().toString(), id, spinner.getSelectedItem().toString(), completed);

                UpdateToDoFragment.this.dismiss();
            }
        });

        return view;
    }
}