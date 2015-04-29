package be.stijnvanhulle.mapshistory.Loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by stijnvanhulle on 28/04/15.
 */
public class StoresLoader extends AsyncTaskLoader<Cursor> {
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
                    Contract.StoresColumns.COLUMN_BEDRIJFSNAAM,
                    Contract.StoresColumns.COLUMN_ADRES,
                    Contract.StoresColumns.COLUMN_GEMEENTE,
                    Contract.StoresColumns.COLUMN_GEO_X,
                    Contract.StoresColumns.COLUMN_GEO_Y
            };
    private static Object lock = new Object();


            /*
            ** Constructor
             */

    public StoresLoader(Context context) {
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
            String url = "http://data.kortrijk.be/middenstand/winkels_markten.json";
            InputStream input = null;
            JsonReader reader = null;

            try {

                //json ophalen
                input = new URL(url).openStream();
                reader = new JsonReader(new InputStreamReader(input, "UTF-8"));

                int id = 1;
                //reader.setLenient(true);
                reader.beginObject();



                    String bedrijfsnaam = "";
                    String address = "";
                    String gemeente = "";
                    String geo_x="";
                    String geo_y="";

                    while (reader.hasNext()) {
                        String name = reader.nextName();

                        if (name.equals("company")){
                            reader.beginArray();
                            if (reader.hasNext()){


                                while (reader.hasNext()){
                                    if (reader.peek()==JsonToken.NULL){
                                        reader.skipValue();
                                    }else{

                                        reader.beginObject();
                                        while (reader.hasNext()) {

                                            name = reader.nextName();

                                            //controle welke naam json object heeft
                                            if (name.equals("address")) {

                                                if (reader.hasNext()) {
                                                    reader.beginObject();
                                                    while (reader.hasNext()) {
                                                        name = reader.nextName();
                                                        if (name.equals("@text")) {
                                                            address = reader.nextString();
                                                        }

                                                    }
                                                    reader.endObject();

                                                }


                                            } else if (name.equals("bedrijfsnaam")) {

                                                if (reader.hasNext()) {
                                                    reader.beginObject();
                                                    while (reader.hasNext()) {
                                                        name = reader.nextName();
                                                        if (name.equals("@text")) {
                                                            bedrijfsnaam = reader.nextString();
                                                        }

                                                    }
                                                    reader.endObject();

                                                }


                                            } else if (name.equals("gemeente")) {

                                                if (reader.hasNext()) {
                                                    reader.beginObject();
                                                    while (reader.hasNext()) {
                                                        name = reader.nextName();
                                                        if (name.equals("@text")) {
                                                            gemeente = reader.nextString();
                                                        }

                                                    }
                                                    reader.endObject();

                                                }

                                            } else if (name.equals("geo_x")) {

                                                if (reader.hasNext()) {
                                                    reader.beginObject();
                                                    while (reader.hasNext()) {
                                                        name = reader.nextName();
                                                        if (name.equals("@text")) {
                                                            geo_x = reader.nextString();
                                                        }

                                                    }
                                                    reader.endObject();

                                                }


                                            } else if (name.equals("geo_y")) {

                                                if (reader.hasNext()) {
                                                    reader.beginObject();
                                                    while (reader.hasNext()) {
                                                        name = reader.nextName();
                                                        if (name.equals("@text")) {
                                                            geo_y = reader.nextString();
                                                        }

                                                    }
                                                    reader.endObject();

                                                }


                                            } else {
                                                reader.skipValue();
                                            }
                                        }



                                        MatrixCursor.RowBuilder row = matrixCursor.newRow();
                                        row.add(id);
                                        row.add(bedrijfsnaam);
                                        row.add(address);
                                        row.add(gemeente);
                                        row.add(geo_x);
                                        row.add(geo_y);
                                        id++;
                                        reader.endObject();



                                    }

                                }



                            }
                            reader.endArray();
                        }



                        //reader.endArray();




                    }

                //reader.endObject();











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
