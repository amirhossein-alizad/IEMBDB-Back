package com.iemdb.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iemdb.Entity.User;
import com.iemdb.exception.RestException;
import com.iemdb.exception.UserAlreadyExists;
import com.iemdb.exception.UserNotFound;
import com.iemdb.model.IEMovieDataBase;
import com.iemdb.utils.Utils;
import io.jsonwebtoken.Jwts;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Authentication {

    public static String KEY = "iemdb1401iemdb1401iemdb1401iemdb1401";

    @PostMapping("/login")
    public ResponseEntity<JsonNode> login(@RequestBody Map<String, String> input) {
        Utils.wait(2000);
        try {
            String username = input.get("username");
            String password = input.get("password");
            User user = IEMovieDataBase.getInstance().getUser(username);
            if (!user.getPassword().equals(password)) {
                throw new UserNotFound();
            }
            IEMovieDataBase.getInstance().setCurrentUser(user);
            String jwt = createToken(user.getEmail());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resp = objectMapper.createObjectNode();
            resp.put("token", jwt);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (UserNotFound e) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resp = objectMapper.createObjectNode();
            resp.put("error", e.getMessage());
            return new ResponseEntity<>(resp, e.getStatusCode());
        } catch (Exception e) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resp = objectMapper.createObjectNode();
            resp.put("error", e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<JsonNode> signup(@RequestBody Map<String, String> input) {
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
            User user = IEMovieDataBase.getInstance().addUser(email, password, name, nickname, birthdate);
            IEMovieDataBase.getInstance().setCurrentUser(user);
            String jwt = createToken(user.getEmail());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resp = objectMapper.createObjectNode();
            resp.put("token", jwt);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (RestException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resp = objectMapper.createObjectNode();
            resp.put("error", e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resp = objectMapper.createObjectNode();
            resp.put("error", e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.OK);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        Utils.wait(2000);
        IEMovieDataBase.getInstance().setCurrentUser(null);
        return new ResponseEntity<>("You logged out successfully!", HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<User> user() {
        Utils.wait(2000);
        try {
            User user = IEMovieDataBase.getInstance().getCurrentUser();
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (RestException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code) {
        Utils.wait(2000);
        System.out.println(code);

        try {
            String url = String.format("https://github.com/login/oauth/access_token?client_id=f91cb2bad21d5c303ca9&client_secret=68ccc847fdb4e0e7f214a3ac5462385fd48cae25&code=%s", code);
            System.out.println(url);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            String accessToken =response.body().split("[&=]")[1];
            System.out.println(accessToken);
            url = "https://api.github.com/user";
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Authorization", String.format("Bearer %s", accessToken))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, String> json = mapper.readValue(response.body(), HashMap.class);
            System.out.println(json.get("login"));
        } catch (Exception e) {
            System.out.println("mother fucker bitch");
            System.out.println(e);
        }

        return new ResponseEntity<>(code, HttpStatus.OK);
    }

    private String createToken(String user) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date exp = c.getTime();

        SecretKey key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        String jws = Jwts.builder()
                .signWith(key)
                .setHeaderParam("typ", "JWT")
                .setIssuer("IEMDB.ir")
                .setIssuedAt(new Date())
                .setExpiration(exp)
                .claim("user", user)
                .compact();

        return jws;
    }
}
