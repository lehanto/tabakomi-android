package com.example.lehanto.tabakomi;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class MainActivity extends Activity {

    private static final String TABAKOMI_API_URL = "http://133.2.37.129/register_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    public void onClickSignUp(View view){
        EditText userNameEditText = (EditText)findViewById(R.id.username_text);

        EditText passwordEditText = (EditText)findViewById(R.id.password_text);
            String userName = userNameEditText.getText().toString();
            String password = passwordEditText.getText().toString();


        Log.d("onClickSignUp", userName + password);
        // Create request params
        HashMap hm = new HashMap();
        hm.put("name", userName);
        hm.put("password", password);
        HttpPostTask task = new HttpPostTask();
        task.execute(hm);
    }


    /**
     *  HttpPostTask class
     *  This class is used when Http is requested.
     */
    public class HttpPostTask extends AsyncTask<HashMap, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(HashMap... hm) {
            // Execute http request
            JSONObject jo = null;
            try {
                // Create params
                Map<String, Object> params = new LinkedHashMap<>();
                for (Object key : hm[0].keySet()) {
                    params.put(key.toString(), hm[0].get(key.toString()));
                }

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                URL url = new URL(TABAKOMI_API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                // Http request
                InputStream is = conn.getInputStream();
                jo = new JSONObject(convertInputStreamToString(is));
            } catch (Exception e) {
                Log.d("HttpPostTask", "HttpConnectionError");
            }
            return jo;
        }

        @Override
        protected void onPostExecute(JSONObject jo) {
            // 処理終了後
            try {
                Log.d("JSON Parse", jo.getString("user_id"));
            } catch (JSONException e) {
                Log.d("JSON Parse", "Error");
            }

        }
    }

        /** Convert InputStream to String */
        static String convertInputStreamToString(InputStream is) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
        }


}
