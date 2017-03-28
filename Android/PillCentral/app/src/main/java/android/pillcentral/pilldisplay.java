package android.pillcentral;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
//import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;


public class pilldisplay extends AppCompatActivity {
    private static final String TAG = Login.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilldisplay);

        lv = (ListView) findViewById(R.id.pillList);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        String username = getIntent().getStringExtra("username");
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute =cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.MILLISECOND);
        String curtime = hour + ":" +  minute + ":" + sec;
        int day = cal.get(Calendar.DAY_OF_WEEK)-1;
        String DOW = day + "";

        getpills(username,curtime ,DOW);
    }

    private void getpills(final String username, final String time, final String DOW)
    {
        final String tag_string_req = "req_login";

        pDialog.setMessage("Populating today's pills ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>()
        {

            @Override
            public void onResponse(String response)
            {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try{
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    //checks error node in json
                    if(!error)
                    {
                        JSONObject pill = jObj.getJSONObject("pill");
                        //get info from php
                        String pillname = pill.getString("pill_name");
                        String position = pill.getString("position");
                        String time = pill.getString("time");

                        //fill ListView


                        finish();
                    }else
                    {
                        String errorMsg = jObj.get("error_msg").toString();
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                //Posting paramters to login URL
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("time", time);
                params.put("DOW", DOW);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog()
    {
        if(!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog()
    {
        if(pDialog.isShowing())
            pDialog.dismiss();
    }


}
