package be.stijnvanhulle.mapshistory.Models;

import java.util.List;

import be.stijnvanhulle.mapshistory.Loader.Contract;

/**
 * Created by stijnvanhulle on 28/04/15.
 */
public class Store {
    public String Bedrijfsnaam;
    public String Adres;
    public String Gemeente;
    public String Geo_x;
    public String Geo_y;


    public Store(String bedrijfsnaam,String adres,String gemeente,String geo_x,String geo_y){
        this.Bedrijfsnaam=bedrijfsnaam;
        this.Adres=adres;
        this.Gemeente=gemeente;
        this.Geo_x=geo_x;
        this.Geo_y=geo_y;
    }
    public Store(){

    }

    public static List<Store> Stores;
}
