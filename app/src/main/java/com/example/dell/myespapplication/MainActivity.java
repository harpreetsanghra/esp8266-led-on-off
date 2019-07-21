package com.example.dell.myespapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;


public class MainActivity extends ActionBarActivity {

    EditText editIp;
    Button btnOn, btnOff;
    TextView textInfo1, textInfo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editIp = (EditText)findViewById(R.id.ip);
        btnOn = (Button)findViewById(R.id.bon);
        btnOff = (Button)findViewById(R.id.boff);
        textInfo1 = (TextView)findViewById(R.id.info1);
        textInfo2 = (TextView)findViewById(R.id.info2);

        btnOn.setOnClickListener(btnOnOffClickListener);
        btnOff.setOnClickListener(btnOnOffClickListener);
    }

    View.OnClickListener btnOnOffClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String onoff;
            if (v == btnOn) {
                onoff = "ledOn";// set the parameters http://192.168.43.157/ledOn
            } else {
                onoff = "ledOff";//set the parameters http://192.168.43.157/ledOff

                btnOn.setEnabled(false);
                btnOff.setEnabled(false);

                String serverIP = editIp.getText().toString();
                TaskEsp taskEsp1 = new TaskEsp(serverIP);
                taskEsp1.execute(onoff);

            }
        }

    };

      public class TaskEsp extends AsyncTask<String, Void, String> {

            String server;

            TaskEsp(String server) {
                this.server = server;
            }

            @Override
            protected String doInBackground(String... params) {

                String val = params[0];
                final String p = "http://192.168.43.157/" + val;// put your defaut ip address of esp8266

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textInfo1.setText(p);
                    }
                });

                String serverResponse = "";
                HttpClient httpclient = new DefaultHttpClient();
                try {
                    HttpGet httpGet = new HttpGet();
                    httpGet.setURI(new URI(p));
                    HttpResponse httpResponse = httpclient.execute(httpGet);

                    InputStream inputStream = null;
                    inputStream = httpResponse.getEntity().getContent();
                    BufferedReader bufferedReader =
                            new BufferedReader(new InputStreamReader(inputStream));
                    serverResponse = bufferedReader.readLine();

                    inputStream.close();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    serverResponse = e.getMessage();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    serverResponse = e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    serverResponse = e.getMessage();
                }

                return serverResponse;
            }

            @Override
            protected void onPostExecute(String s) {
                textInfo2.setText(s);
                btnOn.setEnabled(true);
                btnOff.setEnabled(true);
            }
        }

    }
