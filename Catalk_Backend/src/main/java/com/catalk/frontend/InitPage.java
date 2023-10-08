package com.catalk.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InitPage {
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
    private JTextField textField1;
    private JButton button1;

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
                eventNotifier.notifyLoginCompleted();
            }
        });
    }
}
