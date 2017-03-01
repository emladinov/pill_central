package android.pillcentral;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;


public class pilldisplay extends AppCompatActivity {
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilldisplay);
    }


    public class PillAdapter extends ArrayAdapter<pilldisplay> {
        private final Context context;
        private final ArrayList<pilldisplay> data;
        private final int layoutResourceId;

        public PillAdapter(Context context, int layoutResourceId, ArrayList<pilldisplay> data) {
            super(context, layoutResourceId, data);
            this.context = context;
            this.data = data;
            this.layoutResourceId = layoutResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;

            if(row == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new ViewHolder();
                holder.textView1 = (TextView)row.findViewById(R.id.pillname);
                holder.textView2 = (TextView)row.findViewById(R.id.local);
                holder.textView3 = (TextView)row.findViewById(R.id.taketime);
                row.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)row.getTag();
            }

            pilldisplay Pilldisplay = data.get(position);

            holder.textView1.setText(Pilldisplay.ge);
            holder.textView2.setText(Pilldisplay.getAddress());
            holder.textView3.setText(Pilldisplay.getEtc());

            return row;
        }

        class ViewHolder //possible static
        {
            TextView textView1;
            TextView textView2;
            TextView textView3;
        }
    }
}
