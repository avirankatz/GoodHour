package avirankatz.goodhour.goodhourprefs;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import avirankatz.goodhour.R;
import avirankatz.goodhour.helpers.GlobalMethods;

public class MainActivity extends AppCompatActivity {

    AlarmsAdapter adapter;
    ListView goodHoursView;
    ArrayList<GoodHour> hours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        hours = GlobalMethods.loadGoodHoursFromFile(getApplicationContext());
        adapter = new AlarmsAdapter(
                this,
                R.layout.good_hours_list_item,
                R.id.good_hours_list_item_time,
                hours
        );

        goodHoursView = findViewById(R.id.good_hours_list);
        goodHoursView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.add(new GoodHour());
                GlobalMethods.saveGoodHoursToFile(getApplicationContext(), hours);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
