package com.example.datastorages;

import android.os.Bundle;
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

    // Nama file privat di Internal Storage (getFilesDir)
    private final String INTERNAL_FILE_NAME = "data.txt";

    // UI Views
    private EditText etName;
    private EditText etNote;
    private Button btnSave;
    private Button btnRead;
    private Button btnDelete;
    private TextView tvFileContent;

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

        etName = findViewById(R.id.etName);
        etNote = findViewById(R.id.etNote);
        btnSave = findViewById(R.id.btnSave);
        btnRead = findViewById(R.id.btnRead);
        btnDelete = findViewById(R.id.btnDelete);
        tvFileContent = findViewById(R.id.tvFileContent);
    }

        btnSave.setOnClickListener(v -> saveToInternalStorage());
        btnRead.setOnClickListener(v -> readFromInternalStorage());
        btnDelete.setOnClickListener(v -> deleteInternalFile());
    }

    /**
     * Menyimpan nama + catatan ke file privat di Internal Storage (getFilesDir).
     * Sesuai materi: new File(getFilesDir(), filename) + FileOutputStream.
     */
    private void saveToInternalStorage() {
        String name = etName.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (note.isEmpty()) {
            Toast.makeText(this, "Catatan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        String content = "Nama: " + name + "\nCatatan: " + note + "\n";
        File file = new File(getFilesDir(), INTERNAL_FILE_NAME);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            Toast.makeText(this, "Data berhasil disimpan di Internal Storage!", Toast.LENGTH_SHORT).show();
            etName.setText("");
            etNote.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Membaca file dari Internal Storage.
     * Sesuai materi: FileInputStream + InputStreamReader + BufferedReader.
     */
    private void readFromInternalStorage() {
        File file = new File(getFilesDir(), INTERNAL_FILE_NAME);
        if (!file.exists()) {
            tvFileContent.setText("Belum ada data tersimpan.");
            Toast.makeText(this, "File tidak ditemukan!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            tvFileContent.setText(sb.toString().trim());
            Toast.makeText(this, "Data berhasil dibaca!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            tvFileContent.setText("Gagal membaca file: " + e.getMessage());
        }
    }

    /**
     * Menghapus file dari Internal Storage.
     * Sesuai materi: myContext.deleteFile(fileName) untuk internal.
     */
    private void deleteInternalFile() {
        File file = new File(getFilesDir(), INTERNAL_FILE_NAME);
        if (!file.exists()) {
            Toast.makeText(this, "Tidak ada data untuk dihapus!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (deleteFile(INTERNAL_FILE_NAME)) {
            tvFileContent.setText("Belum ada data tersimpan.");
            Toast.makeText(this, "Data berhasil dihapus!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal menghapus data!", Toast.LENGTH_SHORT).show();
        }
    }

}
