package be.stijnvanhulle.mapshistory;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import be.stijnvanhulle.mapshistory.Loader.Contract;
import be.stijnvanhulle.mapshistory.Loader.StoresLoader;
import be.stijnvanhulle.mapshistory.Models.Store;


public class StoreFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    StoresAdapter mAdapter;
    private OnStartFragmentListener onStartFragmentListener;
    private Button btnSubmit;
    private TextView tvError;

    private GoogleMap map;
    private MapView mapView;




    public static StoreFragment newInstance() {
        StoreFragment fragment = new StoreFragment();

        return fragment;
    }

    //verplicht
    public StoreFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onStartFragmentListener = (OnStartFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragment1Listener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_store, container, false);





        tvError=(TextView) v.findViewById(R.id.Error);


        getMap(v, savedInstanceState);



        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] columns = new String[]{
                Contract.StoresColumns.COLUMN_BEDRIJFSNAAM,
                Contract.StoresColumns.COLUMN_ADRES,
                Contract.StoresColumns.COLUMN_GEMEENTE

        };

        int[] viewIds = new int[]{R.id.Bedrijfsnaam, R.id.Address};

        mAdapter = new StoresAdapter(getActivity(), R.layout.row_stores, null, columns, viewIds, 0);

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
        int iBedrijfsnaam=cursor.getColumnIndex(Contract.StoresColumns.COLUMN_BEDRIJFSNAAM);
        int iAdres=cursor.getColumnIndex(Contract.StoresColumns.COLUMN_ADRES);
        int iGemeente=cursor.getColumnIndex(Contract.StoresColumns.COLUMN_GEMEENTE);
        int iGeo_x=cursor.getColumnIndex(Contract.StoresColumns.COLUMN_GEO_X);
        int iGeo_y=cursor.getColumnIndex(Contract.StoresColumns.COLUMN_GEO_Y);

        String bedrijfsnaam=cursor.getString(iBedrijfsnaam);
        String adres=cursor.getString(iAdres);
        String gemeente=cursor.getString(iGemeente);
        String geo_x=cursor.getString(iGeo_x);
        String geo_y=cursor.getString(iGeo_y);

        Store store= new Store(bedrijfsnaam,adres,gemeente,geo_x,geo_y);
        onStartFragmentListener.changeGeo(store);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new StoresLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        Store.Stores= new ArrayList<Store>();

        data.moveToFirst();
        while (data.moveToNext()){
            int iBedrijfsnaam=data.getColumnIndex(Contract.StoresColumns.COLUMN_BEDRIJFSNAAM);
            int iAdres=data.getColumnIndex(Contract.StoresColumns.COLUMN_ADRES);
            int iGemeente=data.getColumnIndex(Contract.StoresColumns.COLUMN_GEMEENTE);
            int iGeo_x=data.getColumnIndex(Contract.StoresColumns.COLUMN_GEO_X);
            int iGeo_y=data.getColumnIndex(Contract.StoresColumns.COLUMN_GEO_Y);

            String bedrijfsnaam=data.getString(iBedrijfsnaam);
            String adres=data.getString(iAdres);
            String gemeente=data.getString(iGemeente);
            String geo_x=data.getString(iGeo_x);
            String geo_y=data.getString(iGeo_y);

            Store store= new Store(bedrijfsnaam,adres,gemeente,geo_x,geo_y);



            Store.Stores.add(store);
        }

        LoadMap();

    }

    private void LoadMap() {
        if (Store.Stores.size()> 0){


            for(int i=0;i<Store.Stores.size();i++){
                Store store= Store.Stores.get(i);

                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(store.Geo_y), Double.parseDouble(store.Geo_x)))
                        .title(store.Bedrijfsnaam)
                        .snippet(store.Adres);
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



    public class StoresAdapter extends SimpleCursorAdapter //visualiseren van de data afkomstig van de cursor
    {
        private int layout;

        public StoresAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            this.layout = layout;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            View row = view;

            TextView Address = (TextView) row.findViewById(R.id.Address);
            TextView Bedrijfsnaam = (TextView) row.findViewById(R.id.Bedrijfsnaam);
            TextView Gemeente = (TextView) row.findViewById(R.id.Gemeente);

            int colnrAddress = cursor.getColumnIndex(Contract.StoresColumns.COLUMN_ADRES);
            int colnrBedrijfsnaam = cursor.getColumnIndex(Contract.StoresColumns.COLUMN_BEDRIJFSNAAM);
            int colnrGemeente = cursor.getColumnIndex(Contract.StoresColumns.COLUMN_GEMEENTE);

            Address.setText(cursor.getString(colnrAddress));
            Bedrijfsnaam.setText(cursor.getString(colnrBedrijfsnaam));
            Gemeente.setText(cursor.getString(colnrGemeente));


        }




    }

    private void getMap(View v,Bundle savedInstanceState) {
        mapView = (MapView) v.findViewById(R.id.mapView_full);
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

    /*

    Interface
     */
    //private OnStartFragmentListener onStartFragmentListener;

    //interface
    public interface OnStartFragmentListener {
        public void changeGeo(Store store);
    }

}
