package com.catalk.frontend.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

import com.google.gson.Gson;
import org.json.JSONObject;

public class InitPage {

    //TODO: verification code and sign up
    private EventNotifier eventNotifier;

    public JPanel TestPanel;
    private JPanel Login;
    private JPanel Signup;
    private JPanel Captcha;
    private JTextField LoginUsernameInput;
    private JLabel LoginTitle;
    private JLabel LoginUsername;
    private JTextField LoginPasswordInput;
    private JButton LoginSubmit;
    private JButton LoginSwitchSignup;
    private JLabel LoginPassword;
    private JTextField SignupUsernameInput;
    private JTextField SignupPasswordOneInput;
    private JTextField SignupPasswordTwoInput;
    private JTextField SignupEmailInput;
    private JButton SignupSubmit;
    private JButton SignupSwitchLogin;
    private JLabel SignupTitle;
    private JLabel SignupUsername;
    private JLabel SignupPasswordOne;
    private JLabel SignupPasswordTwo;
    private JLabel SignupEmail;
    private JTextField CaptchaInput;
    private JButton CaptchaSubmit;
    private JLabel CaptchaTitle;
    private JLabel CaptchaAlert;
    private JLabel SignupAlert;

    private String appStatus = "submit";

    private HashMap<String, String> request(String address, HashMap<String, String> postBody) {
        URI uri = null;
        try {
            uri = new URI(address);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        Gson gson = new Gson();

        JSONObject json = new JSONObject(postBody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return gson.fromJson(response.body(), HashMap.class);
    }

    public InitPage(EventNotifier eventNotifier) {
        this.eventNotifier = eventNotifier;

        LoginSwitchSignup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CardLayout)TestPanel.getLayout()).show(TestPanel, "Card2");
            }
        });
        SignupSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: check email address correct

                //Gather information
                String username = SignupUsernameInput.getText();
                String passwordOne = SignupPasswordOneInput.getText();
                String passwordTwo = SignupPasswordTwoInput.getText();
                String emailAddress = SignupEmailInput.getText();

                //CHECK empty entry
                if (
                        username.equals("")
                        || passwordOne.equals("")
                        || passwordTwo.equals("")
                        || emailAddress.equals("")
                ) {
                    SignupAlert.setText("Please Complete All Fields.");
                    try {
                        Thread.sleep(3000);
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                    SignupAlert.setText("");
                    return;
                }

                //CHECK if two password are same
                if (!passwordOne.equals(passwordTwo)) {
                    SignupAlert.setText("Two Passwords Do Not Match.");
                    try {
                        Thread.sleep(3000);
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                    SignupAlert.setText("");
                    return;
                }

                //SEND
                HashMap<String, String> postBody = new HashMap<String, String>() {{
                    put("username", username);
                    put("password", passwordOne);
                    put("email", emailAddress);
                    put("signal", appStatus);
                    put("code", "");
                }};
                HashMap<String, String> result = request("http://localhost:8081/signup", postBody);

                //IF OK,
                //switch gui to captcha(card3)
                //adjust appstatus to verify
                if (result.get("result").equals("0")) {
                    ((CardLayout)TestPanel.getLayout()).show(TestPanel, "Card3");
                    appStatus = "verify";
                }
            }
        });
        SignupSwitchLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CardLayout)TestPanel.getLayout()).show(TestPanel, "Card1");
            }
        });
        LoginSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = LoginUsernameInput.getText();
                String password = LoginPasswordInput.getText();

                HashMap<String, String> postBody = new HashMap<>() {{
                    put("username", username);
                    put("password", password);
                }};
                HashMap<String, String> response = request("http://localhost:8081/login", postBody);
                if (response.get("result").equals("0")) {
                    //Switch to MainPage, with register token
                    String token = response.get("info");
                    eventNotifier.notifyLoginCompleted(token);
                }
                //TODO: login failed
            }
        });
        CaptchaSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = CaptchaInput.getText();
                HashMap<String, String> postBody = new HashMap<String, String>() {{
                    /*
                    put("username", username);
                    put("password", passwordOne);
                    put("email", emailAddress);
                    put("signal", appStatus);
                    put("code", "");
                    
                     */
                }};
            }
        });
    }
}
