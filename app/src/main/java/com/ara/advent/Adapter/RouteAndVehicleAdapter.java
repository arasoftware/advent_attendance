package com.ara.advent.Adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ara.advent.R;
import com.ara.advent.models.Customer;
import com.ara.advent.models.RouteAndvehicleModel;

import java.util.ArrayList;

/**
 * Created by User on 09-May-18.
 */

public class RouteAndVehicleAdapter extends ArrayAdapter<RouteAndvehicleModel> {

    ArrayList<RouteAndvehicleModel> data = null;
    private Activity context;

    public RouteAndVehicleAdapter(Activity context, int resource, ArrayList<RouteAndvehicleModel> data) {
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
            row = inflater.inflate(R.layout.simple_spinner_item_two, parent, false);
        }
        RouteAndvehicleModel item = data.get(position);

        if (item != null) { // Parse the data from each object and set it.
            TextView station_name = (TextView) row.findViewById(R.id.item_stationname);
            TextView station_name_id = (TextView) row.findViewById(R.id.item_stationId);

            if (station_name != null) {
                station_name.setText(item.getName());
            }
            if (station_name_id != null) {
                station_name_id.setText(item.getId());
            }

        }

        return row;
    }


}