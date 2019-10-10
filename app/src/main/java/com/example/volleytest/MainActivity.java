package com.example.volleytest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ConnectivityManager cm;
    private Context context;
    private Boolean isNet;
    private playerAdapter adapter;
    private ArrayList<playerObj> players = new ArrayList<>();
    private Button button_get;
    private RequestQueue queue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (context == null) {
            context = getApplicationContext();
        }

        isNet = isInternet(context);
        if(isNet == false) {
            Toast.makeText(context, "No network available.", Toast.LENGTH_SHORT).show();
        }

        // Set custom adapter to ListView
        adapter = new playerAdapter(this, R.layout.list_item, players);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        button_get = findViewById(R.id.button_get);
        button_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_get.setEnabled(false);
                if (players.size() > 0) {
                    players.clear();
                }
                doJsonQuery();
            }
        });

    }

    private void doJsonQuery() {
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }
        String url = "https://webd.savonia.fi/home/ktkoiju/j2me/test_json.php?dates&delay=1";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            getDataFromResponse(response);
                            adapter.notifyDataSetChanged();
                            button_get.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        button_get.setEnabled(true);
                    }
                });
        queue.add(jsonArrayRequest);
    }

    public void getDataFromResponse (JSONArray response) throws JSONException {

        for (int i=0; i < response.length(); i++ ) {
            JSONObject o = response.getJSONObject(i);
            String pvm = o.getString("pvm");
            String nimi = o.getString("nimi");
            playerObj po = new playerObj(nimi, pvm );
            players.add(po);
        }
    }

    private boolean isInternet(Context context) {

        final Network[] allNetworks;
        cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        allNetworks = cm.getAllNetworks();
        return (allNetworks != null);
    }
}
