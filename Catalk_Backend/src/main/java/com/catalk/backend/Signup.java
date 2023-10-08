package com.catalk.backend;

import java.sql.*;
import java.util.*;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;

import org.mindrot.jbcrypt.BCrypt;

public class Signup {
    private static boolean checkUsernameDuplicate(HashMap<String, String> userInfo, Connection conn) {
        //true for duplication
        String username = userInfo.get("username");
        String sql = "SELECT COUNT(*) FROM users WHERE username = '" + username + "';";
        int result = 0;

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                result = resultSet.getInt("count");
            }

            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result > 0 ? true : false;
    }

    private static boolean checkEmailDuplicate() {
        return true;
    }

    private static boolean sendVerificationCode(HashMap<String, String> userInfo, Connection conn) {
        JavaMailSenderImpl emailSender = new JavaMailSenderImpl();

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        emailSender.setHost("smtp.gmail.com"); // Set your SMTP server host
        emailSender.setPort(587); // Set your SMTP server port
        emailSender.setUsername("petelloni233@gmail.com"); // Set your SMTP username (if required)
        emailSender.setPassword("zecujfpwmzisazvc");

        java.util.Properties props = emailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true"); // Enable SMTP authentication if required
        props.put("mail.smtp.starttls.enable", "true"); // Enable TLS if required
        props.put("mail.smtp.timeout", 5000); // Set a timeout (in milliseconds)


        //SimpleMailMessage message = new SimpleMailMessage();

        //TODO: check email address is valid
        //TODO: thymeleaf


        String verificationCode = generateVerificationCode(userInfo, conn);
        String emailHTML = "";
        try {
            emailHTML = readHtmlFromFile("src/main/resources/templates/test.html");
        } catch (IOException e) {
            e.printStackTrace();
        }

        emailHTML = emailHTML.replace("{code}", verificationCode);

        try {
            helper.setTo(userInfo.get("email"));
            helper.setSubject("Catalk Verification Code");
            helper.setText(emailHTML, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        try {
            emailSender.send(message);
        } catch (Exception e) {
            System.out.println("here");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static String readHtmlFromFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        return content.toString();
    }

    private static String generateVerificationCode(HashMap<String, String> userInfo, Connection conn) {
        Random random = new Random();
        String formattedCode = String.format("%06d", random.nextInt(1000000));

        String username = userInfo.get("username");
        String sqlRegisterCode = String.format("INSERT INTO " +
                "verification_code (username, code, expiration_date) " +
                "VALUES ('%1$s', '%2$s', NOW() + INTERVAL '5 minutes') " +
                "ON CONFLICT (username) DO UPDATE SET " +
                "code = '%2$s', expiration_date = NOW() + INTERVAL '5 minutes';", username, formattedCode);

        String sqlDeleteExpiredCode = "DELETE FROM verification_code WHERE expiration_date < NOW()";
        try {
            //logic: first delete old ones, then always insert new code with override
            PreparedStatement register = conn.prepareStatement(sqlRegisterCode);
            PreparedStatement delete = conn.prepareStatement(sqlDeleteExpiredCode);

            //delete old ones
            int rowsAffectedDelete = delete.executeUpdate();
            int rowsAffectedInsert = register.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        //TODO: error handling
        return formattedCode;
    }

    private static boolean checkVerificationCode(HashMap<String, String> userInfo, Connection conn, String inputCode) {
        //status: correct(1), expired(2), incorrect(3)

        boolean result = false;
        String sql = String.format(
                "SELECT COUNT(*) FROM verification_code where username = '%1$s' AND code = '%2$s';",
                userInfo.get("username"),
                inputCode
                );

        try {
            int rowsMatched = 0;
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                rowsMatched = resultSet.getInt("count");
            }

            if (rowsMatched == 1) {
                result = true;
            } else {
                result = false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String hashPassword(HashMap<String, String> userInfo, Connection conn) {

        return BCrypt.hashpw(userInfo.get("password"), BCrypt.gensalt());
    }

    public static HashMap<String, Object> createUser(HashMap<String, String> userInfo, Connection conn, String signal, String code) throws SQLException {
        /*
        TODO: procedure:
        user upload information ->(signal send without code server send email -> user upload correct code -> (signal verify w/ code)
        -> hashPassword(), and create user at here.
         */

        HashMap<String, Object> result = new HashMap<String, Object>();

        if (checkUsernameDuplicate(userInfo, conn)) {
            result.put("result", 1);
            result.put("info", "NAME_DUPLICATE");
            return result;
        }
        if (Objects.equals(signal, "submit")) {
            boolean isVerificationCodeSent = sendVerificationCode(userInfo, conn);
            if (isVerificationCodeSent) {
                result.put("result", 0);
                result.put("info", "OK");
                return result;
            }
        } else if (Objects.equals(signal, "verify")) {
            //TODO: prevent code check abuse
            //TODO: what if user change password? validity of token

            //token will expire in 1h
            boolean isCodeChecked = checkVerificationCode(userInfo, conn, code);
            if (isCodeChecked) {
                System.out.println("OK_HERE");

                //register user.

                String sql = String.format("INSERT INTO users (username, password_hash, email) " +
                        "VALUES ('%1$s', '%2$s', '%3$s');",
                        userInfo.get("username"),
                        hashPassword(userInfo, conn),
                        userInfo.get("email"));

                int rowsInserted = conn.prepareStatement(sql).executeUpdate();
                result.put("result", 0);
                result.put("info", "OK");
                return result;
            }
        }
        return result;
    }
}
