package com.bloopark.hue;

/**
 * Created with IntelliJ IDEA.
 * User: cheimke
 * Date: 24.05.13
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public class Bulp {

    public String id;
    public String name;

    public Bulp(){}

    public Bulp(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }
}
