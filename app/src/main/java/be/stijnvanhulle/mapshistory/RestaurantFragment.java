package be.stijnvanhulle.mapshistory;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import be.stijnvanhulle.mapshistory.Loader.Contract;
import be.stijnvanhulle.mapshistory.Loader.RestaurantLoader;
import be.stijnvanhulle.mapshistory.Models.Restaurant;
import be.stijnvanhulle.mapshistory.Models.Store;
import flexjson.JSONDeserializer;

public class RestaurantFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
//extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> // laten communiceren met loadermanager

    RestaurantAdapter mAdapter;
    private OnRestaurantFragmentListener onRestaurantFragmentListener;

    private TextView tvError;

    private GoogleMap map;
    private MapView mapView;




    //verplicht
    public RestaurantFragment() {
    }

    public static RestaurantFragment newInstance() {
        RestaurantFragment fragment = new RestaurantFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onRestaurantFragmentListener = (OnRestaurantFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragment1Listener");
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_restaurant, container, false);

        tvError=(TextView) v.findViewById(R.id.Error);




        getMap(v, savedInstanceState);


        return v;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] columns = new String[]{
                Contract.RestaurantsColumns.COLUMN_NAAM,
                Contract.RestaurantsColumns.COLUMN_KEYWORDS
        };

        int[] viewIds = new int[]{R.id.Naam,R.id.Keywords};

        mAdapter = new RestaurantAdapter(getActivity(), R.layout.row_restaurants, null, columns, viewIds, 0);

        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this); //laten activeren

        if(tvError!=null){
            if(!this.isOnline()){
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("Geen Internet");
            }else{
                tvError.setVisibility(View.INVISIBLE);
            }

        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor cursor=mAdapter.getCursor();
        int iNaam=cursor.getColumnIndex(Contract.RestaurantsColumns.COLUMN_NAAM);
        int iDescription=cursor.getColumnIndex(Contract.RestaurantsColumns.COLUMN_DESCRIPTION);
        int iKeywords=cursor.getColumnIndex(Contract.RestaurantsColumns.COLUMN_KEYWORDS);
        int iGeo_x=cursor.getColumnIndex(Contract.StoresColumns.COLUMN_GEO_X);
        int iGeo_y=cursor.getColumnIndex(Contract.StoresColumns.COLUMN_GEO_Y);


        cursor.moveToPosition(position);

        String naam=cursor.getString(iNaam);
        String description=cursor.getString(iDescription);
        String keywords=cursor.getString(iKeywords);
        String geo_x=cursor.getString(iGeo_x);
        String geo_y=cursor.getString(iGeo_y);

        Restaurant restaurant= new Restaurant(naam,description,keywords,geo_x,geo_y);
        onRestaurantFragmentListener.changeGeo(restaurant);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new RestaurantLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        Restaurant.Restaurants= new ArrayList<Restaurant>();

        data.moveToFirst();
        while (data.moveToNext()){
            int iNaam=data.getColumnIndex(Contract.RestaurantsColumns.COLUMN_NAAM);
            int iDescription=data.getColumnIndex(Contract.RestaurantsColumns.COLUMN_DESCRIPTION);
            int iKeywords=data.getColumnIndex(Contract.RestaurantsColumns.COLUMN_KEYWORDS);
            int iGeo_x=data.getColumnIndex(Contract.RestaurantsColumns.COLUMN_GEO_X);
            int iGeo_y=data.getColumnIndex(Contract.RestaurantsColumns.COLUMN_GEO_Y);

            String naam=data.getString(iNaam);
            String description=data.getString(iDescription);
            String keywords=data.getString(iKeywords);
            String geo_x=data.getString(iGeo_x);
            String geo_y=data.getString(iGeo_y);


            Restaurant restaurant= new Restaurant(naam,description,keywords,geo_x,geo_y);



            Restaurant.Restaurants.add(restaurant);
        }

        LoadMap();
    }
    private void LoadMap() {
        if (Restaurant.Restaurants.size()> 0){


            for(int i=0;i<Restaurant.Restaurants.size();i++){
                Restaurant restaurant= Restaurant.Restaurants.get(i);

                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(restaurant.Geo_y), Double.parseDouble(restaurant.Geo_x)))
                        .title(restaurant.Naam)
                        .snippet("");
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                map.addMarker(marker);


                //kortijk coordianten laden
            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(50.8028051,3.279785)).zoom(10).build();
            map.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));


            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(50.8028051,3.279785), 10));


        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public class RestaurantAdapter extends SimpleCursorAdapter //visualiseren van de data afkomstig van de cursor
    {
        private int layout;

        public RestaurantAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            this.layout = layout;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView txtKeywords = (TextView) view.findViewById(R.id.Keywords);
            TextView txtName=(TextView) view.findViewById(R.id.Naam);

            int colnrKeywords= cursor.getColumnIndex(Contract.RestaurantsColumns.COLUMN_KEYWORDS);
            int colnrName=cursor.getColumnIndex(Contract.RestaurantsColumns.COLUMN_NAAM);

            String tekst=cursor.getString(colnrKeywords).toString();
            tekst=tekst.substring(1,tekst.length()-1);
            tekst=tekst.replaceAll("\"", "");

            txtKeywords.setText(tekst);
            txtName.setText(cursor.getString(colnrName).toString());
        }
    }

    private void getMap(View v,Bundle savedInstanceState) {
        mapView = (MapView) v.findViewById(R.id.mapView_restaurant_full);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());

        map = mapView.getMap();
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    //private OnStartFragmentListener onStartFragmentListener;

    //interface
    public interface OnRestaurantFragmentListener {
        public void changeGeo(Restaurant restaurant);
    }





}
