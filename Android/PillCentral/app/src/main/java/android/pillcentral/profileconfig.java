package android.pillcentral;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;


public class profileconfig extends AppCompatActivity {
    private static final String TAG = profileconfig.class.getSimpleName();
    private EditText first;
    private EditText last;
    private EditText phonenum;
    private EditText DOB;
    private RadioButton male;
    private RadioButton female;
    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileconfig);

        first = (EditText) findViewById(R.id.fname);
        last = (EditText) findViewById(R.id.lname);
        phonenum = (EditText) findViewById(R.id.textView29);
        DOB = (EditText) findViewById(R.id.DOB);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        male.toggle();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forward, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_forward:
                String phone="";
                String[] datecheck= null;
                boolean check=true;
                if(DOB.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                    check = check && false;
                }
                else{
                    String birth = DOB.getText().toString();
                    datecheck = birth.split("/");
                    datecheck[0] = singletodouble(Integer.parseInt(datecheck[0]));
                    datecheck[1] = singletodouble(Integer.parseInt(datecheck[1]));
                    if(13< Integer.parseInt(datecheck[0]) || Integer.parseInt(datecheck[0]) < 0){
                        Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                        check = check && false;
                    }
                    else if(datecheck[0] == "01" || datecheck[0] == "03" || datecheck[0] == "05" || datecheck[0] == "07" || datecheck[0] == "08" || datecheck[0] == "10" || datecheck[0] == "12"){
                        if(!thirtyonecheck(Integer.parseInt(datecheck[1]))){
                            Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                            check = check && false;
                        }
                    }
                    else if(datecheck[0] == "02"){
                        if(datecheck[2].length() ==4)
                        {
                            if(leapyearcheck(Integer.parseInt(datecheck[2]))){
                                if(!twentyninecheck(Integer.parseInt(datecheck[1])))
                                {
                                    Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                                    check = check && false;
                                }
                            }
                            else{
                                if(!twentyeightcheck(Integer.parseInt(datecheck[1]))){
                                    Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                                    check = check && false;
                                }
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                            check = check && false;
                        }
                    }
                    else{
                        if(!thirtycheck(Integer.parseInt(datecheck[1]))){
                            Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                            check = check && false;
                        }
                    }
                }

                if (!phonenum.getText().toString().isEmpty()) {
                    try {
                        String[] nums = phonenum.getText().toString().split("-");
                        phone = nums[0] + nums[1] + nums[2];
                        if(phonenum.length()!=10)
                        {
                            Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                            check = check && false;
                        }
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Invalid phone number", Toast.LENGTH_LONG).show();
                        check = check && false;
                    }
                }
                if(!first.getText().toString().isEmpty() && !last.getText().toString().isEmpty() && check) {
                    if (male.isChecked()) {
                        profile(getIntent().getStringExtra("username"), first.getText().toString(), last.getText().toString(), phone, datecheck[2] + "-" + datecheck[0] + "-" + datecheck[1], "M");
                    } else {
                        profile(getIntent().getStringExtra("username"), first.getText().toString(), last.getText().toString(), phone, datecheck[2] + "-" + datecheck[0] + "-" + datecheck[1], "F");
                    }
                    Intent intent = new Intent(profileconfig.this, Tabs.class);
                    intent.putExtra("username", getIntent().getStringExtra("username"));
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please enter name", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean thirtyonecheck(int days){
        if(days>31 || 0> days)
        {
            return false;
        }
        return true;
    }

    private boolean thirtycheck(int days){
        if(days>30 || 0> days)
        {
            return false;
        }
        return true;
    }

    private boolean twentyeightcheck(int days){
        if(days>28 || 0> days)
        {
            return false;
        }
        return true;
    }

    private boolean twentyninecheck(int days){
        if(days>29 || 0> days)
        {
            return false;
        }
        return true;
    }

    private boolean leapyearcheck(int year)
    {
        if(year%4==0)
        {
            if(year %100 ==0) {
                if(year%400==0){
                    return true;
                }
                return false;
            }
            return true;
        }
        return false;
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

    private void profile(final String username, final String fname, final String lname, final String phone, final String DOB, final String gender)
    {
        final String tag_string_req = "req_registration";

        pDialog.setMessage("Submitting request ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_USERINFO, new Response.Listener<String>()
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
                        Intent intent = new Intent(profileconfig.this, Tabs.class);
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
                params.put("fname", fname);
                params.put("lname", lname);
                params.put("phone" , phone);
                params.put("DOB" , DOB);
                //params.put("month", DOB[0]);
                //params.put("day", DOB[1]);
                //params.put("year", DOB[2]);
                params.put("gender", gender);
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

