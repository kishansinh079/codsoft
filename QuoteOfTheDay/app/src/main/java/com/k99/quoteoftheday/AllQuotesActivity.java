package com.k99.quoteoftheday;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllQuotesActivity extends AppCompatActivity {

    private RecyclerView quoteList;
    private Spinner categorySpinner;

    private List<QuoteModel> allQuotes = new ArrayList<>();
    private Set<String> categories = new HashSet<>();
    private AllQuotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_quotes);

        quoteList = findViewById(R.id.quoteList);
        EditText searchInput = findViewById(R.id.searchInput);
        ImageView searchIcon = findViewById(R.id.searchIcon);
        categorySpinner = findViewById(R.id.categorySpinner);

        quoteList.setLayoutManager(new LinearLayoutManager(this));
        searchIcon.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            filterQuotes(query);
        });
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterQuotes(searchInput.getText().toString().trim());
                return true;
            }
            return false;
        });

        fetchQuotesFromFirebase();
    }

    private void fetchQuotesFromFirebase() {
        FirebaseDatabase.getInstance().getReference("quotes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allQuotes.clear();
                        categories.clear();

                        for (DataSnapshot child : snapshot.getChildren()) {
                            QuoteModel quote = child.getValue(QuoteModel.class);
                            if (quote != null) {
                                allQuotes.add(quote);
                                if (quote.getCategory() != null) {
                                    categories.add(quote.getCategory());
                                }
                            }
                        }

                        // Spinner Setup
                        List<String> categoryList = new ArrayList<>(categories);
                        categoryList.add(0, "All");
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                                AllQuotesActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                categoryList
                        );
                        categorySpinner.setAdapter(spinnerAdapter);

                        // Set initial list
                        adapter = new AllQuotesAdapter(AllQuotesActivity.this, allQuotes);

                        quoteList.setAdapter(adapter);

                        // Spinner listener for filtering
                        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selected = parent.getItemAtPosition(position).toString();
                                List<QuoteModel> filtered = new ArrayList<>();

                                if (selected.equals("All")) {
                                    filtered.addAll(allQuotes);
                                } else {
                                    for (QuoteModel q : allQuotes) {
                                        if (q.getCategory() != null &&
                                                q.getCategory().equalsIgnoreCase(selected)) {
                                            filtered.add(q);
                                        }
                                    }
                                }

                                adapter = new AllQuotesAdapter(AllQuotesActivity.this, filtered);

                                quoteList.setAdapter(adapter);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AllQuotesActivity.this, "Failed to load quotes", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void filterQuotes(String query) {
        List<QuoteModel> filtered = new ArrayList<>();
        for (QuoteModel quote : allQuotes) {
            if (quote.getText().toLowerCase().contains(query.toLowerCase()) ||
                    quote.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(quote);
            }
        }

        adapter = new AllQuotesAdapter(AllQuotesActivity.this, filtered);
        quoteList.setAdapter(adapter);
    }

}