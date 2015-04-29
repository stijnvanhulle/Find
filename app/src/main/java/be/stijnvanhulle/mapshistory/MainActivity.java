package be.stijnvanhulle.mapshistory;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import be.stijnvanhulle.mapshistory.Models.Restaurant;
import be.stijnvanhulle.mapshistory.Models.Store;


public class MainActivity extends Activity implements StoreFragment.OnStartFragmentListener, RestaurantFragment.OnRestaurantFragmentListener {



    public static final String EXTRA_STORE ="store";
    public static final String EXTRA_RESTAURANT ="restaurant";

    public static final String ERROR ="error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, RestaurantFragment.newInstance())
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
        }else if(id==R.id.action_restaurants){
            getFragmentManager().popBackStack();
            setTitle("Restaurants");
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.container, RestaurantFragment.newInstance())
                    .addToBackStack(null)
                    .commit();

        } else if(id==R.id.action_stores){
            getFragmentManager().popBackStack();
            setTitle("Stores");
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.container, StoreFragment.newInstance())
                    .addToBackStack(null)
                    .commit();

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


    @Override
    public void changeGeo(Restaurant restaurant) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MapsFragment fragment = MapsFragment.newInstance(restaurant);
        fragmentTransaction
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
