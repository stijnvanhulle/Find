package be.stijnvanhulle.mapshistory.Loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import be.stijnvanhulle.mapshistory.MainActivity;
import flexjson.JSONSerializer;

/**
 * Created by stijnvanhulle on 29/04/15.
 */
public class RestaurantLoader extends AsyncTaskLoader<Cursor> {
    //extends AsyncTaskLoader<Cursor>
            /*
            ** Fields
             */

           /*
            ** Fields
             */

    private Cursor cursor;
    private final String[] columnNames = new String[]
            {
                    BaseColumns._ID,
                    Contract.RestaurantsColumns.COLUMN_NAAM,
                    Contract.RestaurantsColumns.COLUMN_DESCRIPTION,
                    Contract.RestaurantsColumns.COLUMN_KEYWORDS,
                    Contract.RestaurantsColumns.COLUMN_GEO_X,
                    Contract.RestaurantsColumns.COLUMN_GEO_Y
            };
    private static Object lock = new Object();


            /*
            ** Constructor
             */

    public RestaurantLoader(Context context) {
        super(context);
    }

            /*
            ** Events
             */

    @Override
    protected void onStartLoading() {
        if (cursor != null) {
            deliverResult(cursor);
        }

        if (takeContentChanged() || cursor == null) {
            forceLoad();
        }
    }

    @Override
    public Cursor loadInBackground() {
        if (cursor == null) {
            loadCursor();
        }
        return cursor;
    }

    /*
    ** Methods
     */
    private void loadCursor() {
        synchronized (lock) {
            if (cursor != null) {
                return;
            }

            MatrixCursor matrixCursor = new MatrixCursor(columnNames);
            //start loading
            //json start
            String url = "http://data.kortrijk.be/geografie/ipoints.json";
            InputStream input = null;
            JsonReader reader = null;

            try {

                //json ophalen
                input = new URL(url).openStream();
                reader = new JsonReader(new InputStreamReader(input, "UTF-8"));

                int id = 1;
                //reader.setLenient(true);
                reader.beginArray();



                String naam = "";
                String description = "";
                ArrayList<String> keywords= new ArrayList<String>();
                String geo_x="";
                String geo_y="";

                while (reader.hasNext()) {
                    reader.beginObject();

                    while (reader.hasNext()) {
                        String name = reader.nextName();

                        if (name.equals("Name")){
                            naam=reader.nextString();


                        }else if(name.equals("description")){
                            description=reader.nextString();

                        }else if(name.equals("lng")){
                            geo_x=reader.nextString();

                        }else if(name.equals("lat")){
                            geo_y=reader.nextString();

                        }else if(name.equals("keywords")){
                            if (reader.hasNext()) {
                                keywords= new ArrayList<String>();
                                reader.beginArray();
                                while (reader.hasNext()) {
                                    keywords.add(reader.nextString());


                                }

                                reader.endArray();
                            }

                        }else{
                            reader.skipValue();
                        }
                    }




                    JSONSerializer ser = new JSONSerializer();

                    for (int i=0;i<keywords.size();i++){
                        if (keywords.get(i).equals("eten") || keywords.get(i).equals("restaurant") ){

                            MatrixCursor.RowBuilder row = matrixCursor.newRow();
                            row.add(id);
                            row.add(naam);
                            row.add(description);
                            row.add(ser.deepSerialize(keywords));
                            row.add(geo_x);
                            row.add(geo_y);
                            id++;
                            break;

                        }
                    }


                    reader.endObject();







                }

                reader.endArray();











                cursor = matrixCursor;


            } catch (IOException ex) {
                ex.printStackTrace();

            } finally {
                try {
                    reader.close();

                } catch (IOException ex) {


                }
                try {
                    input.close();
                } catch (IOException ex) {

                }
            }


            //json einde
            //end loading

            cursor = matrixCursor;
        }
    }
}
