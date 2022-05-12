package com.meowmivice.game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;


public class Splash extends JPanel implements ActionListener {
    JLabel label;
    JButton startButton;
    Font normalFont = new Font ("Times New Roman", Font.PLAIN, 30);

    public Splash () throws Exception {
        setPreferredSize(new Dimension(1094,  730));
        setLayout(null);
        setBackground(Color.black);
        BufferedImage img = ImageIO.read(new File("resources/Images/splash.jpg"));
        ImageIcon imageIcon = new ImageIcon(img);
        Image image = imageIcon.getImage();
        Image img2 = image.getScaledInstance(1094, 730,  Image.SCALE_DEFAULT);
        label = new JLabel( new ImageIcon(img2));
        label.setBounds(0,0,1094, 730);
        add(label);

        startButton = new JButton("START");
        startButton.setOpaque(true);
        startButton.setForeground(Color.MAGENTA);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setBounds (450, 500, 200, 30);
        startButton.setFont(normalFont);
        startButton.addActionListener(this);
        add(startButton);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        String command = e.getActionCommand();
        if(command.equalsIgnoreCase("start")){
                try {
                    MainFrame.clearContent();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
    }

}