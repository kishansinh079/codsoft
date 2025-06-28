package com.k99.todolist;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TaskStorage {

    private static final String PREFS_NAME = "task_prefs";
    private static final String TASKS_KEY = "tasks";

    public static void saveTasks(Context context, ArrayList<Task> taskList) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(taskList);

        editor.putString(TASKS_KEY, json);
        editor.apply();
    }

    public static ArrayList<Task> loadTasks(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(TASKS_KEY, null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Task>>() {}.getType();
            return gson.fromJson(json, type);
        }

        return new ArrayList<>();
    }
}
