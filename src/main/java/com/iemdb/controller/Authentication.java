package com.iemdb.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iemdb.Entity.User;
import com.iemdb.Repository.UserRepository;
import com.iemdb.exception.RestException;
import com.iemdb.exception.UserAlreadyExists;
import com.iemdb.exception.UserNotFound;
import com.iemdb.model.CurrentUser;
import com.iemdb.model.IEMovieDataBase;
import com.iemdb.utils.Utils;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class Authentication {

    public static String KEY = "iemdb1401iemdb1401iemdb1401iemdb1401";
    UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<JsonNode> login(@RequestBody Map<String, String> input) {
        Utils.wait(2000);
        try {
            String username = input.get("username");
            String password = input.get("password");

            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
                password = new String(hashBytes, StandardCharsets.UTF_8);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }

            Optional<User> user = userRepository.findById(username);
            if(user.isEmpty())
                throw new UserNotFound();
            if(!user.get().getEmail().equals(username)
            || !user.get().getPassword().equals(password))
                throw new RuntimeException();
            String jwt = createToken(user.get().getEmail());
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
            Optional<User> user = userRepository.findById(input.get("email"));
            if(user.isPresent())
                throw new UserAlreadyExists();
            User createdUser = createUser(input);
            String jwt = createToken(createdUser.getEmail());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resp = objectMapper.createObjectNode();
            resp.put("token", jwt);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resp = objectMapper.createObjectNode();
            resp.put("error", e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<JsonNode> user() {
        Utils.wait(2000);
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            User user = (User) request.getAttribute("user");
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resp = objectMapper.createObjectNode();
            resp.put("email", user.getEmail());
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resp = objectMapper.createObjectNode();
            resp.put("error", e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<JsonNode> callback(@RequestParam("code") String code) {
        Utils.wait(2000);
        try {
            String url = String.format("https://github.com/login/oauth/access_token?client_id=f91cb2bad21d5c303ca9&client_secret=68ccc847fdb4e0e7f214a3ac5462385fd48cae25&code=%s", code);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            String accessToken =response.body().split("[&=]")[1];
            url = "https://api.github.com/user";
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Authorization", String.format("Bearer %s", accessToken))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, String> json = mapper.readValue(response.body(), HashMap.class);

            String email = json.get("email");
            String nickname = json.get("login");
            String name = json.get("name");
            String password = "";
            String created_at = json.get("created_at");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate birthdate = LocalDate.parse(created_at.substring(0, 10), formatter).minusYears(18);
            Optional<User> user = userRepository.findById(email);
            if (user.isPresent()) {
                user.get().setBirthDate(birthdate);
                user.get().setName(name);
                user.get().setNickname(nickname);
                user.get().setPassword(password);
                userRepository.save(user.get());
            } else {
                User createdUser = new User(email, password, nickname, name, birthdate);
                userRepository.save(createdUser);
            }
            String jwt = createToken(email);
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resp = objectMapper.createObjectNode();
            resp.put("token", jwt);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resp = objectMapper.createObjectNode();
            resp.put("error", e.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
    }

    private String createToken(String user) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date exp = c.getTime();

        SecretKey key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return Jwts.builder()
                .signWith(key)
                .setHeaderParam("typ", "JWT")
                .setIssuer("IEMDB.ir")
                .setIssuedAt(new Date())
                .setExpiration(exp)
                .claim("user", user)
                .compact();
    }

    private User createUser(Map<String, String> input) {
        String email = input.get("email");
        String password = input.get("password");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthdate = LocalDate.parse(input.get("birthDate"), formatter);
        String name = input.get("name");
        String nickname = input.get("nickname");
        User user = new User(email, password, nickname, name, birthdate);
        userRepository.save(user);
        return user;
    }
}
