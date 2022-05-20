//package com.iemdb.controller;
//
//import com.iemdb.Entity.User;
//import com.iemdb.exception.RestException;
//import com.iemdb.exception.UserAlreadyExists;
//import com.iemdb.exception.UserNotFound;
//import com.iemdb.model.IEMovieDataBase;
//import com.iemdb.utils.Utils;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.Map;
//
//@RestController
//@CrossOrigin(origins = "*", allowedHeaders = "*")
//public class Authentication {
//
//    @PostMapping("/login")
//    public ResponseEntity<User> login(@RequestBody Map<String, String> input) {
//        Utils.wait(2000);
//        try {
//            String username = input.get("username");
//            String password = input.get("password");
//            User user = IEMovieDataBase.getInstance().getUser(username);
//            IEMovieDataBase.getInstance().setCurrentUser(user);
//            return new ResponseEntity<>(user, HttpStatus.OK);
//        } catch (UserNotFound e) {
//            return new ResponseEntity<>(null, e.getStatusCode());
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @PostMapping("/signup")
//    public ResponseEntity<String> signup(@RequestBody Map<String, String> input) {
//        Utils.wait(2000);
//        try {
//            input.computeIfAbsent("email", key -> {throw new RuntimeException(key + " not found!");});
//            input.computeIfAbsent("password", key -> {throw new RuntimeException(key + " not found!");});
//            input.computeIfAbsent("name", key -> {throw new RuntimeException(key + " not found!");});
//            input.computeIfAbsent("nickname", key -> {throw new RuntimeException(key + " not found!");});
//            input.computeIfAbsent("birthDate", key -> {throw new RuntimeException(key + " not found!");});
//            String email = input.get("email");
//            String password = input.get("password");
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            LocalDate birthdate = LocalDate.parse(input.get("birthDate"), formatter);
//            String name = input.get("name");
//            String nickname = input.get("nickname");
//            User user = IEMovieDataBase.getInstance().addUser(email, password, name, nickname, birthdate);
//            IEMovieDataBase.getInstance().setCurrentUser(user);
//        } catch (RestException e) {
//            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>("You signed up successfully!", HttpStatus.OK);
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout() {
//        Utils.wait(2000);
//        IEMovieDataBase.getInstance().setCurrentUser(null);
//        return new ResponseEntity<>("You logged out successfully!", HttpStatus.OK);
//    }
//
//    @GetMapping("/user")
//    public ResponseEntity<User> user() {
//        Utils.wait(2000);
//        try {
//            User user = IEMovieDataBase.getInstance().getCurrentUser();
//            return new ResponseEntity<>(user, HttpStatus.OK);
//        } catch (RestException e) {
//            return new ResponseEntity<>(null, e.getStatusCode());
//        }
//    }
//}
