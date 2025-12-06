package com.example.todolist;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todolist.databinding.ActivityMainBinding;
import com.example.todolist.databinding.DialogAddTodoBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayList<TodoItem> todoList;
    private TodoAdapter todoAdapter;
    private TodoRepo todoRepo; // Thêm TodoRepo

    private ArrayList<Contact> tempSelectedContacts = new ArrayList<>();
    private ContactAdapter tempContactAdapter;

    private static final int REQUEST_CODE_PICK_CONTACT = 1001;
    private static final int REQUEST_PERMISSION_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        checkPermission();

        // --- TÍCH HỢP SQLITE ---
        todoRepo = new TodoRepo(this);
        loadDataFromDatabase(); // Tải dữ liệu từ DB
        // --- KẾT THÚC TÍCH HỢP ---

        todoAdapter = new TodoAdapter(todoList, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(todoAdapter);

        updateDateHeader();

        binding.fabAdd.setOnClickListener(v -> showTodoDialog(null, -1));
    }

    // --- Tải dữ liệu từ cơ sở dữ liệu ---
    private void loadDataFromDatabase() {
        todoList = todoRepo.getAllTodos();
        // Nếu DB trống, có thể thêm dữ liệu mẫu
        if (todoList.isEmpty()) {
            TodoItem sampleItem = new TodoItem("Bài tập Contact", "Test chức năng thêm người", "30/11/2025 20:00", "Đang làm", false, new ArrayList<>());
            todoRepo.addTodo(sampleItem);
            todoList.add(sampleItem);
        }
    }

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

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = item.getGroupId();
        if (position < 0 || position >= todoList.size()) return super.onContextItemSelected(item);

        TodoItem selectedItem = todoList.get(position);

        switch (item.getItemId()) {
            case 121: // Edit
                showTodoDialog(selectedItem, position);
                return true;
            case 122: // Delete
                deleteTodoItem(position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showTodoDialog(TodoItem itemToEdit, int position) {
        DialogAddTodoBinding dialogBinding = DialogAddTodoBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setCancelable(true)
                .create();

        tempSelectedContacts.clear();

        if (itemToEdit != null) {
            tempSelectedContacts.addAll(itemToEdit.getContacts());
            dialogBinding.tvHeaderTitle.setText("CẬP NHẬT CÔNG VIỆC");
            dialogBinding.etTitle.setText(itemToEdit.getTitle());
            dialogBinding.etDescription.setText(itemToEdit.getDescription());
            dialogBinding.tvDeadline.setText(itemToEdit.getDeadline());
            dialogBinding.btnSave.setText("LƯU THAY ĐỔI");
        }

        tempContactAdapter = new ContactAdapter(this, tempSelectedContacts, false);
        dialogBinding.rvSelectedContacts.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dialogBinding.rvSelectedContacts.setAdapter(tempContactAdapter);

        dialogBinding.btnAddContact.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_PICK_CONTACT);
            } else {
                Toast.makeText(this, "Cần cấp quyền danh bạ", Toast.LENGTH_SHORT).show();
                checkPermission();
            }
        });

        String[] statuses = {"Đang làm", "Hoàn thành", "Hủy", "Tạm hoãn"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses);
        dialogBinding.spinnerStatus.setAdapter(adapter);

        if (itemToEdit != null) {
            for (int i = 0; i < statuses.length; i++) {
                if (statuses[i].equals(itemToEdit.getStatus())) {
                    dialogBinding.spinnerStatus.setSelection(i);
                    break;
                }
            }
        }

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

        dialogBinding.btnSave.setOnClickListener(v -> {
            String title = dialogBinding.etTitle.getText().toString().trim();
            if (title.isEmpty()) return;
            String desc = dialogBinding.etDescription.getText().toString().trim();
            String deadline = dialogBinding.tvDeadline.getText().toString();
            if (deadline.equals("Chọn deadline")) deadline = "Không đặt hạn";
            String status = dialogBinding.spinnerStatus.getSelectedItem().toString();

            ArrayList<Contact> finalContacts = new ArrayList<>(tempSelectedContacts);

            if (itemToEdit == null) {
                // Thêm mới vào DB
                TodoItem newItem = new TodoItem(title, desc, deadline, status, false, finalContacts);
                todoRepo.addTodo(newItem);
                todoList.add(newItem);
                todoAdapter.notifyItemInserted(todoList.size() - 1);
            } else {
                // Cập nhật trong DB
                TodoItem updatedItem = new TodoItem(itemToEdit.getId(), title, desc, deadline, status, itemToEdit.isCompleted(), finalContacts);
                todoRepo.updateTodo(updatedItem);
                todoList.set(position, updatedItem);
                todoAdapter.notifyItemChanged(position);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CONTACT && resultCode == RESULT_OK && data != null) {
            Uri contactUri = data.getData();
            if (contactUri == null) return;
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                try {
                    int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                    int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    int photoIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
                    int hasPhoneIndex = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);

                    if (idIndex != -1 && nameIndex != -1) {
                        String id = cursor.getString(idIndex);
                        String name = cursor.getString(nameIndex);
                        String photoUri = (photoIndex != -1) ? cursor.getString(photoIndex) : null;
                        String phone = "Không sđt";

                        if (hasPhoneIndex != -1 && Integer.parseInt(cursor.getString(hasPhoneIndex)) > 0) {
                            Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            if (pCur != null) {
                                if (pCur.moveToFirst()) {
                                    int numIndex = pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                    if (numIndex != -1) phone = pCur.getString(numIndex);
                                }
                                pCur.close();
                            }
                        }
                        tempSelectedContacts.add(new Contact(name, phone, photoUri));
                        if (tempContactAdapter != null) tempContactAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        }
    }

    private void selectAllTasks() {
        for (TodoItem item : todoList) {
            item.setCompleted(true);
            todoRepo.updateTodo(item); // Cập nhật trạng thái trong DB
        }
        todoAdapter.notifyDataSetChanged();
    }

    private void deleteSelectedTasks() {
        Iterator<TodoItem> iterator = todoList.iterator();
        while (iterator.hasNext()) {
            TodoItem item = iterator.next();
            if (item.isCompleted()) {
                todoRepo.deleteTodo(item.getId()); // Xóa khỏi DB
                iterator.remove();
            }
        }
        todoAdapter.notifyDataSetChanged();
    }

    private void deleteTodoItem(int position) {
        TodoItem item = todoList.get(position);
        todoRepo.deleteTodo(item.getId()); // Xóa khỏi DB
        todoList.remove(position);
        todoAdapter.notifyItemRemoved(position);
        todoAdapter.notifyItemRangeChanged(position, todoList.size());
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSION_READ_CONTACTS);
        }
    }

    private void updateDateHeader() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());
        String dayStr = dayFormat.format(new Date());
        int day = Integer.parseInt(dayStr);
        String suffix = (day % 10 == 1 && day != 11) ? "st" : (day % 10 == 2 && day != 12) ? "nd" : (day % 10 == 3 && day != 13) ? "rd" : "th";
        binding.tvDate.setText(String.format("%s %s%s", monthFormat.format(new Date()), day, suffix));
    }
}
