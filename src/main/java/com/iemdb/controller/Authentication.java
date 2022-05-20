package com.iemdb.controller;

import com.iemdb.Entity.User;
import com.iemdb.Repository.UserRepository;
import com.iemdb.exception.RestException;
import com.iemdb.exception.UserAlreadyExists;
import com.iemdb.exception.UserNotFound;
import com.iemdb.model.CurrentUser;
import com.iemdb.model.IEMovieDataBase;
import com.iemdb.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Authentication {

    UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody Map<String, String> input) {
        Utils.wait(2000);
        try {
            String username = input.get("username");
            String password = input.get("password");
            Optional<User> user = userRepository.findById(username);
            if(user.isEmpty())
                throw new UserNotFound();
            System.out.println();
            if(!user.get().getEmail().equals(username)
            || !user.get().getPassword().equals(password))
                throw new RuntimeException();
            CurrentUser.username = username;
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Map<String, String> input) {
        Utils.wait(2000);
        try {
            input.computeIfAbsent("email", key -> {throw new RuntimeException(key + " not found!");});
            input.computeIfAbsent("password", key -> {throw new RuntimeException(key + " not found!");});
            input.computeIfAbsent("name", key -> {throw new RuntimeException(key + " not found!");});
            input.computeIfAbsent("nickname", key -> {throw new RuntimeException(key + " not found!");});
            input.computeIfAbsent("birthDate", key -> {throw new RuntimeException(key + " not found!");});
            String email = input.get("email");
            String password = input.get("password");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate birthdate = LocalDate.parse(input.get("birthDate"), formatter);
            String name = input.get("name");
            String nickname = input.get("nickname");
            User user = new User(email, password, nickname, name, birthdate);
            CurrentUser.username = user.getEmail();
            userRepository.save(user);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("You signed up successfully!", HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        Utils.wait(2000);
        CurrentUser.username = "";
        return new ResponseEntity<>("You logged out successfully!", HttpStatus.OK);
    }

}
