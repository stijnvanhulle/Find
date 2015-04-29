package be.stijnvanhulle.mapshistory.Models;

import java.util.ArrayList;
import java.util.List;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * Created by stijnvanhulle on 29/04/15.
 */
public class Restaurant {
    public String Naam;
    public String Description;
    public ArrayList<String> Keywords;
    public String Geo_x;
    public String Geo_y;


    public Restaurant(String naam,String description,String keywords,String geo_x,String geo_y){
        JSONDeserializer<ArrayList<String>> ser = new JSONDeserializer<>();

        this.Naam=naam;
        this.Description=description;
        this.Keywords=ser.deserialize(keywords);


        this.Geo_x=geo_x;
        this.Geo_y=geo_y;
    }
    public Restaurant(String naam,String description,ArrayList<String> keywords,String geo_x,String geo_y){
        this.Naam=naam;
        this.Description=description;
        this.Keywords=keywords;
        this.Geo_x=geo_x;
        this.Geo_y=geo_y;
    }
    public Restaurant(){

    }

    public static List<Restaurant> Restaurants;
}
