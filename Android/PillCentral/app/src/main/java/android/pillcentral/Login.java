package android.pillcentral;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.widget.Toast;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;



public class Login extends AppCompatActivity {
    private static final String TAG = Login.class.getSimpleName();
    private EditText inputusername;
    private EditText inputpassword;
    private Button btnLogin;
    private ProgressDialog pDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //register button and input boxes
        inputusername = (EditText) findViewById(R.id.editText);
        inputpassword = (EditText) findViewById(R.id.editText2);
        btnLogin = (Button) findViewById(R.id.button);

        //Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);






        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String username = inputusername.getText().toString();
                String password = inputpassword.getText().toString();

                if (!username.isEmpty() && !password.isEmpty()) {
                    checkLogin(username, password);
                } else if (username.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter username", Toast.LENGTH_LONG).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter username & password", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    private void checkLogin(final String username, final String password)
    {
        final String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>()
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


                        //Launch pilldisplay
                        Intent intent = new Intent(Login.this, pilldisplay.class);
                        startActivity(intent);
                        error=true;
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
            params.put("password", password);

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









