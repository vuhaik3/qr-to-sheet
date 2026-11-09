// Dán vào Google Sheet: Tiện ích mở rộng > Apps Script
// Sau đó: Triển khai > Triển khai mới > Ứng dụng web
//   - Thực thi dưới tên: Tôi
//   - Ai có quyền truy cập: Bất kỳ ai
// Copy URL ứng dụng web (kết thúc bằng /exec) dán vào app Android.

function doPost(e) {
  var sheet = SpreadsheetApp.getActiveSpreadsheet().getSheets()[0];
  var code = (e && e.parameter && e.parameter.code) ? e.parameter.code : '';

  if (!code) {
    return ContentService.createTextOutput('EMPTY');
  }

  if (sheet.getLastRow() === 0) {
    sheet.appendRow(['Ngày giờ', 'Mã QR']);
    sheet.getRange(1, 1, 1, 2).setFontWeight('bold');
  }

  var now = Utilities.formatDate(new Date(), 'Asia/Ho_Chi_Minh', 'dd/MM/yyyy HH:mm:ss');
  var row = sheet.getLastRow() + 1;

  // Ghi dạng text để tránh Sheets tự đổi số/công thức
  sheet.getRange(row, 1, 1, 2).setNumberFormat('@').setValues([[now, code]]);

  return ContentService.createTextOutput('OK');
}

function doGet() {
  return ContentService.createTextOutput('QR to Sheet đang hoạt động');
}
