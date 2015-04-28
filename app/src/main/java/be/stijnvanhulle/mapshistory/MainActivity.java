package be.stijnvanhulle.mapshistory;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import be.stijnvanhulle.mapshistory.Models.Store;


public class MainActivity extends Activity implements StoreFragment.OnStartFragmentListener {



    public static final String EXTRA_STORE ="cursor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, StoreFragment.newInstance())
                    .commit();
        }

    }

    //kijken voor stores + restaurants aan de hand van keywoards
    //bij elke fragment bovenaan mapje met alle stores/restaurants



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void changeGeo(Store store) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MapsFragment fragment = MapsFragment.newInstance(store);
        fragmentTransaction
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
