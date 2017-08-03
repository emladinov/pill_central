package android.pillcentral;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;

public class addpill extends AppCompatActivity {
    private static final String TAG = addpill.class.getSimpleName();
    private TextView timeset;
    private TextView ampm;
    private EditText pillname;
    private EditText dosage;
    private EditText location;
    private EditText pi;
    private Button settime;
    private Button submit;
    private CheckBox SUN;
    private CheckBox MON;
    private CheckBox TUES;
    private CheckBox WED;
    private CheckBox THUR;
    private CheckBox FRI;
    private CheckBox SAT;
    private ProgressDialog pDialog;

    JSONParser jParser = new JSONParser();
    JSONArray pills = null;

    int nums = 0;
    boolean checkers = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpill);

        settime = (Button) findViewById(R.id.settime);
        submit = (Button) findViewById(R.id.Submit);
        dosage = (EditText) findViewById(R.id.dosage);
        pillname = (EditText) findViewById(R.id.pilltxt);
        location = (EditText) findViewById(R.id.loc);
        pi = (EditText) findViewById(R.id.pillinformation);
        timeset = (TextView) findViewById(R.id.timeset);
        ampm = (TextView) findViewById(R.id.AMPM);
        SUN = (CheckBox) findViewById(R.id.sun);
        MON = (CheckBox) findViewById(R.id.mon);
        TUES = (CheckBox) findViewById(R.id.tues);
        WED = (CheckBox) findViewById(R.id.wed);
        THUR = (CheckBox) findViewById(R.id.thurs);
        FRI = (CheckBox) findViewById(R.id.fri);
        SAT = (CheckBox) findViewById(R.id.sat);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        boxnumbers(getIntent().getStringExtra("username"));

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour ==12){
            ampm.setText("PM");
        }
        else if (hour>11)
        {
            ampm.setText("PM");
            hour = hour-12;
        }
        timeset.setText(singletodouble(hour) + ":" + singletodouble(cal.get(Calendar.MINUTE)));

        settime.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(),"TimePicker");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String pill = pillname.getText().toString();
                String dos = dosage.getText().toString();
                String local = location.getText().toString();
                String time = timeset.getText().toString();
                String AMPM = ampm.getText().toString();
                int h;
                String[] ttime = null;
                if (pill.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a pill name", Toast.LENGTH_LONG).show();
                } else if (dos.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a dosage, if not stated enter 0", Toast.LENGTH_LONG).show();
                } else if (local.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a pill box location", Toast.LENGTH_LONG).show();
                } else if (!pill.isEmpty() && !dos.isEmpty() && !local.isEmpty()) {
                    ttime = time.split(":");
                    h = Integer.parseInt(ttime[0]);
                    if (AMPM == "PM") {
                        h = h + 12;
                        ttime[0] = String.valueOf(h);
                    } else {
                        ttime[0] = singletodouble(h);
                    }

                    new boxcheck().execute(getIntent().getStringExtra("username"), local, pill, dos);
                    if(Integer.parseInt(local) <= nums && Integer.parseInt(local) >0 && checkers) {
                        if (!SUN.isChecked() && !MON.isChecked() && !TUES.isChecked() && !WED.isChecked() && !THUR.isChecked() && !FRI.isChecked() && !SAT.isChecked()) {
                            Toast.makeText(getApplicationContext(), "Please choose at least 1 day of the week", Toast.LENGTH_LONG).show();
                        }

                        if (SUN.isChecked()) {
                            pillsubmit(getIntent().getStringExtra("username"), pill, dos, "0", ttime[0], ttime[1], "00", local, pi.getText().toString());
                        }
                        if (MON.isChecked()) {
                            pillsubmit(getIntent().getStringExtra("username"), pill, dos, "1", ttime[0], ttime[1], "00", local, pi.getText().toString());
                        }
                        if (TUES.isChecked()) {
                            pillsubmit(getIntent().getStringExtra("username"), pill, dos, "2", ttime[0], ttime[1], "00", local, pi.getText().toString());
                        }
                        if (WED.isChecked()) {
                            pillsubmit(getIntent().getStringExtra("username"), pill, dos, "3", ttime[0], ttime[1], "00", local, pi.getText().toString());
                        }
                        if (THUR.isChecked()) {
                            pillsubmit(getIntent().getStringExtra("username"), pill, dos, "4", ttime[0], ttime[1], "00", local, pi.getText().toString());
                        }
                        if (FRI.isChecked()) {
                            pillsubmit(getIntent().getStringExtra("username"), pill, dos, "5", ttime[0], ttime[1], "00", local, pi.getText().toString());
                        }
                        if (SAT.isChecked()) {
                            pillsubmit(getIntent().getStringExtra("username"), pill, dos, "6", ttime[0], ttime[1], "00", local, pi.getText().toString());
                        }
                    }else if (Integer.parseInt(local) > nums || Integer.parseInt(local) <= 0) {
                        Toast.makeText(getApplicationContext(), "Box does not exist", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Box already taken by another medication", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cancel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_cancel:
                Intent intent = new Intent(addpill.this, Tabs.class);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String singletodouble(int single)
    {
        switch(single){
            case 0: return "00";
            case 1: return "01";
            case 2: return "02";
            case 3: return "03";
            case 4: return "04";
            case 5: return "05";
            case 6: return "06";
            case 7: return "07";
            case 8: return "08";
            case 9: return "09";
            default: break;
        }
        return String.valueOf(single);
    }

    private void pillsubmit(final String username, final String pill,final String dosage, final String d, final String hour, final String minute, final String second, final String local, final String ai )
    {
        final String tag_string_req = "req_registration";

        pDialog.setMessage("Submitting request ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_NEWPILL, new Response.Listener<String>()
        {

            @Override
            public void onResponse(String response)
            {
                Log.d(TAG, "Submission Response: " + response.toString());
                hideDialog();

                try{
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    //checks error node in json
                    if(!error)
                    {
                        Intent intent = new Intent(addpill.this, Tabs.class);
                        intent.putExtra("username", getIntent().getStringExtra("username"));
                        startActivity(intent);
                        finish();
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
                Log.e(TAG, "Enter Error: " + error.getMessage());
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
                params.put("pillname", pill);
                params.put("dosage", dosage);
                params.put("DOW", d);
                params.put("location", local);
                params.put("hour", hour);
                params.put("min", minute);
                params.put("sec", second);
                params.put("addinfo", ai);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void boxnumbers(final String username)
    {
        final String tag_string_req = "req_registration";

        pDialog.setMessage("Submitting request ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SETTINGS, new Response.Listener<String>()
        {

            @Override
            public void onResponse(String response)
            {
                Log.d(TAG, "Submission Response: " + response.toString());
                hideDialog();

                try{
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    //checks error node in json
                    if(!error)
                    {
                        JSONArray pills = jObj.getJSONArray("Settings");
                        JSONObject c = pills.getJSONObject(0);

                        nums = Integer.parseInt(c.getString("num"));
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
                Log.e(TAG, "Enter Error: " + error.getMessage());
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
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    class boxcheck extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            HashMap<String,String > params = new HashMap<>();
            params.put("username",args[0]);

            Log.d("request", "starting");
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(AppConfig.URL_DISTINCT, "POST", params);

            Log.d("All Products: ", json.toString());

            try{

                boolean error = json.getBoolean("error");

                //checks error node in json
                if(!error)
                {
                    pills = json.getJSONArray("Pill");
                    for (int i = 0; i < pills.length(); i++)
                    {
                        JSONObject c = pills.getJSONObject(i);
                        String pillname = c.getString("pill_name");
                        String dosage = c.getString("dosage");
                        String position = c.getString("position");

                        if(args[1].equals(position)){
                            if(args[2].equals(pillname)){
                                checkers = true;
                            }
                        }

                    }
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }

            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                }
            });
        }
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