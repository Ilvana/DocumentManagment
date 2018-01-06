package com.elmana.documentmanagment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Odjava extends AppCompatActivity {

    ListView listaDokumenata;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odjava);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listaDokumenata = (ListView) findViewById(R.id.listView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ispisiDokumente();

        listaDokumenata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                String value = (String) listaDokumenata.getItemAtPosition(position);
                Intent intent = new Intent(Odjava.this, promjenaStatusa.class);
                intent.putExtra("fajl", value);
                Odjava.this.startActivity(intent);
            }
        });
    }

    public void OdjaviSe() {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            private Activity activity;

            public SendPostReqAsyncTask(Activity activity) {
                this.activity = activity;
            }

            @Override
            protected String doInBackground(String... params) {
                String rezultat = "";
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://92.48.66.199/~baze/androidodjava.php?logout");
                    HttpResponse response = httpclient.execute(httppost);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                    StringBuilder sb = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb = sb.append(line);
                    }
                    String jsonText = sb.toString();
                    Log.i("Rezultujući JSON:", jsonText);

                    JSONObject json = new JSONObject(jsonText);
                    String parsiranjson = json.getString("status");
                    Log.i("Parsiran JSON:", parsiranjson);

                    if (parsiranjson.equals("odjava")) {
                        rezultat = "OK";
                    }
                } catch (Exception e) {
                    Log.i("Izuzetak:", e.toString());
                }
                return rezultat;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result.equals("OK")) {
                    activity.startActivity(new Intent(activity, MainActivity.class));
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask(this);
        sendPostReqAsyncTask.execute();
    }

    public void odjava(View view) {
        OdjaviSe();
    }

    public void ispisiDokumente() {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            private Activity activity;

            public SendPostReqAsyncTask(Activity activity) {
                this.activity = activity;
            }

            @Override
            protected String doInBackground(String... params) {
                String rezultat = "";
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    Intent intent = getIntent();
                    String prijavljeniuser = intent.getStringExtra("user");
                    HttpPost httppost = new HttpPost("http://92.48.66.199/~baze/androidIspis.php?user=" + prijavljeniuser);
                    HttpResponse response = httpclient.execute(httppost);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                    StringBuilder sb = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb = sb.append(line);
                    }
                    String jsonText = sb.toString();
                    Log.i("Rezultujući JSON:", jsonText);
                    ArrayList dokumenti = new ArrayList();

                    String[] nizDokumenata = jsonText.split(",");
                    for (int i = 0; i < nizDokumenata.length; i++) {
                        JSONObject json = new JSONObject(nizDokumenata[i]);
                        String parsiranjson = json.getString("dokument");
                        dokumenti.add(parsiranjson);
                        Log.i("Parsiran JSON:", parsiranjson);
                    }
                    adapter = new ArrayAdapter<String>(this.activity, android.R.layout.simple_list_item_1, dokumenti);

                } catch (Exception e) {
                    Log.i("Izuzetak:", e.toString());
                }
                return rezultat;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                listaDokumenata.setAdapter(adapter);
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask(this);
        sendPostReqAsyncTask.execute();
    }


}
