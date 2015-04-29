package be.stijnvanhulle.mapshistory;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import be.stijnvanhulle.mapshistory.Models.Restaurant;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import be.stijnvanhulle.mapshistory.Models.Store;

public class MapsFragment extends Fragment {

    private LatLng PLACE;
    private GoogleMap map;
    private MapView mapView;


    private Store mStore;
    private Restaurant mRestaurant;

    public static MapsFragment newInstance(Store store) {
        MapsFragment fragment = new MapsFragment();

        Bundle bundle = new Bundle();
        JSONSerializer ser = new JSONSerializer();
        bundle.putString(MainActivity.EXTRA_STORE, ser.deepSerialize(store));// to json format
        fragment.setArguments(bundle);

        return fragment;
    }

    public static MapsFragment newInstance(Restaurant restaurant) {
        MapsFragment fragment = new MapsFragment();

        Bundle bundle = new Bundle();
        JSONSerializer ser = new JSONSerializer();
        bundle.putString(MainActivity.EXTRA_RESTAURANT, ser.deepSerialize(restaurant));// to json format
        fragment.setArguments(bundle);

        return fragment;
    }

    //verplicht
    public MapsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getActivity() != null)
        {
            Bundle bundle = getArguments();

            String s = bundle.getString(MainActivity.EXTRA_STORE);
            String r = bundle.getString(MainActivity.EXTRA_RESTAURANT);

            if (s !=null){
                JSONDeserializer<Store> der = new JSONDeserializer<Store>();
                mStore = der.deserialize(s);

                PLACE = new LatLng(Double.parseDouble(mStore.Geo_y), Double.parseDouble(mStore.Geo_x));
            }else{
                JSONDeserializer<Restaurant> der = new JSONDeserializer<Restaurant>();
                mRestaurant = der.deserialize(r);

                PLACE = new LatLng(Double.parseDouble(mRestaurant.Geo_y), Double.parseDouble(mRestaurant.Geo_x));
            }




        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_maps, container, false);

        getMap(v,savedInstanceState);



        MarkerOptions marker;

        //controle restaurant of store
        if (mStore!=null){
            marker = new MarkerOptions()
                    .position(PLACE)
                    .title(mStore.Bedrijfsnaam)
                    .snippet(mStore.Adres);
        }else{
            marker = new MarkerOptions()
                    .position(PLACE)
                    .title(mRestaurant.Naam)
                    .snippet(mRestaurant.Description);
        }

        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        map.addMarker(marker);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(PLACE).zoom(15).build();
        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                PLACE, 15));

        // Polylines are useful for marking paths and routes on the map.
//        map.addPolyline(new PolylineOptions().geodesic(true)
//                .add(new LatLng(-33.866, 151.195))  // Sydney
//                .add(new LatLng(-18.142, 178.431))  // Fiji
//                .add(new LatLng(21.291, -157.821))  // Hawaii
//                .add(new LatLng(37.423, -122.091))  // Mountain View
//        );

        return v;
    }

    private void getMap(View v,Bundle savedInstanceState) {
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());

        map = mapView.getMap();
    }


}
