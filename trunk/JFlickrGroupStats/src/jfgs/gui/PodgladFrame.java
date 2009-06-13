/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PodgladFrame.java
 *
 * Created on 2009-01-15, 21:27:14
 */

package jfgs.gui;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author michalus
 */
public class PodgladFrame extends javax.swing.JFrame {

    /** Creates new form PodgladFrame */
    public PodgladFrame(String path) {

        initComponents();

        setSize(new Dimension(800, 600));

        try {
            wgrajPlik(path);
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    private void wgrajPlik(String path) throws IOException {

        File f = new File(path);
        FileReader fr = new FileReader(f);
        BufferedReader bf = new BufferedReader(fr);
        
        info.setText(f.getCanonicalPath());

        StringBuilder sb = new StringBuilder();
        String linia = "";
        while ((linia = bf.readLine()) != null) {
            sb.append(linia);
            sb.append(System.getProperty("line.separator"));
        }
        bf.close();

        podglad.setText(sb.toString());

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        podglad = new javax.swing.JTextArea();
        info = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("JFlickrGroupStats - Podgląd");

        podglad.setColumns(20);
        podglad.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        podglad.setRows(5);
        jScrollPane1.setViewportView(podglad);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        info.setText("...");
        getContentPane().add(info, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel info;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea podglad;
    // End of variables declaration//GEN-END:variables

}