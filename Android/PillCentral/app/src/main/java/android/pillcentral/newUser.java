package android.pillcentral;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
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

public class newUser extends AppCompatActivity {
    private static final String TAG = Login.class.getSimpleName();
    private Button sbmt;
    private EditText usrnm;
    private EditText psswrd;
    private EditText eml;
    private Switch swpass;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        sbmt = (Button) findViewById(R.id.submit);
        usrnm = (EditText) findViewById(R.id.username);
        psswrd = (EditText) findViewById(R.id.password);
        eml = (EditText) findViewById(R.id.email);
        swpass = (Switch) findViewById(R.id.showpass);

        //Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        swpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    psswrd.setTransformationMethod(null);
                }else
                {
                    psswrd.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        sbmt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean at  = false;
                String username = usrnm.getText().toString();
                String password = psswrd.getText().toString();
                String email = eml.getText().toString();
                if(!username.isEmpty() && !password.isEmpty() && !email.isEmpty())
                {
                    for(int i=0; i<email.length();i++)
                    {
                        if(email.charAt(i) == '@')
                        {
                            at = !at;
                        }
                    }
                    if(at == true)
                    {
                        usersubmit(username, password, email);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Not a valid email address", Toast.LENGTH_LONG).show();
                    }
                }
                else if(username.isEmpty() && password.isEmpty() && email.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please enter username, password, & email", Toast.LENGTH_LONG).show();
                }
                else if (username.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please enter username", Toast.LENGTH_LONG).show();
                }
                else if(password.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void usersubmit(final String username, final String password, final String email)
    {
        final String tag_string_req = "req_registration";

        pDialog.setMessage("Submitting request ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>()
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
                        //Launch pilldisplay
                        Intent intent = new Intent(newUser.this, Login.class);
                        startActivity(intent);
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
                params.put("password", password);
                params.put("email", email);
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
