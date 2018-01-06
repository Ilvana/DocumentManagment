package com.elmana.documentmanagment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void getData(final String s, final String s1) {
        //Kreiranje klase koja kreira novi thread na kojem se vrsi dobavljanje ovog servisa
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
                    HttpPost httppost = new HttpPost("http://92.48.66.199/~baze/androidprijava.php?uid=" + s + "&pass=" + s1 + "&login=true");
                    HttpResponse response = httpclient.execute(httppost);

                    //Citanje responsa
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                    StringBuilder sb = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb = sb.append(line);
                    }
                    String jsonText = sb.toString();//pokupi rezultat u string
                    Log.i("Vraćeni JSON:", jsonText);

                    JSONObject json = new JSONObject(jsonText); //parsira JSON
                    String parsiranjson = json.getString("status");

                    Log.i("Parsiran JSON;", parsiranjson);

                    if (parsiranjson.equals("OK")) {
                        rezultat = "OK";
                    } else {
                        rezultat = "NOK";
                    }
                } catch (Exception e) {
                    Log.i("Izuzetak:", e.toString());
                }
                return rezultat;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result.equals("OK")) // ako vrati da je resultat ok, korisnik je logovan, predji na novi activity, ako nije nikom nista
                {
                    Intent intent=new Intent(activity, Odjava.class);
                    intent.putExtra("user",s);
                    activity.startActivity(intent);
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask(this);
        sendPostReqAsyncTask.execute(s, s1);
    }

    public void proba(View view)

    {
        TextView t1 = (TextView) findViewById(R.id.editText);
        TextView t2 = (TextView) findViewById(R.id.editText2);
        Log.i("Username:", t1.getText().toString());
        Log.i("Password:", t2.getText().toString());

        getData(t1.getText().toString(), t2.getText().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
