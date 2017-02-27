package android.pillcentral;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class pilldisplay extends AppCompatActivity {
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilldisplay);
        btn = (Button) findViewById(R.id.button);
    }

    public void onclick(View view)
    {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
    }
}
