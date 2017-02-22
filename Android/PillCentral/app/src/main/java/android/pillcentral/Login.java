package android.pillcentral;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    private EditText editusername;
    private EditText editpassword;

    public static final String USER_NAME = "USERNAME";

    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editusername = (EditText) findViewById(R.id.editText);
        editpassword = (EditText) findViewById(R.id.editText2);
    }


    protected void onclick(View view) {
        String pass = editpassword.getText().toString();
        String user = editusername.getText().toString();

        login(user, pass);

    }

    private String login(String Username, String Password) {
        class LoginAsync extends AsyncTask<String, Void, String> {

            protected void onPreExecute(){
            }
            @Override
            protected String doInBackground(String... arg0) {
                try {
                    String username = (String) arg0[0];
                    String password = (String) arg0[1];
                    String link = "http://turing.manhattan.edu/~nwilk01/GetDatabasePassword.php?username=" + username;

                    URL url = new URL(link);
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(link));
                    HttpResponse response = client.execute(request);
                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(response.getEntity().getContent()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();


                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        return result;
    }
}






