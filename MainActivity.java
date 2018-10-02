package com.example.ashutosh.memorableplaces;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView placesListView;
    List<String> places;
    ArrayList<String> Latlong = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placesListView = (ListView) findViewById(R.id.placesListView);

        places = new ArrayList<String>();
        places.add("Add a New Place....");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, places);

        final Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        placesListView.setAdapter(arrayAdapter);
        placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    intent.putExtra("openLatlong", Latlong.get(i - 1));
                }

                startActivityForResult(intent, 0);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            Log.i("msg", String.valueOf(resultCode));
            if (resultCode == Activity.RESULT_OK) {

                String received = data.getStringExtra("sent");
                ArrayList<String> temp = data.getStringArrayListExtra("Latlong");

                for(int i = 0; i < temp.size(); i++){
                    Latlong.add(temp.get(i));
                }

                Log.i("latlong",Latlong.toString());
                String[] addresses = received.split(";");
                for(String w: addresses){
                    if(!w.equals("")) {
                        places.add(w);
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, places);
                placesListView.setAdapter(arrayAdapter);
            }
        }
    }

}
