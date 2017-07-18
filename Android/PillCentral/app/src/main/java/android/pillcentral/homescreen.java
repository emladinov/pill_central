package android.pillcentral;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import app.AppConfig;


public class homescreen extends Fragment {

    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> pillList;
    HashMap<String, String> map;
    JSONParser jParser = new JSONParser();
    JSONArray pills = null;

    private TextView uname;
    private TextView pill;
    private TextView loc;
    private TextView time;
    private TextView dosage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout rootview = (RelativeLayout) inflater.inflate(R.layout.homescreen, container, false);

        pillList = new ArrayList<HashMap<String, String>>();

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        uname = (TextView) rootview.findViewById(R.id.uname);
        pill = (TextView) rootview.findViewById(R.id.pname);
        dosage = (TextView) rootview.findViewById(R.id.dosage);
        loc = (TextView) rootview.findViewById(R.id.local);
        time = (TextView) rootview.findViewById(R.id.ttime);


        final String username = getArguments().getString("username");
        Calendar cal = Calendar.getInstance();
        String hour = singletodouble(cal.get(Calendar.HOUR_OF_DAY));
        String minute = singletodouble(cal.get(Calendar.MINUTE));
        String sec = singletodouble(cal.get(Calendar.SECOND));
        int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
        String DOW = day + "";

        new LoadName().execute(username, hour, minute, sec, DOW);
        new LoadAllPills().execute(username, hour, minute, sec, DOW);

        return rootview;
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

    class LoadName extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
            JSONObject json = jParser.makeHttpRequest(AppConfig.URL_UNAME, "POST", params);

            Log.d("All Products: ", json.toString());

            try{

                boolean error = json.getBoolean("error");

                //checks error node in json
                if(!error)
                {
                    pills = json.getJSONArray("Name");
                    for (int i = 0; i < pills.length(); i++)
                    {
                        JSONObject c = pills.getJSONObject(i);
                        String pillname = c.getString("name");

                        //create a new HashMap
                        map = new HashMap<String, String >();

                        map.put("name", pillname);


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
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    for(int i=0;i<pillList.size();i++) {
                        try {
                            HashMap<String, String> test = pillList.get(i);
                            uname.setText("Hello " + test.get("name"));
                        }catch(Exception e){
                        }
                    }
                }
            });
        }
    }

    class LoadAllPills extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
            JSONObject json = jParser.makeHttpRequest(AppConfig.URL_PILLDISPLAY, "POST", params);

            Log.d("All Products: ", json.toString());

            try{

                boolean error = json.getBoolean("error");

                //checks error node in json
                if(!error)
                {
                    pills = json.getJSONArray("Pill");
                    JSONObject c = pills.getJSONObject(0);

                    String pillname = c.getString("pill_name");
                    String dosage = c.getString("dosage");
                    String position = c.getString("position");
                    String time = c.getString("time");

                    String ampm = "AM";
                    String[] ttime = time.split(":");
                    if (Integer.parseInt(ttime[0]) == 12) {
                        ampm = "PM";
                    } else if (Integer.parseInt(ttime[0]) > 11) {
                        ttime[0] = String.valueOf(Integer.parseInt(ttime[0]) - 12);
                        ampm = "PM";
                    }
                    time = ttime[0] + ":" + ttime[1];

                    //create a new HashMap
                    map = new HashMap<String, String>();

                    map.put("pill_name", pillname);
                    map.put("dosage", dosage + " mg");
                    map.put("position", position);
                    map.put("time", time + " " + ampm);


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
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    for(int i=0;i<pillList.size();i++) {
                        try {
                            HashMap<String, String> test = pillList.get(i);
                            if(test.get("pill_name") != null) {
                                pill.setText(test.get("pill_name"));
                            }else
                            {
                                pill.setText("No");
                            }
                            if(test.get("dosage") != null) {
                                dosage.setText(test.get("dosage"));
                            }else
                            {
                                dosage.setText("pills");
                            }
                            if(test.get("position") != null) {
                                loc.setText("Box " + test.get("position"));
                            }else
                            {
                                loc.setText("remaining");
                            }
                            if(test.get("time") != null) {
                                //time.setText(test.get("time"));
                                //time.setText());
                                countdown(test.get("time"));
                            }else
                            {
                                time.setText("today");
                            }
                        }catch(Exception e){
                        }
                    }
                }
            });
        }

        private void countdown(String pilltime){
            Calendar cal = Calendar.getInstance();
            String hour = singletodouble(cal.get(Calendar.HOUR_OF_DAY));
            String minute = singletodouble(cal.get(Calendar.MINUTE));
            String sec = singletodouble(cal.get(Calendar.SECOND));
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            final String[] timesplitter = pilltime.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
            if(timesplitter[3].equals(" PM")) {
                pilltime = String.valueOf(Integer.parseInt(timesplitter[0])+12) + ":" + timesplitter[2] + ":00";
            }
            else{
                pilltime = timesplitter[0] + ":" + timesplitter[2] + ":00";
            }
            try{
                Date ctime = format.parse(hour + ":" + minute + ":" + sec);
                Date ptime = format.parse(pilltime);
                long diff = ptime.getTime() - ctime.getTime();
                timer(diff);
            }catch (Exception e){time.setText("Error: " + e.getMessage());}

        }
    }

    private void timer(long mills) {
        new CountDownTimer(mills, 1000){
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                int Hours = (int) (millis/(1000 * 60 * 60));
                int Mins = (int) (millis/(1000*60)) % 60;
                int Secs = (int) (millis/1000)%60;
                time.setText(singletodouble(Hours) + ":" + singletodouble(Mins) + ":" + singletodouble(Secs));
            }

            public void onFinish() {

            }
        }.start();

    }
}
