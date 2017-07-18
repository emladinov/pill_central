package android.pillcentral;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;

public class profile extends AppCompatActivity {

    private static final String TAG = profileconfig.class.getSimpleName();
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> pillList;
    HashMap<String, String> map;
    JSONParser jParser = new JSONParser();
    JSONArray pills = null;

    private EditText fname;
    private EditText lname;
    private EditText phone;
    private EditText dob;
    private RadioButton male;
    private RadioButton female;
    private MenuItem editcheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pillList = new ArrayList<HashMap<String, String>>();

        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        phone = (EditText) findViewById(R.id.phone);
        dob = (EditText) findViewById(R.id.dob);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);

        new getInfo().execute(getIntent().getStringExtra("username"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        getMenuInflater().inflate(R.menu.cancel, menu);
        editcheck = menu.findItem(R.id.action_edit);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_cancel:
                if (editcheck.getIcon().getConstantState().equals(
                        getResources().getDrawable(R.drawable.ic_menu_edit_dark).getConstantState())) {
                    Intent intent = new Intent(profile.this, Tabs.class);
                    intent.putExtra("username", getIntent().getStringExtra("username"));
                    startActivity(intent);
                    return true;
                } else {
                    editcheck.setIcon(R.drawable.ic_menu_edit_dark);
                    fname.setEnabled(false);
                    lname.setEnabled(false);
                    dob.setEnabled(false);
                    phone.setEnabled(false);
                    male.setEnabled(false);
                    female.setEnabled(false);
                    return true;
                }
            case R.id.action_edit:
                if (item.getIcon().getConstantState().equals(
                        getResources().getDrawable(R.drawable.ic_menu_check_dark).getConstantState())) {
                    String phonenum = "";
                    String date = "";
                    boolean check=true;
                    if (!dob.getText().toString().isEmpty()) {
                        try {
                            String[] datecheck = dob.getText().toString().split("/");
                            datecheck[0] = singletodouble(Integer.parseInt(datecheck[0]));
                            datecheck[1] = singletodouble(Integer.parseInt(datecheck[1]));
                            if (13 < Integer.parseInt(datecheck[0]) || Integer.parseInt(datecheck[0]) < 0) {
                                Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                                check = check && false;
                            } else if (datecheck[0] == "01" || datecheck[0] == "03" || datecheck[0] == "05" || datecheck[0] == "07" || datecheck[0] == "08" || datecheck[0] == "10" || datecheck[0] == "12") {
                                if (!thirtyonecheck(Integer.parseInt(datecheck[1]))) {
                                    Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                                    check = check && false;
                                }
                            } else if (datecheck[0] == "02") {
                                if (datecheck[2].length() == 4) {
                                    if (leapyearcheck(Integer.parseInt(datecheck[2]))) {
                                        if (!twentyninecheck(Integer.parseInt(datecheck[1]))) {
                                            Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                                            check = check && false;
                                        }
                                    } else {
                                        if (!twentyeightcheck(Integer.parseInt(datecheck[1]))) {
                                            Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                                            check = check && false;
                                        }
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                                    check = check && false;
                                }
                            } else {
                                if (!thirtycheck(Integer.parseInt(datecheck[1]))) {
                                    Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                                    check = check && false;
                                }
                                check = check && true;
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                            check = check && false;
                        }
                    }
                    if (phone.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Invalid phone number", Toast.LENGTH_LONG).show();
                        check = check && false;
                    }
                    if (!phone.getText().toString().isEmpty()) {
                        try {
                            String[] nums = phone.getText().toString().split("-");
                            phonenum = nums[0] + nums[1] + nums[2];
                            if(phonenum.length()!=10)
                            {
                                Toast.makeText(getApplicationContext(), "Invalid date of birth", Toast.LENGTH_LONG).show();
                                check = check && false;
                            }
                        }catch (Exception e){Toast.makeText(getApplicationContext(), "Invalid phone number", Toast.LENGTH_LONG).show();
                            check = check && false;}
                    }

                    if (!fname.getText().toString().isEmpty() && !lname.getText().toString().isEmpty() && phonenum.length() == 10 && !dob.getText().toString().isEmpty() && check) {
                        String[] datecheck = dob.getText().toString().split("/");
                        datecheck[0] = singletodouble(Integer.parseInt(datecheck[0]));
                        datecheck[1] = singletodouble(Integer.parseInt(datecheck[1]));
                        if (male.isChecked()) {
                            profileupdate(getIntent().getStringExtra("username"), fname.getText().toString(), lname.getText().toString(), phonenum, datecheck[2] + "-" + datecheck[0] + "-" + datecheck[1], "M");
                        } else {
                            profileupdate(getIntent().getStringExtra("username"), fname.getText().toString(), lname.getText().toString(), phonenum, datecheck[2] + "-" + datecheck[0] + "-" + datecheck[1], "F");
                        }
                        Intent intent = new Intent(profile.this, Tabs.class);
                        intent.putExtra("username", getIntent().getStringExtra("username"));
                        startActivity(intent);
                    }
                } else {
                    fname.setEnabled(true);
                    lname.setEnabled(true);
                    dob.setEnabled(true);
                    phone.setEnabled(true);
                    male.setEnabled(true);
                    female.setEnabled(true);
                    item.setIcon(R.drawable.ic_menu_check_dark);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean thirtyonecheck(int days) {
        if (days > 31 || 0 > days) {
            return false;
        }
        return true;
    }

    private boolean thirtycheck(int days) {
        if (days > 30 || 0 > days) {
            return false;
        }
        return true;
    }

    private boolean twentyeightcheck(int days) {
        if (days > 28 || 0 > days) {
            return false;
        }
        return true;
    }

    private boolean twentyninecheck(int days) {
        if (days > 29 || 0 > days) {
            return false;
        }
        return true;
    }

    private boolean leapyearcheck(int year) {
        if (year % 4 == 0) {
            if (year % 100 == 0) {
                if (year % 400 == 0) {
                    return true;
                }
                return false;
            }
            return true;
        }
        return false;
    }

    public String singletodouble(int single) {
        switch (single) {
            case 0:
                return "00";
            case 1:
                return "01";
            case 2:
                return "02";
            case 3:
                return "03";
            case 4:
                return "04";
            case 5:
                return "05";
            case 6:
                return "06";
            case 7:
                return "07";
            case 8:
                return "08";
            case 9:
                return "09";
            default:
                break;
        }
        return String.valueOf(single);
    }

    private void profileupdate(final String username, final String fname, final String lname, final String phone, final String DOB, final String gender)
    {
        final String tag_string_req = "req_registration";

        pDialog.setMessage("Submitting request ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SETTINGSUPDATE, new Response.Listener<String>()
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
                        Intent intent = new Intent(profile.this, Tabs.class);
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
                params.put("birth" , DOB);
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

    class getInfo extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(profile.this);
            pDialog.setCancelable(false);

        }

        protected String doInBackground(String... args) {

            HashMap<String, String> params = new HashMap<>();
            params.put("username", args[0]);

            Log.d("request", "starting");
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(AppConfig.URL_INFO, "POST", params);

            Log.d("All Products: ", json.toString());

            try {

                boolean error = json.getBoolean("error");

                //checks error node in json
                if (!error) {
                    pills = json.getJSONArray("info");
                    JSONObject c = pills.getJSONObject(0);

                    String first = c.getString("fname");
                    String last = c.getString("lname");
                    String phone = c.getString("phone");
                    String birth = c.getString("birth");
                    String gender = c.getString("gender");

                    //create a new HashMap
                    map = new HashMap<String, String>();

                    map.put("first", first);
                    map.put("last", last);
                    map.put("phone", phone);
                    map.put("birth", birth);
                    map.put("gender", gender);

                    pillList.add(map);
                }
            } catch (JSONException e) {
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
                    for (int i = 0; i < pillList.size(); i++) {
                        try {
                            HashMap<String, String> test = pillList.get(i);
                            if (test.get("first") != null) {
                                fname.setText(test.get("first"));
                            } else {

                            }
                            if (test.get("last") != null) {
                                lname.setText(test.get("last"));
                            } else {

                            }
                            if (test.get("phone") != null) {
                                phone.setText(test.get("phone").substring(0, 3) + "-" + test.get("phone").substring(3, 6) + "-" + test.get("phone").substring(6, 10));
                                //phone.setText(test.get("phone"));
                            } else {

                            }
                            if (test.get("birth") != null) {
                                String[] dates = test.get("birth").split("-");
                                dob.setText(dates[1] + "/" + dates[2] + "/" + dates[0]);
                            } else {

                            }
                            if (test.get("gender") != null) {
                                if (test.get("gender").equals("m") || test.get("gender").equals("M")) {
                                    male.toggle();
                                } else {
                                    female.toggle();
                                }
                            } else {

                            }
                        } catch (Exception e) {
                        }
                    }
                }
            });
        }
    }
}
