package com.example.todolist;

import android.content.Context;
import android.view.ContextMenu; // Đã thêm import
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;       // Đã thêm import
import android.view.ViewGroup;

import androidx.annotation.NonNull; // Đã thêm import
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

        // Tránh vòng lặp sự kiện khi setChecked
        holder.binding.cbCompleted.setOnCheckedChangeListener(null);
        holder.binding.cbCompleted.setChecked(item.isCompleted());

        // Đánh dấu hoàn thành
        holder.binding.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);
            // Không gọi notifyItemChanged(position) ở đây để tránh lỗi focus,
            // chỉ cập nhật dữ liệu ngầm
        });

        // Đăng ký Context Menu cho itemView (toàn bộ thẻ card)
        holder.itemView.setOnLongClickListener(v -> {
            // Cần thiết để hiện menu ở vị trí đúng
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    // Quan trọng: implements View.OnCreateContextMenuListener để sửa lỗi dòng 54 cũ
    static class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ItemTodoBinding binding;

        TodoViewHolder(ItemTodoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            // Đăng ký menu
            binding.getRoot().setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Tùy chọn công việc");
            // groupId, itemId, order, title
            // Dùng getAdapterPosition() làm groupId để biết user nhấn vào item nào
            menu.add(this.getAdapterPosition(), 121, 0, "Sửa công việc");
            menu.add(this.getAdapterPosition(), 122, 1, "Xóa công việc");
        }
    }
}