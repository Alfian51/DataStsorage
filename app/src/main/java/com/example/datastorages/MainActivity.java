package com.example.datastorages;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    // SharedPreferences Constants & Variables
    private SharedPreferences mPreferences;
    private final String sharedPrefFile = "com.example.datastorages.prefs";
    private final String KEY_COUNT = "count";
    private final String KEY_COLOR = "color";

    private int mCount = 0;
    private int mCurrentColor;
    private final int[] mColors = {
            Color.parseColor("#4F46E5"), // Modern Indigo
            Color.parseColor("#10B981"), // Emerald Green
            Color.parseColor("#F59E0B"), // Vibrant Amber
            Color.parseColor("#EC4899"), // Rose Pink
            Color.parseColor("#06B6D4")  // Modern Cyan
    };
    private int mColorIndex = 0;

    // UI Views
    private TextView tvCount;
    private Button btnCountUp, btnChangeColor, btnResetPrefs;
    private EditText etInputText;
    private Button btnSaveInternal, btnReadInternal;
    private TextView tvFileContent;
    private Button btnCheckStorage;
    private TextView tvStorageInfo;

    // File Storage Constants
    private final String INTERNAL_FILE_NAME = "notes.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        tvCount = findViewById(R.id.tvCount);
        btnCountUp = findViewById(R.id.btnCountUp);
        btnChangeColor = findViewById(R.id.btnChangeColor);
        btnResetPrefs = findViewById(R.id.btnResetPrefs);

        etInputText = findViewById(R.id.etInputText);
        btnSaveInternal = findViewById(R.id.btnSaveInternal);
        btnReadInternal = findViewById(R.id.btnReadInternal);
        tvFileContent = findViewById(R.id.tvFileContent);

        btnCheckStorage = findViewById(R.id.btnCheckStorage);
        tvStorageInfo = findViewById(R.id.tvStorageInfo);

        // Initialize SharedPreferences
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        mCurrentColor = mColors[0];

        // Restore SharedPreferences State in onCreate()
        restorePreferences();

        // 1. SharedPreferences Event Listeners
        btnCountUp.setOnClickListener(v -> {
            mCount++;
            tvCount.setText(String.valueOf(mCount));
        });

        btnChangeColor.setOnClickListener(v -> {
            mColorIndex = (mColorIndex + 1) % mColors.length;
            mCurrentColor = mColors[mColorIndex];
            tvCount.setBackgroundColor(mCurrentColor);
        });

        btnResetPrefs.setOnClickListener(v -> resetPreferences());

        // 2. Internal Storage Event Listeners
        btnSaveInternal.setOnClickListener(v -> saveToInternalStorage());
        btnReadInternal.setOnClickListener(v -> readFromInternalStorage());

        // 3. Storage Info Event Listener
        btnCheckStorage.setOnClickListener(v -> updateStorageInfo());
    }

    // --- 1. SHAREDPREFERENCES METHODS ---

    /**
     * Memulihkan data SharedPreferences saat onCreate()
     */
    private void restorePreferences() {
        mCount = mPreferences.getInt(KEY_COUNT, 0);
        mCurrentColor = mPreferences.getInt(KEY_COLOR, mColors[0]);

        tvCount.setText(String.valueOf(mCount));
        tvCount.setBackgroundColor(mCurrentColor);
    }

    /**
     * Menyimpan data ke SharedPreferences saat onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt(KEY_COUNT, mCount);
        preferencesEditor.putInt(KEY_COLOR, mCurrentColor);
        preferencesEditor.apply(); // Asynchronous save
    }

    /**
     * Membersihkan (Clear) seluruh SharedPreferences
     */
    private void resetPreferences() {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.clear();
        preferencesEditor.apply();

        // Reset local variables
        mCount = 0;
        mColorIndex = 0;
        mCurrentColor = mColors[0];

        tvCount.setText(String.valueOf(mCount));
        tvCount.setBackgroundColor(mCurrentColor);

        Toast.makeText(this, "SharedPreferences berhasil di-reset!", Toast.LENGTH_SHORT).show();
    }

    // --- 2. INTERNAL STORAGE METHODS ---

    /**
     * Menulis teks ke file pribadi di penyimpanan internal (getFilesDir)
     */
    private void saveToInternalStorage() {
        String textToSave = etInputText.getText().toString().trim();
        if (textToSave.isEmpty()) {
            Toast.makeText(this, "Teks tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(getFilesDir(), INTERNAL_FILE_NAME);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(textToSave.getBytes(StandardCharsets.UTF_8));
            Toast.makeText(this, "File berhasil disimpan di Internal Storage!", Toast.LENGTH_SHORT).show();
            etInputText.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal menyimpan file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Membaca teks dari file di penyimpanan internal (getFilesDir)
     */
    private void readFromInternalStorage() {
        File file = new File(getFilesDir(), INTERNAL_FILE_NAME);
        if (!file.exists()) {
            tvFileContent.setText("File belum dibuat/belum ada.");
            Toast.makeText(this, "File tidak ditemukan!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            tvFileContent.setText(stringBuilder.toString().trim());
            Toast.makeText(this, "File berhasil dibaca!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            tvFileContent.setText("Gagal membaca file: " + e.getMessage());
        }
    }

    // --- 3. STORAGE INFO & UTILITY METHODS ---

    /**
     * Mengecek status ketersediaan External Storage (Mount status)
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Memperbarui UI dengan informasi sistem file dan kapasitas penyimpanan
     */
    private void updateStorageInfo() {
        File filesDir = getFilesDir();
        File cacheDir = getCacheDir();

        long freeSpaceBytes = filesDir.getFreeSpace();
        long totalSpaceBytes = filesDir.getTotalSpace();

        double freeSpaceMB = freeSpaceBytes / (1024.0 * 1024.0);
        double totalSpaceMB = totalSpaceBytes / (1024.0 * 1024.0);

        boolean isExternalWritable = isExternalStorageWritable();

        String info = "• Internal Files Dir: " + filesDir.getAbsolutePath() + "\n" +
                "• Internal Cache Dir: " + cacheDir.getAbsolutePath() + "\n" +
                "• Ruang Tersisa (Free Space): " + String.format("%.2f", freeSpaceMB) + " MB\n" +
                "• Total Ruang (Total Space): " + String.format("%.2f", totalSpaceMB) + " MB\n" +
                "• Status External Storage: " + (isExternalWritable ? "MEDIA_MOUNTED (Dapat Ditulis)" : "Tidak Tersedia");

        tvStorageInfo.setText(info);
    }
}