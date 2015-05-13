package Uloha02.Forms;

import Uloha02.FileReceiver;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

public class Client implements Runnable {
    private JTextField txtSubory;
    private JComboBox comboBox1;
    private JButton stiahnutButton;
    private JPanel panel;

    final private Set<FileReceiver> stahovace = new HashSet<FileReceiver>();

    public Client() {
        stiahnutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stiahnutButtonActionPerformed(e);
            }
        });
    }

    private void stiahnutButtonActionPerformed(ActionEvent e) {
        // zacneme stahovat subor
        FileReceiver stahovac = (FileReceiver) comboBox1.getSelectedItem();
        new Thread(stahovac).start();
    }

    public void run() {
        JFrame frame = new JFrame("Client");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    public void addStahovac(FileReceiver stahovac) {
        stahovace.add(stahovac);
        DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBox1.getModel();
        if (comboBoxModel.getIndexOf(stahovac) == -1) {
            comboBox1.addItem(stahovac);
        }
        txtSubory.setText("" + stahovace.size());
    }
}
