package com.example.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.databinding.ItemTodoBinding;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private ArrayList<TodoItem> todoList;
    private Context context;

    public TodoAdapter(ArrayList<TodoItem> todoList, Context context) {
        this.todoList = todoList;
        this.context = context;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTodoBinding binding = ItemTodoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TodoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoItem item = todoList.get(position);
        holder.binding.tvTitle.setText(item.getTitle());
        holder.binding.tvDescription.setText(item.getDescription());
        holder.binding.tvDeadline.setText("Hạn: " + item.getDeadline());
        holder.binding.tvStatus.setText(item.getStatus());
        holder.binding.cbCompleted.setChecked(item.isCompleted());

        // Đánh dấu hoàn thành
        holder.binding.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {
        ItemTodoBinding binding;

        TodoViewHolder(ItemTodoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}