package android.pillcentral;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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
    private static final String TAG = Login.class.getSimpleName();
    private SeekBar dow;
    private TextView dowtxt;
    private TextView timeset;
    private TextView ampm;
    private EditText pillname;
    private EditText dosage;
    private EditText location;
    private Button settime;
    private Button submit;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpill);

        dow = (SeekBar) findViewById(R.id.seekBar);
        settime = (Button) findViewById(R.id.settime);
        submit = (Button) findViewById(R.id.Submit);
        dosage = (EditText) findViewById(R.id.dosage);
        pillname = (EditText) findViewById(R.id.pilltxt);
        location = (EditText) findViewById(R.id.loc);
        timeset = (TextView) findViewById(R.id.timeset);
        ampm = (TextView) findViewById(R.id.AMPM);
        dowtxt = (TextView) findViewById(R.id.dayofweek);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour>12)
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

        dow.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress){
                    case 0: dowtxt.setText("Monday");
                        break;
                    case 1: dowtxt.setText("Tuesday");
                        break;
                    case 2: dowtxt.setText("Wednesday");
                        break;
                    case 3: dowtxt.setText("Thursday");
                        break;
                    case 4: dowtxt.setText("Friday");
                        break;
                    case 5: dowtxt.setText("Saturday");
                        break;
                    case 6: dowtxt.setText("Sunday");
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String pill = pillname.getText().toString();
                String dos = dosage.getText().toString();
                String local = location.getText().toString();
                String time = timeset.getText().toString();
                String DOW = dowtxt.getText().toString();
                String AMPM = ampm.getText().toString();
                int h, d = 0;
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

                    switch (DOW) {
                        case "Monday":
                            d = 1;
                            break;
                        case "Tuesday":
                            d = 2;
                            break;
                        case "Wednesday":
                            d = 3;
                            break;
                        case "Thursday":
                            d = 4;
                            break;
                        case "Friday":
                            d = 5;
                            break;
                        case "Saturday":
                            d = 6;
                            break;
                        case "Sunday":
                            d = 0;
                            break;
                    }

                    pillsubmit(getIntent().getStringExtra("username"), pill + " " + dos + "mg", String.valueOf(d), ttime[0], ttime[1], "00", local);
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
                Intent intent = new Intent(addpill.this, pilldisplay.class);
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
    private void pillsubmit(final String username, final String pill, final String d, final String hour, final String minute, final String second, final String local )
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
                        Intent intent = new Intent(addpill.this, pilldisplay.class);
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
                params.put("DOW", d);
                params.put("location", local);
                params.put("hour", hour);
                params.put("min", minute);
                params.put("sec", second);
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
