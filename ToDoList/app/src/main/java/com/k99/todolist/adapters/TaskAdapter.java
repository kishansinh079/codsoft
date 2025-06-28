package com.k99.todolist.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.k99.todolist.R;
import com.k99.todolist.Task;
import com.k99.todolist.TaskStorage;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<Task> taskList;
    private OnTaskClickListener listener;
    private Runnable sortTasksExternally;

    public interface OnTaskClickListener {
        void onTaskLongClick(int position);
    }

//    public TaskAdapter(ArrayList<Task> taskList, OnTaskClickListener listener) {
//        this.taskList = taskList;
//        this.listener = listener;
//    }
    public TaskAdapter(ArrayList<Task> taskList, OnTaskClickListener listener, Runnable sortTasksExternally) {
        this.taskList = taskList;
        this.listener = listener;
        this.sortTasksExternally = sortTasksExternally;
    }
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, time;
        CheckBox checkCompleted;
        public TaskViewHolder(@NonNull View itemView, OnTaskClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.taskTitle);
            description = itemView.findViewById(R.id.taskDescription);
            time = itemView.findViewById(R.id.taskTime);
            checkCompleted = itemView.findViewById(R.id.checkCompleted);


            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onTaskLongClick(getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.title.setText(task.getTitle());
        holder.description.setText(task.getDescription());
        holder.time.setText(task.getTime());
//        holder.checkCompleted.setChecked(task.isCompleted());

        // Strike-through if completed
        holder.title.setPaintFlags(task.isCompleted() ?
                holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG :
                holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        // Checkbox listener
//        holder.checkCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            task.setCompleted(isChecked);
//            notifyItemChanged(position);
//            TaskStorage.saveTasks(holder.itemView.getContext(), taskList);
//        });
        holder.checkCompleted.setOnCheckedChangeListener(null);
        holder.checkCompleted.setChecked(task.isCompleted());

        holder.checkCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            TaskStorage.saveTasks(holder.itemView.getContext(), taskList);
            if (sortTasksExternally != null) {
                sortTasksExternally.run(); // or sortTasks + notifyDataSetChanged()
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
