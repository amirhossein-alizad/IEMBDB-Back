package com.iemdb;

import com.iemdb.Entity.User;
import com.iemdb.model.IEMovieDataBase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IemdbApplication {

    public static void main(String[] args) {
        try {
            User user = IEMovieDataBase.getInstance().getUser("sara@ut.ac.ir");
            IEMovieDataBase.getInstance().setCurrentUser(user);
        } catch (Exception e) {
            System.out.println(e);
        }
        SpringApplication.run(IemdbApplication.class, args);
    }

}
