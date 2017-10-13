package Service.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Random;
import Server.Serializer;
import Server.Server;


public class SNames {
    String[] data;


    public SNames(String fileName) {
        try {
            InputStream file = new FileInputStream(new File(fileName));
            SNames temp = (SNames) Serializer.decoder(Server.readString(file), this.getClass());
            this.data = temp.getData();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getRandomSName(){
        Random rand = new Random();
        return data[rand.nextInt(data.length)];
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }
}
