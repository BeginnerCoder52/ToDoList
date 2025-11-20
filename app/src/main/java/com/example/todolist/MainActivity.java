package com.example.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.todolist.databinding.ActivityMainBinding;
import com.example.todolist.databinding.DialogAddTodoBinding;  // phải có dòng này
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayList<TodoItem> todoList;
    private TodoAdapter todoAdapter;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        todoList = new ArrayList<>();
        todoAdapter = new TodoAdapter(todoList, this);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(todoAdapter);

        updateDateHeader();

        binding.fabAdd.setOnClickListener(v -> showAddTodoDialog());
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

    private void showAddTodoDialog() {
        DialogAddTodoBinding dialogBinding = DialogAddTodoBinding.inflate(getLayoutInflater());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setCancelable(true)
                .create();

        // Spinner
        String[] statuses = {"Đang làm", "Hoàn thành", "Hủy", "Tạm hoãn"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, statuses);
        dialogBinding.spinnerStatus.setAdapter(adapter);

        // Chọn deadline
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

        // Lưu
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

            todoList.add(new TodoItem(title, desc, deadline, status, false));
            todoAdapter.notifyItemInserted(todoList.size() - 1);
            dialog.dismiss();
        });

        dialog.show();
    }
}