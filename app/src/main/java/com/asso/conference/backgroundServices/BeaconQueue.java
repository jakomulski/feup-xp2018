package com.asso.conference.backgroundServices;

import android.os.Environment;

import com.asso.conference.webClient.models.BeaconModel;
import com.squareup.moshi.Moshi;
import com.squareup.tape2.ObjectQueue;
import com.squareup.tape2.QueueFile;

import java.io.File;
import java.io.IOException;

public enum BeaconQueue  {
    INSTANCE;
    ObjectQueue<BeaconModel> queue;

    BeaconQueue(){
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "beaconQueue.dat");
        try {
            QueueFile queueFile = new QueueFile.Builder(file).build();
            ObjectQueue.Converter<BeaconModel> converter = new MoshiConverter<>(new Moshi.Builder().build(), BeaconModel.class);
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
