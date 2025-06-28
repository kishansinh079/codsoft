package com.k99.todolist;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.k99.todolist.adapters.TaskAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    RecyclerView taskRecyclerView;
    TaskAdapter taskAdapter;
    ArrayList<Task> taskList;
    FloatingActionButton fabAddTask;
    private final Calendar selectedCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        fabAddTask = findViewById(R.id.fabAddTask);

//        taskList = new ArrayList<>();
        taskList = TaskStorage.loadTasks(this);

        if(taskList.isEmpty()) {
            taskList.add(new Task("Lunch with client", "Ask Secretary", "10:30 AM", false));
            taskList.add(new Task("Check Emails", "Open PC", "01:45 PM", false));
        }
        sortTasks();
        taskAdapter = new TaskAdapter(taskList, position -> showTaskOptionsDialog(position), () -> {
            sortTasks();
            taskAdapter.notifyDataSetChanged();
        });

        taskRecyclerView.setAdapter(taskAdapter);

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setAdapter(taskAdapter);

        fabAddTask.setOnClickListener(view -> showAddTaskDialog());
    }
    private void showAddTaskDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        EditText editTitle = dialogView.findViewById(R.id.editTitle);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);
        EditText editTime = dialogView.findViewById(R.id.editTime);

        editTime.setOnClickListener(v -> {
            showDateTimePicker(MainActivity.this, selectedCalendar, formatted ->
                    editTime.setText(formatted)
            );
        });

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Add New Task")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = editTitle.getText().toString().trim();
                    String description = editDescription.getText().toString().trim();
                    String time = editTime.getText().toString().trim();

                    if (!title.isEmpty()) {
                        // Add and sort
                        taskList.add(new Task(title, description, time, false));
                        sortTasks();

                        // Notify full update (position may have changed after sort)
                        taskAdapter.notifyDataSetChanged();
                        TaskStorage.saveTasks(MainActivity.this, taskList);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showTaskOptionsDialog(int position) {
        String[] options = {"Edit", "Delete"};

        new AlertDialog.Builder(this)
                .setTitle("Task Options")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showEditTaskDialog(position);
                    } else if (which == 1) {
                        taskList.remove(position);
                        taskAdapter.notifyItemRemoved(position);
                        TaskStorage.saveTasks(MainActivity.this, taskList);
                    }
                })
                .show();
    }
    private void showEditTaskDialog(int position) {
        Task task = taskList.get(position); // Get the selected task

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        EditText editTitle = dialogView.findViewById(R.id.editTitle);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);
        EditText editTime = dialogView.findViewById(R.id.editTime);

        // Pre-fill fields with existing task data
        editTitle.setText(task.getTitle());
        editDescription.setText(task.getDescription());
        editTime.setText(task.getTime());
        editTime.setOnClickListener(v -> {
            showDateTimePicker(MainActivity.this, selectedCalendar, formatted ->
                    editTime.setText(formatted)
            );
        });
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Edit Task")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newTitle = editTitle.getText().toString().trim();
                    String newDescription = editDescription.getText().toString().trim();
                    String newTime = editTime.getText().toString().trim();

                    if (!newTitle.isEmpty()) {
                        // Update the task in the list
                        Task updatedTask = new Task(newTitle, newDescription, newTime, false);
                        taskList.set(position, updatedTask);
                        sortTasks(); // 🔁
                        taskAdapter.notifyDataSetChanged();
                        TaskStorage.saveTasks(MainActivity.this, taskList);

                        // Notify adapter and save
                        taskAdapter.notifyItemChanged(position);
                        TaskStorage.saveTasks(MainActivity.this, taskList);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void sortTasks() {
        taskList.sort((task1, task2) -> Boolean.compare(task1.isCompleted(), task2.isCompleted()));
    }
    private void showDateTimePicker(Context context, Calendar initialCalendar, Consumer<String> onDateTimeSelected) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year, month, dayOfMonth) -> {
                    initialCalendar.set(Calendar.YEAR, year);
                    initialCalendar.set(Calendar.MONTH, month);
                    initialCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                            (timeView, hourOfDay, minute) -> {
                                initialCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                initialCalendar.set(Calendar.MINUTE, minute);

                                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                                String formattedDateTime = sdf.format(initialCalendar.getTime());
                                onDateTimeSelected.accept(formattedDateTime); // callback
                            },
                            initialCalendar.get(Calendar.HOUR_OF_DAY),
                            initialCalendar.get(Calendar.MINUTE),
                            false
                    );
                    timePickerDialog.show();
                },
                initialCalendar.get(Calendar.YEAR),
                initialCalendar.get(Calendar.MONTH),
                initialCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
