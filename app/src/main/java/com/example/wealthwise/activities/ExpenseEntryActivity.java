package com.example.wealthwise.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.wealthwise.R;
import com.example.wealthwise.database.WealthWiseDatabase;
import com.example.wealthwise.models.Expense;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.google.android.material.textfield.TextInputEditText;
import com.example.wealthwise.database.WealthWiseDatabase;
import com.example.wealthwise.models.Expense;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.textfield.TextInputEditText;
import com.example.wealthwise.database.WealthWiseDatabase;
import com.example.wealthwise.models.Expense;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpenseEntryActivity extends AppCompatActivity {

    private String selectedCategory = null;
    private CardView currentlySelectedCard = null;
    private Button selectDateButton;
    private String selectedDate;
    private TextInputEditText expenseAmountEditText;
    private WealthWiseDatabase database;
    private HashMap<String, CardView> categoryCards = new HashMap<>();

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_entry);

        database = WealthWiseDatabase.getDatabase(this);
        expenseAmountEditText = findViewById(R.id.expenseAmount);
        selectDateButton = findViewById(R.id.selectDateButton);


        setupCategorySelection();


        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        selectDateButton.setText(selectedDate);


        selectDateButton.setOnClickListener(v -> showDatePickerDialog());


        findViewById(R.id.submitExpense).setOnClickListener(v -> {
            if (validateInput()) {
                addExpenseToDatabase();
            }
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.add_expense));
        }


        Button uploadExpenseByReciept = findViewById(R.id.submitExpenseByReciept);
        uploadExpenseByReciept.setOnClickListener(v -> openGallery());


    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                extractTextFromImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void extractTextFromImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        TextRecognizer recognizer = TextRecognition.getClient(new com.google.mlkit.vision.text.latin.TextRecognizerOptions.Builder().build());

        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String fullText = visionText.getText();
                    parseReceiptDetails(fullText);
                })
                .addOnFailureListener(e -> {
                    Log.e("TextRecognition", "Failed to extract text: ", e);
                    Toast.makeText(this, "Failed to extract text!", Toast.LENGTH_SHORT).show();
                });
    }


    private void parseReceiptDetails(String text) {


        String amountPattern = "\\$?([0-9]+\\.[0-9]{2})";
        String datePattern = "(\\b\\d{1,2}[-/\\s]\\d{1,2}[-/\\s]\\d{2,4}\\b)";


        Pattern amountRegex = Pattern.compile(amountPattern);
        Matcher amountMatcher = amountRegex.matcher(text);

        double maxAmount = 0.0;

        while (amountMatcher.find()) {
            String amountStr = amountMatcher.group(1);
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount > maxAmount) {
                    maxAmount = amount;
                }
            } catch (NumberFormatException e) {
                Log.e("ReceiptParsing", "Error parsing amount: " + amountStr, e);
            }
        }



        Pattern dateRegex = Pattern.compile(datePattern);
        Matcher dateMatcher = dateRegex.matcher(text);
        String rawDate = null;
        if (dateMatcher.find()) {
            rawDate = dateMatcher.group(1);
        }



        String formattedDate = getString(R.string.invalid_date);

        if (rawDate != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = inputFormat.parse(rawDate.replace(" ", "/").replace("-", "/"));
                formattedDate = outputFormat.format(date);
            } catch (ParseException e) {
                Log.e("DateParsing", "Error parsing date: " + rawDate, e);
            }
        }



        double finalMaxAmount = maxAmount;
        String finalFormattedDate = formattedDate;
        runOnUiThread(() -> {
            expenseAmountEditText.setText(finalMaxAmount > 0 ? String.format("%.2f", finalMaxAmount) : "");
            selectedDate = finalFormattedDate;
            selectDateButton.setText(selectedDate);
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    calendar.set(year, month, dayOfMonth);
                    selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                    selectDateButton.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    private void setupCategorySelection() {


        categoryCards.put("Transportation", findViewById(R.id.transportationCard));
        categoryCards.put("Food", findViewById(R.id.foodCard));
        categoryCards.put("Entertainment", findViewById(R.id.entertainmentCard));
        categoryCards.put("Utilities", findViewById(R.id.utilitiesCard));
        categoryCards.put("Household Expenses", findViewById(R.id.householdExpensesCard));
        categoryCards.put("Health", findViewById(R.id.healthCard));
        categoryCards.put("Travel", findViewById(R.id.travelCard));
        categoryCards.put("Shopping", findViewById(R.id.shoppingCard));



        for (String category : categoryCards.keySet()) {
            CardView cardView = categoryCards.get(category);
            cardView.setOnClickListener(v -> toggleCategorySelection(category, cardView));
        }
    }


    private void toggleCategorySelection(String category, CardView selectedCard) {
        if (currentlySelectedCard == selectedCard) {


            deselectCurrentCategory();
        } else {


            selectCategory(category, selectedCard);
        }
    }


    private void selectCategory(String category, CardView selectedCard) {
        deselectCurrentCategory();

        selectedCard.setCardBackgroundColor(getResources().getColor(R.color.light_grey));
        selectedCategory = category;
        currentlySelectedCard = selectedCard;
    }


    private void deselectCurrentCategory() {
        if (currentlySelectedCard != null) {
            currentlySelectedCard.setCardBackgroundColor(getResources().getColor(R.color.white));
            currentlySelectedCard = null;
        }
        selectedCategory = null;
    }


    private boolean validateInput() {
        String amountText = expenseAmountEditText.getText().toString().trim();

        if (amountText.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_expense_amount), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedCategory == null) {
            Toast.makeText(this, getString(R.string.select_expense_category), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void addExpenseToDatabase() {
        String amountText = expenseAmountEditText.getText().toString().trim();
        float amount = Float.parseFloat(amountText);





        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setCategory(selectedCategory);
        expense.setDate(selectedDate);



        new Thread(() -> {
            database.expenseDao().insert(expense);

            runOnUiThread(() -> {
                Toast.makeText(this, getString(R.string.expense_added_success), Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}

