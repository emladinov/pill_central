package android.pillcentral;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;

public class pillinfo extends AppCompatActivity {
    private static final String TAG = pillinfo.class.getSimpleName();
    private EditText pillname;
    private EditText dosage;
    private EditText boxnum;
    private EditText ai;
    private CheckBox SUN;
    private CheckBox MON;
    private CheckBox TUES;
    private CheckBox WED;
    private CheckBox THUR;
    private CheckBox FRI;
    private CheckBox SAT;
    private TextView AMPM;
    private TextView time;
    private Button settime;
    private Button del;
    private MenuItem editcheck;

    int nums = 0;
    boolean checkers = false;

    ArrayList<HashMap<String, String>> pillList;
    HashMap<String, String> map;
    JSONParser jParser = new JSONParser();
    JSONArray pills = null;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pillinfo);
        AMPM = (TextView) findViewById(R.id.ampm);
        time = (TextView) findViewById(R.id.ctime);
        SUN = (CheckBox) findViewById(R.id.sun);
        MON = (CheckBox) findViewById(R.id.mon);
        TUES = (CheckBox) findViewById(R.id.tues);
        WED = (CheckBox) findViewById(R.id.wed);
        THUR = (CheckBox) findViewById(R.id.thurs);
        FRI = (CheckBox) findViewById(R.id.fri);
        SAT = (CheckBox) findViewById(R.id.sat);
        settime = (Button) findViewById(R.id.timechange);
        del = (Button) findViewById(R.id.deletion);
        dosage = (EditText) findViewById(R.id.dosage);
        pillname = (EditText) findViewById(R.id.pillname);
        boxnum = (EditText) findViewById(R.id.boxnum);
        ai = (EditText) findViewById(R.id.pilladdinfo);

        pillList = new ArrayList<HashMap<String, String>>();

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        pillname.setText(getIntent().getStringExtra("pill"));
        dosage.setText(getIntent().getStringExtra("dos"));

        final String[] timesplitter = getIntent().getStringExtra("time").split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        time.setText(timesplitter[0]+ ":"+timesplitter[2]);
        AMPM.setText(timesplitter[3]);


        if(AMPM.getText().equals(" PM")){
            timesplitter[0] = String.valueOf(Integer.parseInt(timesplitter[0])+12);
        }
        boxnum.setText(getIntent().getStringExtra("box"));

        boxnumbers(getIntent().getStringExtra("username"));
        new finddays().execute(getIntent().getStringExtra("username"), getIntent().getStringExtra("pill"), timesplitter[0], timesplitter[2], "00", getIntent().getStringExtra("box"));
        new additionalinfo().execute(getIntent().getStringExtra("username"), getIntent().getStringExtra("pill"), timesplitter[0], timesplitter[2], "00", getIntent().getStringExtra("box"));

        settime.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(),"TimePicker");
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(pillinfo.this);
                builder.setMessage("Continuing will delete " + pillname.getText() + " for all days at " + time.getText() + " " + AMPM.getText())
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String[] dosagesplitter = getIntent().getStringExtra("dos").split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                                deletepills(getIntent().getStringExtra("username"), getIntent().getStringExtra("pill"), dosagesplitter[0], timesplitter[0], timesplitter[2], "00", getIntent().getStringExtra("box"));
                                Intent intent = new Intent(pillinfo.this, Tabs.class);
                                intent.putExtra("username", getIntent().getStringExtra("username"));
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        getMenuInflater().inflate(R.menu.cancel, menu);
        editcheck = menu.findItem(R.id.action_edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_cancel:
                if(editcheck.getIcon().getConstantState().equals(
                        getResources().getDrawable(R.drawable.ic_menu_edit_dark).getConstantState())) {
                    Intent intent = new Intent(pillinfo.this, Tabs.class);
                    intent.putExtra("username", getIntent().getStringExtra("username"));
                    startActivity(intent);
                    return true;
                }else{
                    pillname.setEnabled(false);
                    settime.setVisibility(View.INVISIBLE);
                    dosage.setEnabled(false);
                    boxnum.setEnabled(false);
                    MON.setEnabled(false);
                    TUES.setEnabled(false);
                    WED.setEnabled(false);
                    THUR.setEnabled(false);
                    FRI.setEnabled(false);
                    SAT.setEnabled(false);
                    SUN.setEnabled(false);
                    ai.setEnabled(false);
                    editcheck.setIcon(R.drawable.ic_menu_edit_dark);
                    return true;
                }
            case R.id.action_edit:
                if(item.getIcon().getConstantState().equals(
                        getResources().getDrawable(R.drawable.ic_menu_check_dark).getConstantState()))
                {
                    String[] dosagesplitter = getIntent().getStringExtra("dos").split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                    final String[] timesplitter = getIntent().getStringExtra("time").split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                    int h = Integer.parseInt(timesplitter[0]);
                    if (timesplitter[3].equals(" PM")) {
                        h = h + 12;
                        timesplitter[0] = String.valueOf(h);
                    } else {
                        timesplitter[0] = String.valueOf(h);
                    }

                    String pill = pillname.getText().toString();
                    String dos = dosage.getText().toString();
                    String local = boxnum.getText().toString();
                    String ctime = time.getText().toString();
                    String ampm = AMPM.getText().toString();

                    String[] ttime = null;
                    if (pill.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter a pill name", Toast.LENGTH_LONG).show();
                    } else if (dos.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter a dosage, if not stated enter 0", Toast.LENGTH_LONG).show();
                    } else if (local.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter a pill box location", Toast.LENGTH_LONG).show();
                    } else if (!pill.isEmpty() && !dos.isEmpty() && !local.isEmpty()) {
                        ttime = ctime.split(":");
                        h = Integer.parseInt(ttime[0]);
                        if (ampm.equals("PM") || ampm.equals(" PM")) {
                            h = h + 12;
                            ttime[0] = String.valueOf(h);
                        } else {
                            ttime[0] = String.valueOf(h);
                        }

                        new boxcheck().execute(getIntent().getStringExtra("username"), local, pill, dos);
                        if(Integer.parseInt(local) <= nums && Integer.parseInt(local) >0 && checkers) {
                            if (!SUN.isChecked() && !MON.isChecked() && !TUES.isChecked() && !WED.isChecked() && !THUR.isChecked() && !FRI.isChecked() && !SAT.isChecked()) {
                                Toast.makeText(getApplicationContext(), "Please choose at least 1 day of the week", Toast.LENGTH_LONG).show();
                            }
                            if (SUN.isChecked()) {
                                deletepills(getIntent().getStringExtra("username"), getIntent().getStringExtra("pill"), dosagesplitter[0], timesplitter[0], timesplitter[2], "00", getIntent().getStringExtra("box"));
                                pillsubmit(getIntent().getStringExtra("username"), pill, dos, "0", ttime[0], ttime[1], "00", local, ai.getText().toString());
                            }
                            if (MON.isChecked()) {
                                deletepills(getIntent().getStringExtra("username"), getIntent().getStringExtra("pill"), dosagesplitter[0], timesplitter[0], timesplitter[2], "00", getIntent().getStringExtra("box"));
                                pillsubmit(getIntent().getStringExtra("username"), pill, dos, "1", ttime[0], ttime[1], "00", local, ai.getText().toString());
                            }
                            if (TUES.isChecked()) {
                                deletepills(getIntent().getStringExtra("username"), getIntent().getStringExtra("pill"), dosagesplitter[0], timesplitter[0], timesplitter[2], "00", getIntent().getStringExtra("box"));
                                pillsubmit(getIntent().getStringExtra("username"), pill, dos, "2", ttime[0], ttime[1], "00", local, ai.getText().toString());
                            }
                            if (WED.isChecked()) {
                                deletepills(getIntent().getStringExtra("username"), getIntent().getStringExtra("pill"), dosagesplitter[0], timesplitter[0], timesplitter[2], "00", getIntent().getStringExtra("box"));
                                pillsubmit(getIntent().getStringExtra("username"), pill, dos, "3", ttime[0], ttime[1], "00", local, ai.getText().toString());
                            }
                            if (THUR.isChecked()) {
                                deletepills(getIntent().getStringExtra("username"), getIntent().getStringExtra("pill"), dosagesplitter[0], timesplitter[0], timesplitter[2], "00", getIntent().getStringExtra("box"));
                                pillsubmit(getIntent().getStringExtra("username"), pill, dos, "4", ttime[0], ttime[1], "00", local, ai.getText().toString());
                            }
                            if (FRI.isChecked()) {
                                deletepills(getIntent().getStringExtra("username"), getIntent().getStringExtra("pill"), dosagesplitter[0], timesplitter[0], timesplitter[2], "00", getIntent().getStringExtra("box"));
                                pillsubmit(getIntent().getStringExtra("username"), pill, dos, "5", ttime[0], ttime[1], "00", local, ai.getText().toString());
                            }
                            if (SAT.isChecked()) {
                                deletepills(getIntent().getStringExtra("username"), getIntent().getStringExtra("pill"), dosagesplitter[0], timesplitter[0], timesplitter[2], "00", getIntent().getStringExtra("box"));
                                pillsubmit(getIntent().getStringExtra("username"), pill, dos, "6", ttime[0], ttime[1], "00", local, ai.getText().toString());
                            }
                        }else if (Integer.parseInt(local) > nums || Integer.parseInt(local) <= 0) {
                            Toast.makeText(getApplicationContext(), "Box does not exist", Toast.LENGTH_LONG).show();

                        }else {
                            Toast.makeText(getApplicationContext(), "Box already taken by another medication", Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    pillname.setEnabled(true);
                    settime.setVisibility(View.VISIBLE);
                    dosage.setEnabled(true);
                    boxnum.setEnabled(true);
                    MON.setEnabled(true);
                    TUES.setEnabled(true);
                    WED.setEnabled(true);
                    THUR.setEnabled(true);
                    FRI.setEnabled(true);
                    SAT.setEnabled(true);
                    SUN.setEnabled(true);
                    ai.setEnabled(true);
                    item.setIcon(R.drawable.ic_menu_check_dark);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deletepills(final String username, final String name, final String dosage, final String hour, final String minute, final String sec, final String box)
    {
        final String tag_string_req = "req_registration";

        pDialog.setMessage("Submitting request ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_DELETE, new Response.Listener<String>()
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
                params.put("pillname", name);
                params.put("dosage", dosage);
                params.put("location", box);
                params.put("hour", hour);
                params.put("min", minute);
                params.put("sec", sec);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
                        Intent intent = new Intent(pillinfo.this, Tabs.class);
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

    class finddays extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(pillinfo.this);
            pDialog.setMessage("Loading pill. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            HashMap<String,String > params = new HashMap<>();
            params.put("username", args[0]);
            params.put("pillname", args[1]);
            params.put("location", args[5]);
            params.put("hour", args[2]);
            params.put("min", args[3]);
            params.put("sec", args[4]);

            Log.d("request", "starting");
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(AppConfig.URL_PILLINFO , "POST", params);

            Log.d("All Products: ", json.toString());

            try{

                boolean error = json.getBoolean("error");

                //checks error node in json
                if(!error) {
                    pills = json.getJSONArray("DOW");
                    for (int i = 0; i < pills.length(); i++) {
                        JSONObject c = pills.getJSONObject(i);
                        String DOW = c.getString("day");

                        //create a new HashMap
                        map = new HashMap<String, String>();

                        map.put("day", DOW);

                        pillList.add(map);
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
                    for(int i=0;i<pillList.size();i++)
                    {
                        try {
                            HashMap<String, String> test = pillList.get(i);
                            if (Integer.parseInt(test.get("day")) == 0) {
                                SUN.setChecked(true);
                            }
                            if (Integer.parseInt(test.get("day")) == 1) {
                                MON.setChecked(true);
                            }
                            if (Integer.parseInt(test.get("day")) == 2) {
                                TUES.setChecked(true);
                            }
                            if (Integer.parseInt(test.get("day")) == 3) {
                                WED.setChecked(true);
                            }
                            if (Integer.parseInt(test.get("day")) == 4) {
                                THUR.setChecked(true);
                            }
                            if (Integer.parseInt(test.get("day")) == 5) {
                                FRI.setChecked(true);
                            }
                            if (Integer.parseInt(test.get("day")) == 6) {
                                SAT.setChecked(true);
                            }
                        }catch(Exception e){
                        }
                    }
                }
            });
        }
    }

    class additionalinfo extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            HashMap<String,String > params = new HashMap<>();
            params.put("username", args[0]);
            params.put("pillname", args[1]);
            params.put("location", args[5]);
            params.put("hour", args[2]);
            params.put("min", args[3]);
            params.put("sec", args[4]);

            Log.d("request", "starting");
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(AppConfig.URL_ADDITIONAL , "POST", params);

            Log.d("All Products: ", json.toString());

            try{

                boolean error = json.getBoolean("error");

                //checks error node in json
                if(!error) {
                    pills = json.getJSONArray("info");
                    for (int i = 0; i < pills.length(); i++) {
                        JSONObject c = pills.getJSONObject(i);
                        String addinfo = c.getString("addish");

                        //create a new HashMap
                        map = new HashMap<String, String>();

                        map.put("info", addinfo);

                        pillList.add(map);
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
                    for(int i=0;i<pillList.size();i++)
                    {
                        try {
                            HashMap<String, String> test = pillList.get(i);
                            ai.setText(test.get("info"));
                        }catch(Exception e){
                        }
                    }
                }
            });
        }
    }

    private void boxnumbers(final String username)
    {
        final String tag_string_req = "req_registration";

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
}


