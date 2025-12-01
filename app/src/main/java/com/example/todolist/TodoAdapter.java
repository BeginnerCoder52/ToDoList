package com.example.todolist;

import android.content.Context;
import android.view.ContextMenu; // Import mới
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.databinding.ItemTodoBinding;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private final ArrayList<TodoItem> todoList;
    private final Context context;

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
        holder.binding.tvDeadline.setText(String.format("Hạn: %s", item.getDeadline()));
        holder.binding.tvStatus.setText(item.getStatus());
        holder.binding.cbCompleted.setChecked(item.isCompleted());

        holder.binding.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);
        });

        // Setup RecyclerView Contacts
        if (!item.getContacts().isEmpty()) {
            holder.binding.rvContacts.setVisibility(View.VISIBLE);
            ContactAdapter contactAdapter = new ContactAdapter(context, item.getContacts(), true); // true = Read Only
            holder.binding.rvContacts.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            );
            holder.binding.rvContacts.setAdapter(contactAdapter);
        } else {
            holder.binding.rvContacts.setVisibility(View.GONE);
        }

        // Đăng ký Context Menu cho toàn bộ item
        holder.itemView.setOnLongClickListener(v -> false); // Cần thiết để Context Menu hoạt động
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    // Implement OnCreateContextMenuListener
    public static class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ItemTodoBinding binding;

        public TodoViewHolder(ItemTodoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            // Đăng ký listener
            binding.getRoot().setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Tùy chọn công việc");
            // GroupId = AdapterPosition để biết item nào được chọn
            menu.add(this.getAdapterPosition(), 121, 0, "Sửa công việc");
            menu.add(this.getAdapterPosition(), 122, 1, "Xóa công việc");
        }
    }
}