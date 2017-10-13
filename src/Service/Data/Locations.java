package Service.Data;

import java.io.File;
import Model.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Random;
import Server.Serializer;
import Server.Server;

public class Locations {
    Event.Location[] data;

    public Locations(String fileName) {
        try {
            InputStream file = new FileInputStream(new File(fileName));
            Locations temp = (Locations) Serializer.decoder(Server.readString(file), this.getClass());
            this.data = temp.getData();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Event.Location getRandomLocation(){
        Random rand = new Random();
        return data[rand.nextInt(data.length)];
    }

    public Event.Location[] getData() {
        return data;
    }

    public void setData(Event.Location[] data) {
        this.data = data;
    }
}
