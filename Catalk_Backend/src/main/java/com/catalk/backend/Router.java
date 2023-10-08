package com.catalk.backend;

import java.util.*;
import java.sql.*;

import com.google.gson.Gson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class Router {

    public static void main(String[] args) {
        SpringApplication.run(Router.class, args);
    }
}

@RestController
class TestController {
    @PostMapping("/login")
    public String handleLogin(@RequestBody String requestData) throws SQLException {
        Gson gson = new Gson();
        HashMap<String, String> info = gson.fromJson(requestData, HashMap.class);
        Connection conn = CredentialUtil.createConnection();
        HashMap<String, Object> res = Login.login(info, conn);
        CredentialUtil.closeConnection();
        return gson.toJson(res);
    }

    @PostMapping("/signup")
    public String handleSignup(@RequestBody String requestData) throws SQLException {
        Gson gson = new Gson();
        HashMap<String, String> userInfo = gson.fromJson(requestData, HashMap.class);
        Connection conn = CredentialUtil.createConnection();

        //signal is either "submit" or "verify"
        HashMap<String, Object> res = Signup.createUser(userInfo, conn, userInfo.get("signal"), userInfo.get("code"));
        CredentialUtil.closeConnection();
        return gson.toJson(res);
    }

    @PostMapping("/test_check")
    public int handleTestCheck(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String inputCode
    ) throws SQLException {
        HashMap<String, String> userInfo = new HashMap<String, String>() {{
            put("username", username);
            put("password", password);
            put("email", email);
        }};
        Connection conn = CredentialUtil.createConnection();

        HashMap<String, Object> res = Signup.createUser(userInfo, conn, "submit", "");
        CredentialUtil.closeConnection();
        return (int) res.get("result");
    }
}
