# Quản lý chi tiêu cá nhân - Android Java MVVM

Ứng dụng Android native viết bằng Java, dùng kiến trúc MVVM để quản lý thu nhập, chi tiêu, danh mục, thống kê và ngân sách cá nhân.

## Công nghệ

- Java
- Android Studio / Gradle
- MVVM
- Room Database
- LiveData / ViewModel
- Material Components
- RecyclerView
- MPAndroidChart
- SharedPreferences cho ngân sách tháng

## Kiến trúc

Luồng dữ liệu chính:

```text
UI -> ViewModel -> Repository -> DAO -> Room Database
```

UI chỉ quan sát `LiveData` và gửi action lên `ViewModel`. Repository là lớp trung gian duy nhất gọi DAO.

## Chức năng đã scaffold

- Home: số dư, tổng thu, tổng chi, cảnh báo ngân sách, giao dịch gần đây.
- Thêm/sửa giao dịch: số tiền, loại, danh mục, ngày, ghi chú, validate form.
- Danh sách giao dịch: lọc tháng hiện tại theo tất cả/thu/chi, sửa bằng tap, xóa bằng long press.
- Danh mục: thêm danh mục, xem danh mục, chặn xóa danh mục đang được dùng.
- Thống kê: tổng thu/chi/số dư và Pie Chart chi tiêu theo danh mục.
- Ngân sách: lưu ngân sách tháng và bật/tắt cảnh báo bằng SharedPreferences.

## Linear

Project Linear đích:

https://linear.app/tunapp/team/TEA/projects/view/quan-ly-chi-tieu-android-studio-50945fb657a7

Đã tạo EPIC và task con từ `TEA-1` đến `TEA-87`.

## Cách chạy

1. Mở thư mục này bằng Android Studio.
2. Đợi Gradle sync dependency.
3. Chạy app module `app` trên emulator hoặc thiết bị Android.

Lưu ý: môi trường terminal hiện tại chưa có Java runtime, nên cần cài JDK hoặc dùng JDK đi kèm Android Studio để build.
