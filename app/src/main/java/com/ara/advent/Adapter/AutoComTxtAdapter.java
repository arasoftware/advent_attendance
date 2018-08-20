package com.ara.advent.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ara.advent.R;
import com.ara.advent.models.AutoComTxtModel;

import java.util.ArrayList;


public class AutoComTxtAdapter extends ArrayAdapter<AutoComTxtModel> {
    ArrayList<AutoComTxtModel> data = null;
    private Activity context;


    public AutoComTxtAdapter(Activity context, int resource, ArrayList<AutoComTxtModel> data) {
        super(context, resource, data);
        this.context = context;
        this.data = data;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.simple_spinner_item, parent, false);
        }
        AutoComTxtModel item = data.get(position);

        if (item != null) { // Parse the data from each object and set it.
            TextView customerId = (TextView) row.findViewById(R.id.item_id_actv);
            TextView customerName = (TextView) row.findViewById(R.id.item_value_actv);
            if (customerId != null) {
                customerId.setText(item.getId());
            }
            if (customerName != null) {
                customerName.setText(item.getVeh_no());
            }

        }
        return row;
    }


}
