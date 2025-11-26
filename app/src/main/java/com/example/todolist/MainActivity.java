package com.example.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;     // Import Menu
import android.view.MenuItem; // Import MenuItem
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull; // Import NonNull
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todolist.databinding.ActivityMainBinding;
import com.example.todolist.databinding.DialogAddTodoBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayList<TodoItem> todoList;
    private TodoAdapter todoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set Toolbar để hiện Option Menu (nếu bạn dùng theme NoActionBar)
        setSupportActionBar(binding.toolbar); // Bạn cần thêm Toolbar vào XML nếu chưa có, hoặc dùng default Actionbar

        todoList = new ArrayList<>();
        // Dữ liệu mẫu
        todoList.add(new TodoItem("Làm bài tập Android", "Hoàn thành context menu", "28/11/2025 10:00", "Đang làm", false));

        todoAdapter = new TodoAdapter(todoList, this);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(todoAdapter);

        updateDateHeader();

        // Nút FAB thêm mới
        binding.fabAdd.setOnClickListener(v -> showTodoDialog(null, -1));
    }

    // --- 1. XỬ LÝ OPTION MENU (Góc trên phải) ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new) {
            showTodoDialog(null, -1);
            return true;
        } else if (id == R.id.action_select_all) {
            selectAllTasks();
            return true;
        } else if (id == R.id.action_delete_selected) {
            deleteSelectedTasks();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- 2. XỬ LÝ CONTEXT MENU (Nhấn giữ item) ---
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // Lấy vị trí item từ GroupId mà ta đã gán trong Adapter
        int position = item.getGroupId();

        // Kiểm tra vị trí hợp lệ
        if (position < 0 || position >= todoList.size()) return super.onContextItemSelected(item);

        TodoItem selectedItem = todoList.get(position);

        switch (item.getItemId()) {
            case 121: // Sửa (ID 121 định nghĩa bên Adapter)
                showTodoDialog(selectedItem, position);
                return true;
            case 122: // Xóa (ID 122 định nghĩa bên Adapter)
                deleteTodoItem(position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // --- CÁC HÀM XỬ LÝ LOGIC ---

    private void selectAllTasks() {
        for (TodoItem item : todoList) {
            item.setCompleted(true);
        }
        todoAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Đã chọn tất cả", Toast.LENGTH_SHORT).show();
    }

    private void deleteSelectedTasks() {
        // Sử dụng Iterator để xóa an toàn trong khi duyệt danh sách
        java.util.Iterator<TodoItem> iterator = todoList.iterator();
        while (iterator.hasNext()) {
            TodoItem item = iterator.next();
            if (item.isCompleted()) {
                iterator.remove();
            }
        }
        todoAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Đã xóa các mục hoàn thành", Toast.LENGTH_SHORT).show();
    }

    private void deleteTodoItem(int position) {
        todoList.remove(position);
        todoAdapter.notifyItemRemoved(position);
        todoAdapter.notifyItemRangeChanged(position, todoList.size()); // Cập nhật lại index
        Toast.makeText(this, "Đã xóa công việc", Toast.LENGTH_SHORT).show();
    }

    // Hàm Dialog dùng chung cho cả Thêm và Sửa
    private void showTodoDialog(TodoItem itemToEdit, int position) {
        DialogAddTodoBinding dialogBinding = DialogAddTodoBinding.inflate(getLayoutInflater());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setCancelable(true)
                .create();

        // Setup Spinner
        String[] statuses = {"Đang làm", "Hoàn thành", "Hủy", "Tạm hoãn"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, statuses);
        dialogBinding.spinnerStatus.setAdapter(adapter);

        // Setup Deadline Picker
        dialogBinding.tvDeadline.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                cal.set(year, month, day);
                new TimePickerDialog(this, (tview, hour, minute) -> {
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    dialogBinding.tvDeadline.setText(sdf.format(cal.getTime()));
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        // == CHẾ ĐỘ SỬA (EDIT MODE) ==
        if (itemToEdit != null) {
            dialogBinding.etTitle.setText(itemToEdit.getTitle());
            dialogBinding.etDescription.setText(itemToEdit.getDescription());
            dialogBinding.tvDeadline.setText(itemToEdit.getDeadline());

            // Set spinner selection
            for(int i=0; i < statuses.length; i++) {
                if(statuses[i].equals(itemToEdit.getStatus())) {
                    dialogBinding.spinnerStatus.setSelection(i);
                    break;
                }
            }
            dialogBinding.btnSave.setText("CẬP NHẬT"); // Đổi text nút
        }

        // Sự kiện Lưu
        dialogBinding.btnSave.setOnClickListener(v -> {
            String title = dialogBinding.etTitle.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(this, "Nhập tiêu đề đi bạn ơi!", Toast.LENGTH_SHORT).show();
                return;
            }
            String desc = dialogBinding.etDescription.getText().toString().trim();
            String deadline = dialogBinding.tvDeadline.getText().toString();
            if (deadline.equals("Chọn deadline")) deadline = "Không đặt hạn";
            String status = dialogBinding.spinnerStatus.getSelectedItem().toString();

            if (itemToEdit == null) {
                // Thêm mới
                todoList.add(new TodoItem(title, desc, deadline, status, false));
                todoAdapter.notifyItemInserted(todoList.size() - 1);
            } else {
                // Cập nhật
                // Vì TodoItem fields là private và không có Setter (trừ setCompleted),
                // bạn cần thêm Setter trong class TodoItem hoặc tạo object mới
                // Cách tạo mới thay thế object cũ:
                TodoItem newItem = new TodoItem(title, desc, deadline, status, itemToEdit.isCompleted());
                todoList.set(position, newItem);
                todoAdapter.notifyItemChanged(position);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateDateHeader() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());
        String dayStr = dayFormat.format(new Date());
        int day = Integer.parseInt(dayStr);
        String suffix = (day % 10 == 1 && day != 11) ? "st" :
                (day % 10 == 2 && day != 12) ? "nd" :
                        (day % 10 == 3 && day != 13) ? "rd" : "th";
        binding.tvDate.setText(monthFormat.format(new Date()) + " " + day + suffix);
    }
}