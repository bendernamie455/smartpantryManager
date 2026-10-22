package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class PantryAdapter extends ArrayAdapter<PantryItem> {

    public interface PantryActions {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final PantryActions actions;

    public PantryAdapter(Context context, ArrayList<PantryItem> items,
                         PantryActions actions) {
        super(context, 0, items);
        this.actions = actions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            row = LayoutInflater.from(getContext())
                    .inflate(R.layout.row_pantry_item, parent, false);
        }

        PantryItem item = getItem(position);

        TextView nameText = row.findViewById(R.id.textItemName);
        TextView detailsText = row.findViewById(R.id.textItemDetails);
        Button editButton = row.findViewById(R.id.buttonEdit);
        Button deleteButton = row.findViewById(R.id.buttonDelete);

        if (item != null) {
            nameText.setText(item.getName());

            String details = item.getQuantity() + " " + item.getUnit();
            if (item.getExpiryDate() != null &&
                    !item.getExpiryDate().trim().isEmpty()) {
                details += "\nExpires: " + item.getExpiryDate();
            }
            detailsText.setText(details);

            editButton.setOnClickListener(v -> actions.onEdit(item));

            deleteButton.setOnClickListener(v ->
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete ingredient")
                            .setMessage("Delete " + item.getName() + "?")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Delete", (dialog, which) ->
                                    actions.onDelete(item))
                            .show());
        }

        return row;
    }
}
