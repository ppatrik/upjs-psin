package Uloha02.Forms;

import Uloha02.InfoSender;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Server implements Runnable {
    private JEditorPane editorPane1;
    private JPanel panel;
    private JButton addFile;
    private JTextField sendingFiles;
    public InfoSender parent;

    public Server() {
        addFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addFileActionPerformed(e);
            }
        });
    }

    public void println(String s) {
        editorPane1.setText(editorPane1.getText() + s + "\n");
    }

    public void run() {
        JFrame frame = new JFrame("Server");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    public void addFileActionPerformed(ActionEvent e) {
        parent.pridajSubory();
    }

    public void setSendingFiles(int sendingFiles) {
        this.sendingFiles.setText("" + sendingFiles);
    }
}
