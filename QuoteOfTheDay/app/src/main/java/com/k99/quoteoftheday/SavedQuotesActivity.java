package com.k99.quoteoftheday;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SavedQuotesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SavedQuotesAdapter adapter;
    List<String> savedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_quotes);

        recyclerView = findViewById(R.id.recyclerViewSavedQuotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
        Set<String> savedQuotesSet = prefs.getStringSet("saved_quotes", new HashSet<>());
        savedList = new ArrayList<>(savedQuotesSet);

        adapter = new SavedQuotesAdapter(savedList, quote -> confirmDeleteQuote(quote));
        recyclerView.setAdapter(adapter);

//        adapter = new SavedQuotesAdapter(savedList);
//        recyclerView.setAdapter(adapter);
    }
    private void confirmDeleteQuote(String quoteToDelete) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Quote")
                .setMessage("Are you sure you want to delete this quote?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
                    Set<String> savedQuotes = prefs.getStringSet("saved_quotes", new HashSet<>());
                    Set<String> updatedQuotes = new HashSet<>(savedQuotes);

                    if (updatedQuotes.remove(quoteToDelete)) {
                        prefs.edit().putStringSet("saved_quotes", updatedQuotes).apply();
                        savedList.remove(quoteToDelete);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "Quote deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
