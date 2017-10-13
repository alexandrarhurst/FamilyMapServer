package Service.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Random;
import Server.*;

public class FNames {
    private String[] data;

    public FNames(String fileName) {
        try {
            InputStream file = new FileInputStream(new File(fileName));
            FNames temp = (FNames) Serializer.decoder(Server.readString(file), this.getClass());
            this.data = temp.getData();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getRandomFName(){
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
