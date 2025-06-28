package com.k99.adminqotday;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    TextInputEditText etQuote, etAuthor, etCategory;
    Button btnUpload;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null){
        showAdminLoginDialog();
        }
        etQuote = findViewById(R.id.etQuote);
        etAuthor = findViewById(R.id.etAuthor);
        etCategory = findViewById(R.id.etCategory);
        btnUpload = findViewById(R.id.btnUpload);

        dbRef = FirebaseDatabase.getInstance().getReference("quotes");

        btnUpload.setOnClickListener(v -> {
            String quote = etQuote.getText().toString().trim();
            String author = etAuthor.getText().toString().trim();
            String category = toCamelCase(etCategory.getText().toString().trim());

            if (quote.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = dbRef.push().getKey();
            HashMap<String, String> quoteData = new HashMap<>();
            quoteData.put("text", quote);
            quoteData.put("author", author);
            quoteData.put("category", category);

            dbRef.child(id).setValue(quoteData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Quote uploaded", Toast.LENGTH_SHORT).show();
                    etQuote.setText("");
                    etAuthor.setText("");
//                    etCategory.setText("");
                    etQuote.requestFocus();
                } else {
                    Toast.makeText(this, "Failed to upload", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private void showAdminLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Admin Login");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Enter admin password");

        builder.setView(input);

        builder.setPositiveButton("Login", (dialog, which) -> {
            String password = input.getText().toString().trim();
            String adminEmail = "admin@k99.com"; // use your admin email

            FirebaseAuth.getInstance().signInWithEmailAndPassword(adminEmail, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Admin authenticated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            finish(); // optionally close app
                        }
                    });
        });

        builder.setCancelable(false);
        builder.show();
    }
    public static String toCamelCase(String input) {
        String[] words = input.toLowerCase().split("[\\s_\\-]+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1));
            }
        }

        return result.toString();
    }
}