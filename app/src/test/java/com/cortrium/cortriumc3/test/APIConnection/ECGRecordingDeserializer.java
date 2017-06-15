package com.cortrium.cortriumc3.test.APIConnection;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Kike Bodi on 06/06/2017.
 * Cortrium
 * bodi.inf@gmail.com
 */

public class ECGRecordingDeserializer implements JsonDeserializer<ECGRecording> {
    @Override
    public ECGRecording deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ECGRecording recording = new ECGRecording();

        JsonObject recordingJsonObject = json.getAsJsonObject();
        recording.filename = recordingJsonObject.get("filename").getAsString();
        recording.id = recordingJsonObject.get("id").getAsString();
        recording.start = recordingJsonObject.get("start").getAsString();
        try{
            recording.url = recordingJsonObject.get("url").getAsString();
        } catch (NullPointerException e){
            recording.url = recordingJsonObject.get("data").getAsString();
        }
        return recording;
    }
}
