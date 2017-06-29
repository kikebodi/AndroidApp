
package com.cortrium.cortriumc3.ApiConnection.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Events {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("identifier")
    @Expose
    private String identifier;
    @SerializedName("logged")
    @Expose
    private String logged;
    @SerializedName("id")
    @Expose
    private String id;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Events() {
    }

    /**
     * 
     * @param id
     * @param name
     * @param logged
     * @param identifier
     */
    public Events(String name, String identifier, String logged, String id) {
        super();
        this.name = name;
        this.identifier = identifier;
        this.logged = logged;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Events withName(String name) {
        this.name = name;
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Events withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public String getLogged() {
        return logged;
    }

    public void setLogged(String logged) {
        this.logged = logged;
    }

    public Events withLogged(String logged) {
        this.logged = logged;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Events withId(String id) {
        this.id = id;
        return this;
    }

}
