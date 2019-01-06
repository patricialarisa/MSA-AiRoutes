package com.example.app.newproject;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import static com.example.app.newproject.AirportActivity.routes;

public class RoutesListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_list);
        // Create a list data which will be displayed in inner ListView.
//        List<String> listData = new ArrayList<String>();
//        listData.add("Audi");
//        listData.add("Benz");
//        listData.add("BMW");
//        listData.add("Ford");
//        listData.add("Honda");
//        listData.add("Toyoto");

        // Create the ArrayAdapter use the item row layout and the list data.
        ArrayAdapter<String> listDataAdapter = new ArrayAdapter<String>(this, R.layout.activity_routes_list_row, R.id.listRowTextView, routes);

        // Set this adapter to inner ListView object.
        this.setListAdapter(listDataAdapter);
    }

    // When user click list item, this method will be invoked.
//    @Override
//    protected void onListItemClick(ListView listView, View v, int position, long id) {
//        // Get the list data adapter.
//        ListAdapter listAdapter = listView.getAdapter();
//        // Get user selected item object.
//        Object selectItemObj = listAdapter.getItem(position);
//        String itemText = (String)selectItemObj;
//
//        // Create an AlertDialog to show.
//        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//        alertDialog.setMessage(itemText);
//        alertDialog.show();
//    }
}
