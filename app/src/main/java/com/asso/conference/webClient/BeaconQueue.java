package com.asso.conference.webClient;

import android.os.Environment;

import com.asso.conference.webClient.models.BeaconModel;
import com.squareup.moshi.Moshi;
import com.squareup.tape2.ObjectQueue;
import com.squareup.tape2.QueueFile;

import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;

import retrofit2.converter.gson.GsonConverterFactory;

public class BeaconQueue  {

    ObjectQueue<BeaconModel> queue;

    public BeaconQueue(){
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "baconQueue.dat");
        try {
            QueueFile queueFile = new QueueFile.Builder(file).build();
            ObjectQueue.Converter<BeaconModel> converter = new MoshiConverter<>(new Moshi.Builder().build(), BeaconModel.class);
            ObjectQueue<BeaconModel> queue = ObjectQueue.create(queueFile, converter);
            queue = ObjectQueue.create(queueFile, converter);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    public void push(BeaconModel model){
        if(queue == null)
            return;
        try {
            queue.add(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
