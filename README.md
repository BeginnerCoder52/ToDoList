# ToDoList - Ứng dụng Quản lý Công Việc

Ứng dụng ToDoList đơn giản, đẹp mắt, hiện đại được thiết kế theo phong cách Material Design với tông màu xanh dương chủ đạo.
Hỗ trợ thêm, hiển thị, đánh dấu hoàn thành công việc kèm deadline, trạng thái và quản lý người liên quan.

## Tính năng chính
- Thêm công việc mới với tiêu đề, mô tả, deadline (ngày + giờ).
- Chọn trạng thái: Đang làm / Hoàn thành / Hủy / Tạm hoãn.
- Giao diện card bo tròn, icon dễ thương, hiệu ứng bóng đổ.
- Hiển thị ngày hiện tại lớn ở đầu màn hình.
- FAB nổi bật để thêm task nhanh.
- Đánh dấu hoàn thành bằng checkbox.
- **Tương thích hoàn toàn với Android mới nhất.**

---

## 📅 Lịch sử Cập nhật (Changelog)

### Update bài tập 2/12 (Mới nhất)
**Tính năng: Tích hợp Cơ sở dữ liệu SQLite (Data Persistence)**
- **Lưu trữ bền vững:**
    - Thay thế lưu trữ tạm thời (ArrayList trên RAM) bằng **SQLite Database**.
    - Dữ liệu công việc được bảo toàn ngay cả khi đóng ứng dụng hoặc khởi động lại thiết bị.
- **Kiến trúc & Xử lý dữ liệu:**
    - Xây dựng lớp **`TodoRepo`** (kế thừa `SQLiteOpenHelper`) để quản lý kết nối và thực hiện các thao tác CRUD (Create, Read, Update, Delete).
    - Sử dụng thư viện **Gson** để serialize danh sách người liên quan (`ArrayList<Contact>`) thành chuỗi JSON để lưu trữ vào cột văn bản trong database.
- **Tối ưu hóa:**
    - Tự động tải dữ liệu từ DB khi khởi động ứng dụng.
    - Đồng bộ trạng thái hoàn thành/xóa ngay lập tức xuống DB.

### Update bài tập 25/11
**Tính năng: Tích hợp Danh bạ & Nested RecyclerView**
- **Quản lý người liên quan (Contacts):**
    - Tích hợp xin quyền truy cập danh bạ (`READ_CONTACTS`) tại thời điểm chạy (Runtime Permission).
    - Sử dụng `Intent` hệ thống để chọn người từ danh bạ điện thoại.
    - Lấy thông tin Tên và Số điện thoại thông qua `ContentResolver`.
- **Giao diện & Trải nghiệm (UI/UX):**
    - **Nested RecyclerView:** Hiển thị danh sách Contact (ngang) nằm bên trong mỗi thẻ công việc (dọc).
    - **Badge UI:** Hiển thị Contact dưới dạng hình tròn (Avatar/Placeholder).
    - **Popup chi tiết:** Nhấn vào avatar để xem Số điện thoại và gọi nhanh.
    - **Redesign:** Chuyển nền Card sang màu trắng, tối ưu màu chữ (Đen/Xám) để dễ đọc hơn.

### Update bài tập 18/11
**Tính năng: Menu điều khiển & Thao tác hàng loạt**
- **Option Menu (Góc trên phải):**
    - Thêm mới công việc (`New`).
    - Chọn tất cả công việc (`Select All`).
    - Xóa các công việc đã chọn (`Delete Selected`).
- **Context Menu (Nhấn giữ item):**
    - Hỗ trợ sự kiện Long Click vào một công việc bất kỳ.
    - Chức năng **Sửa (Edit):** Mở lại dialog với thông tin cũ để cập nhật.
    - Chức năng **Xóa (Delete):** Xóa nhanh một công việc cụ thể.

---

## Công nghệ sử dụng
- **Ngôn ngữ:** Java
- **Kiến trúc:** View Binding, Repository Pattern
- **Lưu trữ (Database):** SQLite, Gson (JSON Serialization)
- **UI Components:**
    - RecyclerView (Nested: Horizontal in Vertical)
    - CardView, CoordinatorLayout, FloatingActionButton
    - Material Design Input Field & Button
- **System Integration:**
    - Explicit & Implicit Intents (Contacts)
    - Content Provider (ContactsContract)
    - Permissions Handling
- **Utilities:** DatePickerDialog & TimePickerDialog, Picasso (Load ảnh)

## Tác giả
**Nguyễn Lê Thanh Hiển**
* MSSV: 22520418
* Lớp: SE114.Q11 – Nhập môn Ứng dụng Di động
* Link source code: https://github.com/BeginnerCoder52/ToDoList
