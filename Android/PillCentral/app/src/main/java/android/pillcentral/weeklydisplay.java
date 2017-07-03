package android.pillcentral;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class weeklydisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weeklydisplay);


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
                Intent intent3 = new Intent(weeklydisplay.this, Login.class);
                startActivity(intent3);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
