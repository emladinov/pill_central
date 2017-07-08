package android.pillcentral;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.AppConfig;

public class distinctpills extends AppCompatActivity {
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> pillList;
    HashMap<String, String> map;
    JSONParser jParser = new JSONParser();
    JSONArray pills = null;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distinctpills);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        lv=(ListView) findViewById(R.id.ilist);
        pillList = new ArrayList<HashMap<String, String>>();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> item =(HashMap<String,String>) (lv.getItemAtPosition(position));
                Intent intent = new Intent(distinctpills.this, pillinfo.class);
                String pillname = item.get("pill_name");
                String dos = item.get("dosage");
                String time = item.get("time");
                String boxnum = item.get("position");
                intent.putExtra("pill", pillname);
                intent.putExtra("dos", dos);
                intent.putExtra("time", time);
                intent.putExtra("box", boxnum);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
            }
        });

        new LoadAllPills().execute(getIntent().getStringExtra("username"));
    }

    class LoadAllPills extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(distinctpills.this);
            pDialog.setMessage("Loading pills. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            //pDialog.show();
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
                        String time = c.getString("time");

                        String ampm = "AM";
                        String[] ttime = time.split(":");
                        if(Integer.parseInt(ttime[0]) ==12)
                        {
                            ampm = "PM";
                        }
                        else if(Integer.parseInt(ttime[0]) >11)
                        {
                            ttime[0] = String.valueOf(Integer.parseInt(ttime[0])-12);
                            ampm = "PM";
                        }
                        time = ttime[0]+ ":" + ttime[1];


                        //create a new HashMap
                        map = new HashMap<String, String >();

                        map.put("pill_name", pillname);
                        map.put("dosage", dosage + " mg");
                        map.put("position", position);
                        map.put("time", time + " " + ampm);

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

                    ListAdapter adapter = new SimpleAdapter(
                            distinctpills.this, pillList,
                            R.layout.singlepill, new String[]{
                            "pill_name", "dosage","position", "time"},
                            new int[]{ R.id.pillname, R.id.dosage, R.id.local, R.id.taketime});
                    lv.setAdapter(adapter);
                }
            });
        }
    }
}
