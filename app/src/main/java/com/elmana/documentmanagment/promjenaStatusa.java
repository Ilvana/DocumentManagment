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

public class promjenaStatusa extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promjena_statusa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void prikaz(View view) {
        Intent intent = getIntent();
        String fajl = intent.getStringExtra("fajl");
        prikazDokumenta(fajl);
    }

    public void prelazUNarendnoStanje(View view){
        Intent intent = getIntent();
        String fajl = intent.getStringExtra("fajl");
        Uredu(fajl);
    }

    public void prelazUPrethodnoStanje(View view){
        Intent intent = getIntent();
        String fajl = intent.getStringExtra("fajl");
        nijeUredu(fajl);
    }

    public void prikazDokumenta(final String fajl) {
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
                    Log.i("FAJL", fajl);
                    HttpGet httpget = new HttpGet("http://92.48.66.199/~baze/util/getDocument.php?lokacija=" + URLEncoder.encode(fajl, "UTF-8"));
                    HttpResponse response = httpclient.execute(httpget);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                    StringBuilder sb = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb = sb.append(line);
                    }
                    String jsonText = sb.toString();
                    Log.i("Rezultujući JSON:", jsonText);

                    JSONObject json = new JSONObject(jsonText);
                    String parsiranjson = "Lokacija:" + json.getString("lokacija") + "\n";
                    parsiranjson += "Datum uploada:" + json.getString("datum") + "\n";
                    parsiranjson += "Rok trajanja:" + json.getString("rok") + "\n";
                    parsiranjson += "Korisnik:" + json.getString("korisnik") + "\n";
                    //parsiranjson += "Trenutna faza:" + json.getString("faza") + "\n";
                    //parsiranjson += "Vrsta dokumenta:" + json.getString("vrsta")+"\n";
                    //parsiranjson += "Ime trenutne faze:" + json.getString("ime");
                    Log.i("Parsiran JSON:", parsiranjson);
                    rezultat = parsiranjson;
                    //rezultat=jsonText;

                } catch (Exception e) {
                    Log.i("Izuzetak:", e.toString());
                }
                return rezultat;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, result, duration);
                toast.show();
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask(this);
        sendPostReqAsyncTask.execute();
    }

    public void Uredu(final String fajl) {
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
                    HttpPost httppost = new HttpPost("http://92.48.66.199/~baze/anextStanje.php?fajl="+URLEncoder.encode(fajl, "UTF-8"));
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

                    if (parsiranjson.equals("uspjesno")) {
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
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_LONG;
                    String poruka = "Promjenili ste status uspješno.";
                    Toast toast = Toast.makeText(context, poruka, duration);
                    toast.show();
                } else {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_LONG;
                    String poruka = "Niste promjenili status.";
                    Toast toast = Toast.makeText(context, poruka, duration);
                    toast.show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask(this);
        sendPostReqAsyncTask.execute();
    }

    public void nijeUredu(final String fajl) {
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
                    HttpPost httppost = new HttpPost("http://92.48.66.199/~baze/apreviousStanje.php?fajl="+URLEncoder.encode(fajl, "UTF-8"));
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

                    if (parsiranjson.equals("uspjesno")) {
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
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_LONG;
                    String poruka = "Promjenili ste status uspješno.";
                    Toast toast = Toast.makeText(context, poruka, duration);
                    toast.show();
                } else {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_LONG;
                    String poruka = "Niste promjenili status.";
                    Toast toast = Toast.makeText(context, poruka, duration);
                    toast.show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask(this);
        sendPostReqAsyncTask.execute();
    }



}
