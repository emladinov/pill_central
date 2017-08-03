package android.pillcentral;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.AppConfig;

public class settings extends Fragment {

    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> pillList;
    HashMap<String, String> map;
    JSONParser jParser = new JSONParser();
    JSONArray pills = null;

    private ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout rootview = (RelativeLayout) inflater.inflate(R.layout.settings, container, false);

        pillList = new ArrayList<HashMap<String, String>>();
        lv = (ListView) rootview.findViewById(R.id.list);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        new LoadSettings().execute(getArguments().getString("username"));
        new LoadUserInfo().execute(getArguments().getString("username"));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> item = (HashMap<String, String>) (lv.getItemAtPosition(position));

                if(item.get("title").equals("Notify before take time") || item.get("title").equals("Number of boxes") || item.get("title").equals("Box ID")) {
                    Intent intent = new Intent(getActivity(), systemsettings.class);
                    intent.putExtra("username", getArguments().getString("username"));
                    startActivity(intent);
                }else
                {
                    Intent intent = new Intent(getActivity(), profile.class);
                    intent.putExtra("username", getArguments().getString("username"));
                    startActivity(intent);
                }
            }
        });

        return rootview;
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
                    map.put("title", "Number of boxes");
                    map.put("item", boxnum);
                    pillList.add(map);

                    map = new HashMap<String, String>();
                    map.put("title", "Box ID");
                    map.put("item", boxid);
                    pillList.add(map);

                    map = new HashMap<String, String>();
                    map.put("title", "Notify before take time");
                    map.put("item", alerttime + " minutes");
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
                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(),pillList,
                            R.layout.singlesettings, new String[]{
                            "title", "item"}, new int[] {R.id.itemtitle, R.id.itemselection});
                    lv.setAdapter(adapter);
                }
            });
        }
    }

    class LoadUserInfo extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
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
                    map.put("title", "Name");
                    map.put("item", first + " " +last);
                    pillList.add(map);

                    map = new HashMap<String, String>();
                    map.put("title", "Phone Number");
                    phone = phone.substring(0, 3) + "-" + phone.substring(3, 6) + "-" + phone.substring(6, 10);
                    map.put("item", phone);
                    pillList.add(map);

                    map = new HashMap<String, String>();
                    map.put("title", "Date of Birth");
                    String[] dates = birth.split("-");
                    birth = dates[1] + "/" + dates[2] + "/" + dates[0];
                    map.put("item", birth);
                    pillList.add(map);

                    map = new HashMap<String, String>();
                    map.put("title", "Gender");
                    if(gender.equals("m")) {
                        map.put("item", "Male");
                    }
                    else{
                        map.put("item", "Female");
                    }
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
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(),pillList,
                            R.layout.singlesettings, new String[]{
                            "title", "item"}, new int[] {R.id.itemtitle, R.id.itemselection});
                    lv.setAdapter(adapter);
                }
            });
        }
    }
}
