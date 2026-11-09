package com.example.qrtosheet

import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var urlInput: EditText
    private lateinit var continuous: CheckBox
    private lateinit var logView: TextView

    private val prefs by lazy { getSharedPreferences("qr", MODE_PRIVATE) }

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        val code = result.contents
        if (code == null) {
            log("Đã hủy quét")
        } else {
            send(code)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pad = (16 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad * 2, pad, pad)
        }

        root.addView(TextView(this).apply {
            text = "QR → Google Sheet"
            textSize = 22f
            setTypeface(null, Typeface.BOLD)
        })

        urlInput = EditText(this).apply {
            hint = "Dán URL Apps Script (…/exec)"
            inputType = InputType.TYPE_TEXT_VARIATION_URI
            setText(prefs.getString("url", ""))
        }
        root.addView(urlInput)

        continuous = CheckBox(this).apply { text = "Quét liên tục" }
        root.addView(continuous)

        root.addView(Button(this).apply {
            text = "QUÉT MÃ QR"
            setOnClickListener { startScan() }
        })

        logView = TextView(this).apply { textSize = 14f }
        root.addView(ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            )
            addView(logView)
        })

        setContentView(root)
    }

    private fun startScan() {
        val url = urlInput.text.toString().trim()
        if (!url.startsWith("https://")) {
            log("Hãy dán URL Apps Script hợp lệ (bắt đầu bằng https://)")
            return
        }
        prefs.edit().putString("url", url).apply()

        val options = ScanOptions()
            .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            .setPrompt("Đưa mã QR vào khung")
            .setBeepEnabled(true)
            .setOrientationLocked(false)
        scanLauncher.launch(options)
    }

    private fun send(code: String) {
        val url = urlInput.text.toString().trim()
        log("Đang gửi: $code")

        thread {
            val message = try {
                val body = "code=" + URLEncoder.encode(code, "UTF-8")
                val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    connectTimeout = 15000
                    readTimeout = 15000
                    setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                }
                conn.outputStream.use { it.write(body.toByteArray()) }
                // Apps Script trả 302 -> HttpURLConnection tự theo dõi chuyển hướng
                val text = conn.inputStream.bufferedReader().readText()
                conn.disconnect()
                if (text.trim() == "OK") "✔ Đã lưu: $code" else "✘ Phản hồi lạ: $text"
            } catch (e: Exception) {
                "✘ Lỗi gửi (kiểm tra mạng/URL): ${e.message}"
            }

            runOnUiThread {
                log(message)
                if (continuous.isChecked && message.startsWith("✔")) startScan()
            }
        }
    }

    private fun log(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        logView.text = "[$time] $msg\n" + logView.text
    }
}
