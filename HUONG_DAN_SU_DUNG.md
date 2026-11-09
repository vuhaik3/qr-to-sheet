# QR → Google Sheet: Hướng dẫn tạo APK và sử dụng

## PHẦN A. Chuẩn bị Google Sheet (làm 1 lần, ~3 phút)
1. Vào https://sheets.google.com, tạo một bảng tính mới.
2. Menu **Tiện ích mở rộng → Apps Script**.
3. Xóa code mẫu, mở file `google-apps-script/Code.gs`, copy toàn bộ rồi dán vào. Bấm biểu tượng lưu.
4. Bấm **Triển khai → Triển khai mới**, chọn biểu tượng bánh răng → **Ứng dụng web**:
   - Thực thi dưới tên: **Tôi**
   - Ai có quyền truy cập: **Bất kỳ ai**
5. Bấm **Triển khai**, cấp quyền khi Google hỏi (Nâng cao → Đi tới dự án → Cho phép).
6. **Copy "URL ứng dụng web"** (kết thúc bằng `/exec`). Giữ lại để dán vào app.

## PHẦN B. Tạo file APK (cách 1: GitHub, không cần cài gì)
1. Tạo tài khoản miễn phí tại https://github.com (nếu chưa có).
2. Tạo repository mới (Private cũng được), ví dụ `qr-to-sheet`.
3. Giải nén file zip dự án, kéo thả **toàn bộ nội dung bên trong** vào trang repository
   (nút **Add file → Upload files**). Nhớ tải lên cả thư mục ẩn `.github`; nếu giao diện web
   không nhận thư mục ẩn, dùng GitHub Desktop hoặc lệnh `git push`.
4. Vào tab **Actions → Build APK → Run workflow**. Chờ khoảng 3–6 phút đến khi có dấu ✔ xanh.
5. Bấm vào lần chạy vừa xong, kéo xuống mục **Artifacts**, tải **QR-to-Sheet-APK** (file zip chứa `app-debug.apk`).

### Cách 2: dùng Android Studio
1. Cài Android Studio, tạo project **Empty Views Activity**, ngôn ngữ Kotlin,
   package `com.example.qrtosheet`, Minimum SDK API 24.
2. Chép `MainActivity.kt` và `AndroidManifest.xml` đè lên file tương ứng.
3. Thêm vào `dependencies` của `app/build.gradle`:
   `implementation("com.journeyapps:zxing-android-embedded:4.3.0")` và
   `implementation("androidx.appcompat:appcompat:1.6.1")`, rồi **Sync**.
4. **Build → Build APK(s)**. File nằm ở `app/build/outputs/apk/debug/app-debug.apk`.

## PHẦN C. Cài APK lên điện thoại Android
1. Chép `app-debug.apk` sang điện thoại (cáp USB, Zalo, Google Drive, email…).
2. Mở file bằng ứng dụng **Tệp/Files**, bấm cài đặt.
3. Nếu máy báo chặn: vào **Cài đặt → Cho phép cài ứng dụng từ nguồn này** rồi cài lại.
4. Nếu Google Play Protect cảnh báo, chọn **Vẫn cài đặt** (app tự build nên chưa được Google xác minh).

## PHẦN D. Sử dụng
1. Mở **QR to Sheet**, dán URL Apps Script (Phần A, bước 6). App nhớ URL cho các lần sau.
2. Bấm **QUÉT MÃ QR**, cho phép quyền camera lần đầu.
3. Hướng camera vào mã. Khi quét được, điện thoại kêu bíp và app tự gửi dữ liệu.
4. Dòng nhật ký hiện **✔ Đã lưu: …** là thành công. Mở Google Sheet sẽ thấy:

| Ngày giờ | Mã QR |
|---|---|
| 09/11/2026 14:32:10 | nội dung mã |

5. Tick **Quét liên tục** để quét nhiều mã liên tiếp mà không phải bấm lại.

## Xử lý sự cố
- **✘ Lỗi gửi**: kiểm tra Wi-Fi/4G và URL đã dán đúng, kết thúc bằng `/exec`.
- **✘ Phản hồi lạ**: quyền truy cập chưa đặt là "Bất kỳ ai", hoặc sau khi sửa Code.gs bạn chưa
  tạo **phiên bản triển khai mới**.
- **Camera không mở**: vào Cài đặt → Ứng dụng → QR to Sheet → Quyền → bật Camera.
- **Dữ liệu vào sai sheet**: script ghi vào trang tính đầu tiên của file chứa script đó.
