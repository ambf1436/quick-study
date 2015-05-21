package com.gcw_rome_2014.quickstudy;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gcw_rome_2014.quickstudy.model.Exam;
import com.gcw_rome_2014.quickstudy.model.QuickStudy;
import com.gcw_rome_2014.quickstudy.model.difficulties.Difficulty;
import com.gcw_rome_2014.quickstudy.model.difficulties.Medium;

import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class AddNewExamActivity extends ActionBarActivity {

    private EditText examNameEditText;
    private EditText dateOfExamEditText;
    private EditText hourOfExamEditText;
    private Spinner examDifficultySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_exam);

        examNameEditText = (EditText) findViewById(R.id.examNameEditText);
        dateOfExamEditText = (EditText) findViewById(R.id.dateOfExamEditText);
        hourOfExamEditText = (EditText) findViewById(R.id.hourOfExamEditText);
        examDifficultySpinner = (Spinner) findViewById(R.id.exam_difficulty_spinner);

        this.clearAllFields();

        // To prevent opening keyboard before date/time dialog
        dateOfExamEditText.setInputType(InputType.TYPE_NULL);
        dateOfExamEditText.setFocusable(false);
        hourOfExamEditText.setInputType(InputType.TYPE_NULL);
        hourOfExamEditText.setFocusable(false);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        examDifficultySpinner.setAdapter(adapter);

        setDatePicker();
        setHourPicker();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_exam, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // Validation of fields and saving exam in new thread.
                View v = new View(getApplicationContext());
                if (!isExamValid(v)) {
                    showErrorToast(getResources().getString(R.string.fields_required));
                } else {
                    final Exam newExam = parseExam(v);
                    new Thread(new Runnable() {
                        public void run() {

                            saveNewExamEvent(newExam);
                            finish();
                        }
                    }).start();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private boolean isDateValid(View v) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy-HH:mm", Locale.getDefault());
        String dateString = dateOfExamEditText.getText().toString() + "-" + hourOfExamEditText.getText().toString();
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (Exception e) {
            // The error here would be caught in isExamValid method.
        }

        //Set calendar for an easy management of time.
        Calendar startDate = new GregorianCalendar();
        startDate.setTime(date);

        return !startDate.before(Calendar.getInstance());
    }

    private boolean isExamValid(View v) {
        String examName = examNameEditText.getText().toString();

        if (examName.isEmpty())
            return false;

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy-HH:mm", Locale.getDefault());
        String dateString = dateOfExamEditText.getText().toString() + "-" + hourOfExamEditText.getText().toString();
        try {
            dateFormat.parse(dateString);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private Exam parseExam(View v) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy-HH:mm", Locale.getDefault());
        String examName = examNameEditText.getText().toString();

        String dateString = dateOfExamEditText.getText().toString() + "-" + hourOfExamEditText.getText().toString();
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (Exception e) {
            // The error here would be caught in isExamValid method.
        }

        //Set calendar for an easy management of time.
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        String examDifficultyString = "";
        switch (examDifficultySpinner.getSelectedItemPosition()) {
            case 0:
                examDifficultyString = "Easy";
                break;
            case 1:
                examDifficultyString = "Medium";
                break;
            case 2:
                examDifficultyString = "Hard";
                break;
        }

        Difficulty difficulty;
        // Try to instantiate Difficulty command
        try {
            String className = "com.gcw_rome_2014.quickstudy.model.difficulties.";
            className += examDifficultyString;
            Constructor c = Class.forName(className).getConstructor(Context.class);
            difficulty = (Difficulty) c.newInstance(getApplicationContext());
        } catch (Exception e) {
            difficulty = new Medium(getApplicationContext());
        }

        return new Exam(examName, difficulty, calendar);
    }

    public void setDatePicker() {
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel(myCalendar);
            }
        };

        dateOfExamEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog(AddNewExamActivity.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePicker.show();
            }
        });
    }

    private void setHourPicker() {

        final Calendar myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.HOUR_OF_DAY, 9);
        myCalendar.set(Calendar.MINUTE, 0);

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                updateTimeLabel(myCalendar);
            }
        };

        hourOfExamEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewExamActivity.this, time,
                        myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), true);
                hourOfExamEditText.setInputType(InputType.TYPE_NULL);

                timePickerDialog.show();
            }
        });
    }

    private void updateTimeLabel(Calendar myCalendar) {
        String myFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALIAN);

        hourOfExamEditText.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateDateLabel(Calendar myCalendar) {

        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALIAN);

        dateOfExamEditText.setText(sdf.format(myCalendar.getTime()));
    }



    /**
     * This function is called when the save button is tapped.
     *
     * @param exam Exam to be saved.
     */
    public void saveNewExamEvent(Exam exam) {
        QuickStudy.getInstance().putExam(exam);
    }

    private void showErrorToast(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * Clear all the fields.
     */
    private void clearAllFields() {
        examNameEditText.setText("");
        dateOfExamEditText.setText("");
        hourOfExamEditText.setText("");
    }

}
