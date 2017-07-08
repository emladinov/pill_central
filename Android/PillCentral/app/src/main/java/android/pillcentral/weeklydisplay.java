package android.pillcentral;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import app.AppConfig;

public class weeklydisplay extends AppCompatActivity {
    private RadioGroup weekgroup;
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> pillList;
    HashMap<String, String> map;
    JSONParser jParser = new JSONParser();
    JSONArray pills = null;
    private ListView lv;
    private RadioButton SUN;
    private RadioButton MON;
    private RadioButton TUES;
    private RadioButton WED;
    private RadioButton THUR;
    private RadioButton FRI;
    private RadioButton SAT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weeklydisplay);
        weekgroup = (RadioGroup) findViewById(R.id.week_bar);
        pillList = new ArrayList<HashMap<String, String>>();
        lv=(ListView) findViewById(R.id.weeklv);
        SUN = (RadioButton) findViewById(R.id.SUN);
        MON = (RadioButton) findViewById(R.id.MON);
        TUES = (RadioButton) findViewById(R.id.TUES);
        WED = (RadioButton) findViewById(R.id.WED);
        THUR = (RadioButton) findViewById(R.id.THURS);
        FRI = (RadioButton) findViewById(R.id.FRI);
        SAT = (RadioButton) findViewById(R.id.SAT);

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK)-1;
        new LoadAllPills().execute( getIntent().getStringExtra("username"),String.valueOf(day));

        switch (day) {
            case 0:
                SUN.setChecked(true);
                break;
            case 1:
                MON.setChecked(true);
                break;
            case 2:
                TUES.setChecked(true);
                break;
            case 3:
                WED.setChecked(true);
                break;
            case 4:
                THUR.setChecked(true);
                break;
            case 5:
                //FRI.setChecked(true);
                FRI.toggle();
                break;
            case 6:
                SAT.setChecked(true);
                break;
        }



        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        weekgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.SUN:
                        pillList.clear();
                        new LoadAllPills().execute( getIntent().getStringExtra("username"),"0");
                        break;
                    case R.id.MON:
                        pillList.clear();
                        new LoadAllPills().execute( getIntent().getStringExtra("username"),"1");
                        break;
                    case R.id.TUES:
                        pillList.clear();
                        new LoadAllPills().execute( getIntent().getStringExtra("username"),"2");
                        break;
                    case R.id.WED:
                        pillList.clear();
                        new LoadAllPills().execute( getIntent().getStringExtra("username"),"3");
                        break;
                    case R.id.THURS:
                        pillList.clear();
                        new LoadAllPills().execute( getIntent().getStringExtra("username"),"4");
                        break;
                    case R.id.FRI:
                        pillList.clear();
                        new LoadAllPills().execute( getIntent().getStringExtra("username"),"5");
                        break;
                    case R.id.SAT:
                        pillList.clear();
                        new LoadAllPills().execute(getIntent().getStringExtra("username"),"6");
                        break;
                }
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> item =(HashMap<String,String>) (lv.getItemAtPosition(position));
                Intent intent = new Intent(weeklydisplay.this, pillinfo.class);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.returnback, menu);
        inflater.inflate(R.menu.daily, menu);
        inflater.inflate(R.menu.addpill, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_daily:
                Intent intent2 = new Intent(weeklydisplay.this, pilldisplay.class);
                intent2.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent2);
                return true;
            case R.id.action_add:
                Intent intent = new Intent(weeklydisplay.this, addpill.class);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
                return true;
            case R.id.action_goback:
                Intent intent3 = new Intent(weeklydisplay.this, homescreen.class);
                startActivity(intent3);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class LoadAllPills extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(weeklydisplay.this);
            pDialog.setMessage("Loading pills. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            //pDialog.show();
        }

        protected String doInBackground(String... args) {

            HashMap<String,String > params = new HashMap<>();
            params.put("username",args[0]);
            params.put("DOW",args[1]);

            Log.d("request", "starting");
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(AppConfig.URL_WEEK , "POST", params);

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
                            weeklydisplay.this, pillList,
                            R.layout.singlepill, new String[]{
                            "pill_name", "dosage","position", "time"},
                            new int[]{ R.id.pillname, R.id.dosage, R.id.local, R.id.taketime});
                    lv.setAdapter(adapter);
                }
            });
        }
    }
}