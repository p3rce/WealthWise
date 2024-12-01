package com.example.wealthwise.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wealthwise.R;
import com.google.android.material.slider.Slider;

public class SettingsActivity extends AppCompatActivity {

    private EditText nameEditText;
    private Slider budgetSlider;
    private Button saveButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        sharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE);

        nameEditText = findViewById(R.id.nameEditText);
        budgetSlider = findViewById(R.id.budgetSlider);
        saveButton = findViewById(R.id.saveButton);


        loadSettings();


        saveButton.setOnClickListener(v -> saveSettings());


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadSettings() {
        String name = sharedPreferences.getString("name", "");
        float budget = sharedPreferences.getFloat("budget", 0f);

        nameEditText.setText(name);
        budgetSlider.setValue(budget);
    }

    private void saveSettings() {
        String name = nameEditText.getText().toString().trim();
        float budget = budgetSlider.getValue();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }



        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putFloat("budget", budget);
        editor.apply();

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}