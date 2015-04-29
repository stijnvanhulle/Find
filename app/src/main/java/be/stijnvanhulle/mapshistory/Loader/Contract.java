package be.stijnvanhulle.mapshistory.Loader;

import android.provider.BaseColumns;

/**
 * Created by stijnvanhulle on 28/04/15.
 */
public class Contract {
    public interface StoresColumns extends BaseColumns {
        public static final String COLUMN_BEDRIJFSNAAM = "bedrijfsnaam";
        public static final String COLUMN_ADRES = "adres";
        public static final String COLUMN_GEMEENTE="gemeente";
        public static final String COLUMN_GEO_X="geox";
        public static final String COLUMN_GEO_Y="geoy";
    }
    public interface RestaurantsColumns extends BaseColumns {
        public static final String COLUMN_NAAM = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_KEYWORDS = "keywords";
        public static final String COLUMN_GEO_X="geox";
        public static final String COLUMN_GEO_Y="geoy";
    }
}
