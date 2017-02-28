package android.pillcentral;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.app.ProgressDialog;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;

public class singlepilldisplay extends AppCompatActivity {
    private static final String TAG = Login.class.getSimpleName();
    private ProgressDialog pDialog;
    private TextView dtime;
    private TextView dlocal;
    private TextView dname;
    private ProgressBar timetill;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepilldisplay);
        Calendar cal = Calendar.getInstance();

        dtime = (TextView) findViewById(R.id.taketime);
        dlocal= (TextView) findViewById(R.id.pilllocal);
        dname = (TextView) findViewById(R.id.pillname);


        String username = getIntent().getStringExtra("username");
        SimpleDateFormat propertime = new SimpleDateFormat("HH:mm:ss");
        String time = propertime.format(cal.getTime());
        String dow = cal.get(Calendar.DAY_OF_WEEK)-1 + "";


        displaypill(username, time, dow);
    }


    private void displaypill(final String username, final String time, final String dow)
    {
        final String tag_string_req = "req_login";

        pDialog.setMessage("Loading today's pills ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_PILLDISPLAY, new Response.Listener<String>()
        {

            @Override
            public void onResponse(String response)
            {
                Log.d(TAG, "Pill Response: " + response.toString());
                hideDialog();

                try{
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    //checks error node in json
                    if(!error)
                    {


                        //put code to input into list here
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
                params.put("DOW", dow);

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

