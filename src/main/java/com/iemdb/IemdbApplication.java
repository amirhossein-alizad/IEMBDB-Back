package com.iemdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.iemdb.model.Initializer;
import com.iemdb.model.DataBase;

@SpringBootApplication
public class IemdbApplication {


    public static void main(String[] args) {
        DataBase dataBase = new DataBase();
        Initializer initializer = new Initializer(dataBase);
        initializer.getDataFromAPI();
        SpringApplication.run(IemdbApplication.class, args);
    }

}
