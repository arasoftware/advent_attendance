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
import com.ara.advent.models.Customer;

import java.util.ArrayList;

/**
 * Created by User on 23-Apr-18.
 */

public class CustomerAdapter extends ArrayAdapter<Customer> {
    private Activity context;
    ArrayList<Customer> data = null;


    public CustomerAdapter(Activity context, int resource, ArrayList<Customer> data) {
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
        Customer item = data.get(position);

        if (item != null) { // Parse the data from each object and set it.
            TextView customerId = (TextView) row.findViewById(R.id.item_id);
            TextView customerName = (TextView) row.findViewById(R.id.item_value);
            if (customerId != null) {
                customerId.setText(item.getCus_id());
            }
            if (customerName != null) {
                customerName.setText(item.getCus_name());
            }

        }
        return row;
    }
}
