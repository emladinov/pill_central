package android.pillcentral;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
//import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.AppConfig;
import app.AppController;


public class pilldisplay extends AppCompatActivity { //ListActivity
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> pillList;
    HashMap<String, String> map;
    JSONParser jParser = new JSONParser();
    JSONArray pills = null;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilldisplay);

        pillList = new ArrayList<HashMap<String, String>>();

        lv=(ListView) findViewById(R.id.list);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        String username = getIntent().getStringExtra("username");
        Calendar cal = Calendar.getInstance();
        String hour = singletodouble(cal.get(Calendar.HOUR_OF_DAY));
        String minute = singletodouble(cal.get(Calendar.MINUTE));
        String sec = singletodouble(cal.get(Calendar.SECOND));
        int day = cal.get(Calendar.DAY_OF_WEEK)-1;
        String DOW = day + "";

        new LoadAllPills().execute(username,hour, minute, sec ,DOW);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addpill, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(pilldisplay.this, addpill.class);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class LoadAllPills extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(pilldisplay.this);
            pDialog.setMessage("Loading pills. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            HashMap<String,String > params = new HashMap<>();
            params.put("username",args[0]);
            params.put("hour", args[1]);
            params.put("min", args[2]);
            params.put("sec", args[3]);
            params.put("DOW",args[4]);

            Log.d("request", "starting");
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(AppConfig.URL_PILLDISPLAY , "POST", params);

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
                        String position = c.getString("position");
                        String time = c.getString("time");

                        //create a new HashMap
                        map = new HashMap<String, String >();

                        map.put("pill_name", pillname);
                        map.put("position", position);
                        map.put("time", time);

                        pillList.add(map);
                    }
                    //finish();
                }else
                {
                    //String errorMsg = json.get("error_msg").toString();
                    //Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(),Toast.LENGTH_LONG).show();
            }

            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    ListAdapter adapter = new SimpleAdapter(
                            pilldisplay.this, pillList,
                            R.layout.singlepilldisplay, new String[]{
                            "pill_name", "position", "time"},
                            new int[]{ R.id.pillname, R.id.local, R.id.taketime});
                    // updating listview
                    //setListAdapter(adapter);
                    lv.setAdapter(adapter);
                }
            });
        }
    }
}
