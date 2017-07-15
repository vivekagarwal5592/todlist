package com.sargent.mark.todolist;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

/**
 * Created by mark on 7/4/17.
 */

public class AddToDoFragment extends DialogFragment {

    private EditText toDo;
    private DatePicker dp;
    private Button add;

    //added checkbox for user to mark a task as done or not done
    private CheckBox completed;
    private int completed_value;

    //declared the spinner
    private Spinner spinner;

    private final String TAG = "addtodofragment";

    public AddToDoFragment() {
        completed_value =0;
    }



    //To have a way for the activity to get the data from the dialog

    //added another field of cateogry
    public interface OnDialogCloseListener {
        void closeDialog(int year, int month, int day, String description,String category,int completed);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_adder, container, false);
        toDo = (EditText) view.findViewById(R.id.toDo);
        dp = (DatePicker) view.findViewById(R.id.datePicker);
        add = (Button) view.findViewById(R.id.add);

        //initialized the checkbox
        completed = (CheckBox)view.findViewById(R.id.completed);
        //initialized the spinner
        spinner = (Spinner) view.findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
//set the adapter to the spinner


        //the value of an integer will be changed when the user clicks on the on the checkbox. This vakue wil get stored in the database
        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(completed.isChecked()){
                    completed_value = 1;
                }
                else{
                    completed_value = 0;
                }
            }
        });


        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        dp.updateDate(year, month, day);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnDialogCloseListener activity = (OnDialogCloseListener) getActivity();

                //sending all the values of the todolist to the mehtod in the main activity
                activity.closeDialog(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), toDo.getText().toString(),spinner.getSelectedItem().toString(),completed_value);
                AddToDoFragment.this.dismiss();
            }
        });

        return view;
    }
}



