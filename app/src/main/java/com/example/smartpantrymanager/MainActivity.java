package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private PantryDatabaseHelper database;
    private PantryAdapter adapter;
    private ListView listPantry;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new PantryDatabaseHelper(this);

        listPantry = findViewById(R.id.listPantry);
        textEmpty = findViewById(R.id.textEmpty);
        Button buttonAdd = findViewById(R.id.buttonAddItem);
        Button buttonRecipes = findViewById(R.id.buttonRecipes);

        adapter = new PantryAdapter(
                this,
                new ArrayList<>(),
                new PantryAdapter.PantryActions() {
                    @Override
                    public void onEdit(PantryItem item) {
                        showIngredientDialog(item);
                    }

                    @Override
                    public void onDelete(PantryItem item) {
                        database.deleteItem(item.getId());
                        Toast.makeText(MainActivity.this,
                                "Ingredient deleted",
                                Toast.LENGTH_SHORT).show();
                        loadPantry();
                    }
                }
        );

        listPantry.setAdapter(adapter);

        buttonAdd.setOnClickListener(v -> showIngredientDialog(null));

        buttonRecipes.setOnClickListener(v ->
                Toast.makeText(this,
                        "Recipe screen will be added in the next stage.",
                        Toast.LENGTH_SHORT).show());

        loadPantry();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        ArrayList<PantryItem> pantryItems = database.getAllItems();

        adapter.clear();
        adapter.addAll(pantryItems);
        adapter.notifyDataSetChanged();

        boolean empty = pantryItems.isEmpty();
        textEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        listPantry.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void showIngredientDialog(PantryItem existingItem) {
        boolean editing = existingItem != null;

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        form.setPadding(padding, padding / 2, padding, 0);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Ingredient name (e.g. Rice)");
        nameInput.setSingleLine(true);
        form.addView(nameInput);

        EditText quantityInput = new EditText(this);
        quantityInput.setHint("Quantity (e.g. 2)");
        quantityInput.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
        form.addView(quantityInput);

        EditText unitInput = new EditText(this);
        unitInput.setHint("Unit (e.g. kg, g, items, ml)");
        unitInput.setSingleLine(true);
        form.addView(unitInput);

        EditText expiryInput = new EditText(this);
        expiryInput.setHint("Expiry date (optional, YYYY-MM-DD)");
        expiryInput.setSingleLine(true);
        form.addView(expiryInput);

        if (editing) {
            nameInput.setText(existingItem.getName());
            quantityInput.setText(String.valueOf(existingItem.getQuantity()));
            unitInput.setText(existingItem.getUnit());
            expiryInput.setText(existingItem.getExpiryDate());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(editing ? "Edit Ingredient" : "Add Ingredient")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton(editing ? "Save" : "Add", null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {
                            String name = nameInput.getText().toString().trim();
                            String quantityText =
                                    quantityInput.getText().toString().trim();
                            String unit = unitInput.getText().toString().trim();
                            String expiry =
                                    expiryInput.getText().toString().trim();

                            if (name.isEmpty() || quantityText.isEmpty()
                                    || unit.isEmpty()) {
                                Toast.makeText(this,
                                        "Enter a name, quantity and unit.",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            double quantity;
                            try {
                                quantity = Double.parseDouble(quantityText);
                            } catch (NumberFormatException e) {
                                Toast.makeText(this,
                                        "Enter a valid quantity.",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (quantity <= 0) {
                                Toast.makeText(this,
                                        "Quantity must be greater than zero.",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            PantryItem item = new PantryItem(
                                    editing ? existingItem.getId() : 0,
                                    name,
                                    quantity,
                                    unit,
                                    expiry.isEmpty() ? null : expiry
                            );

                            long result;
                            if (editing) {
                                result = database.updateItem(item);
                            } else {
                                result = database.addItem(item);
                            }

                            if (result != -1) {
                                Toast.makeText(this,
                                        editing ? "Ingredient updated"
                                                : "Ingredient added",
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                loadPantry();
                            } else {
                                Toast.makeText(this,
                                        "Could not save the ingredient.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }));

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}