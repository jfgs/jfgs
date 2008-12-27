/*
 * StatsFrame.java
 *
 * Created on 6 październik 2008, 17:48
 */

package jfgs.gui;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import jfgs.logika.JFlickrGroupStats;

/**
 *
 * @author  michalus
 */
public class StatsFrame extends javax.swing.JFrame implements IStats {

    private KontrolerGUI kgui;
    
    /**
     * Akceptuje tylko niepustą wartość będącą liczbą całkowitą dodatnią
     */
    private class TylkoPoprawnyRok extends InputVerifier {

        @Override
        public boolean verify(JComponent input) {
            String wartosc = ((JTextField) input).getText();
            
            if (wartosc == null || "".equals(wartosc.trim())) return false;
            
            {
                Integer liczba = null;

                try {
                    liczba = Integer.parseInt(wartosc);
                } catch(NumberFormatException ex) {
                    ex.printStackTrace();
                    return false;
                }

                if (liczba <= 0) return false;
            }            
            
            return true;
        }
        
    }
    
    private void akcjaZmianaWartosciIdGrupy() {
        if (kgui != null) {
            groupNameField.setText(kgui.getNazwaGrupy());
        }
    }
    
    /**
     * Akcja głównego guzika
     */
    private void akcjaGeneruj() {        
        stanKomponentów(false);
        
        {
            Thread t = new Thread() {

                @Override
                public void run() {
                    new JFlickrGroupStats(kgui.getGroupId(), kgui);
                }
                
            };
            t.start();
        }        
    }
    
    /**
     * Włączenie/wyłączenie edycji edytowalnych komponentów na formularzu
     * @param czyDoEdycji
     */
    private void stanKomponentów(boolean czyDoEdycji) {        
        miesiacOd.setEnabled(czyDoEdycji);
        miesiacDo.setEnabled(czyDoEdycji);
        rokOd.setEditable(czyDoEdycji);
        rokDo.setEditable(czyDoEdycji);
        groupIdField.setEditable(czyDoEdycji);
        generuj.setEnabled(czyDoEdycji);        
    }
    
    /** Creates new form StatsFrame */
    public StatsFrame() {
        
        initComponents();
        
        try {
            
            kgui = new KontrolerGUI(
                postepOperacji, 
                this, 
                authLabel, 
                groupIdField);
            
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
        try {
            kgui.autoryzuj();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
        /*
         * Inicjujemy daty na miesiąc przed miesiącem bieżącym
         */
        {
            Calendar c = Calendar.getInstance();
            
            rokDo.setText(""+c.get(Calendar.YEAR));
            miesiacDo.setSelectedIndex(c.get(Calendar.MONTH));
            
            /*
             * Jeżeli jesteśmy w styczniu to analizujemy poprzedni rok
             */
            if (c.get(Calendar.MONTH) == Calendar.JANUARY) {
                rokOd.setText(""+(c.get(Calendar.YEAR)-1));
                miesiacOd.setSelectedIndex(Calendar.DECEMBER);
            } else {
                rokOd.setText(""+c.get(Calendar.YEAR));
                miesiacOd.setSelectedIndex(c.get(Calendar.MONTH)-1);
            }
        }
        
        akcjaZmianaWartosciIdGrupy();
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        rokOd = new javax.swing.JTextField();
        rokDo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        miesiacOd = new javax.swing.JComboBox();
        miesiacDo = new javax.swing.JComboBox();
        postepOperacji = new javax.swing.JProgressBar();
        generuj = new javax.swing.JButton();
        authLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        groupIdField = new javax.swing.JTextField();
        groupNameField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JFlickrGroupStats");
        setResizable(false);

        jLabel1.setText("Od:");

        jLabel2.setText("Do:");

        rokOd.setText("2008");
        rokOd.setInputVerifier(new TylkoPoprawnyRok());

        rokDo.setText("2008");
        rokDo.setInputVerifier(new TylkoPoprawnyRok());

        jLabel3.setText("/");

        jLabel4.setText("/");

        miesiacOd.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        miesiacOd.setSelectedIndex(7);

        miesiacDo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        miesiacDo.setSelectedIndex(8);

        postepOperacji.setFocusable(false);

        generuj.setText("Generuj");
        generuj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generujActionPerformed(evt);
            }
        });

        authLabel.setText("...");

        jLabel5.setText("ID grupy:");

        groupIdField.setText("71956997@N00");
        groupIdField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groupIdFieldActionPerformed(evt);
            }
        });

        groupNameField.setEditable(false);

        jLabel6.setText("Grupa:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(postepOperacji, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(generuj)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(authLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rokOd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(miesiacOd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rokDo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(miesiacDo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(groupNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                            .addComponent(groupIdField, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(rokOd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(miesiacOd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(groupIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rokDo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(miesiacDo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(groupNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addComponent(postepOperacji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(generuj)
                    .addComponent(authLabel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void generujActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generujActionPerformed
    akcjaGeneruj();
}//GEN-LAST:event_generujActionPerformed

private void groupIdFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupIdFieldActionPerformed
    akcjaZmianaWartosciIdGrupy();
}//GEN-LAST:event_groupIdFieldActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel authLabel;
    private javax.swing.JButton generuj;
    private javax.swing.JTextField groupIdField;
    private javax.swing.JTextField groupNameField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JComboBox miesiacDo;
    private javax.swing.JComboBox miesiacOd;
    private javax.swing.JProgressBar postepOperacji;
    private javax.swing.JTextField rokDo;
    private javax.swing.JTextField rokOd;
    // End of variables declaration//GEN-END:variables

    public JFrame dajOwner() {
        return this;
    }

    public Date dajDataOd() throws ParseException {
        return 
            DateFormat.getDateInstance(DateFormat.SHORT).parse(
                "01."+miesiacOd.getSelectedItem()+"."+rokOd.getText());                
    }

    public Date dajDataDo() throws ParseException {
        return
            DateFormat.getDateInstance(DateFormat.SHORT).parse(
                "01."+miesiacDo.getSelectedItem()+"."+rokDo.getText());        
    }

}
