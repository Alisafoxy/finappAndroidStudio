package com.dtafox.finalappro.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dtafox.finalappro.R;
import com.dtafox.finalappro.models.task;
import java.util.List;
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<task> taskList;
    public interface OnTaskLongClickListener {
        void onLongClick(task taskToDelete);
    }

    private OnTaskLongClickListener onTaskLongClickListener;

    public void setOnTaskLongClickListener(OnTaskLongClickListener listener) {
        this.onTaskLongClickListener = listener;
    }

    public TaskAdapter(List<task> taskList) {
        this.taskList = taskList;
    }
    public void setTaskList(List<task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        task task = taskList.get(position);
        holder.titleTextView.setText(task.getTitle());
        holder.descriptionTextView.setText(task.getDescription());
        holder.dateTextView.setText(task.getDate());
        holder.itemView.setOnLongClickListener(v -> {
            if (onTaskLongClickListener != null) {
                onTaskLongClickListener.onLongClick(task);
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, dateTextView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTaskTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewTaskDescription);
            dateTextView = itemView.findViewById(R.id.textViewTaskDate);
        }
    }
}
