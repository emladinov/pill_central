package android.pillcentral;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.AppConfig;

public class systemsettings extends AppCompatActivity {

    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> pillList;
    HashMap<String, String> map;
    JSONParser jParser = new JSONParser();
    JSONArray pills = null;

    private EditText num;
    private EditText id;
    private EditText alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_systemsettings);


        num = (EditText) findViewById(R.id.boxes);
        id = (EditText) findViewById(R.id.boxid);
        alert = (EditText) findViewById(R.id.alerttime);

        pillList = new ArrayList<HashMap<String, String>>();

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        new LoadSettings().execute(getIntent().getStringExtra("username"));

    }


    class LoadSettings extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {

            HashMap<String,String > params = new HashMap<>();
            params.put("username",args[0]);

            Log.d("request", "starting");
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(AppConfig.URL_SETTINGS, "POST", params);

            Log.d("All Products: ", json.toString());

            try{

                boolean error = json.getBoolean("error");

                //checks error node in json
                if(!error)
                {
                    pills = json.getJSONArray("Settings");
                    JSONObject c = pills.getJSONObject(0);

                    String boxnum = c.getString("num");
                    String boxid = c.getString("ID");
                    String alerttime = c.getString("alert");

                    //create a new HashMap
                    map = new HashMap<String, String>();

                    map.put("boxnum", boxnum);
                    map.put("boxid", boxid);
                    map.put("alert", alerttime);

                    pillList.add(map);


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
                    for(int i=0;i<pillList.size();i++) {
                        try {
                            HashMap<String, String> test = pillList.get(i);
                            if(test.get("boxnum") != null) {
                                num.setText(test.get("boxnum"));
                            }else
                            {
                                num.setText("error");
                            }
                            if(test.get("boxid") != null) {
                                id.setText("0000" + test.get("boxid"));
                            }else
                            {
                                id.setText("error");
                            }
                            if(test.get("alert") != null) {
                                alert.setText(test.get("alert"));
                            }else
                            {
                                alert.setText("error");
                            }

                        }catch(Exception e){
                        }
                    }
                }
            });
        }
    }
}
