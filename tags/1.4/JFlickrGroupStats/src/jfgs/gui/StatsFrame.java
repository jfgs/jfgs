/*
 *  This file is part of JFlickrGroupStats.
 *
 *  JFlickrGroupStats is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JFlickrGroupStats is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JFlickrGroupStats.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package jfgs.gui;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import jfgs.narzedzia.Constants;
import jfgs.narzedzia.IPanelKonfiguracyjny;
import jfgs.narzedzia.PogromcaLogiki;

/**
 *
 * @author  michalus
 */
public class StatsFrame extends javax.swing.JFrame implements IStats {

    /**
     * @FIXME Stała dla grupy Szczere Komentarze, na szybko
     */
    private final String SKid = "71956997@N00";

    /**
     * @FIXME Stała dla grupy Polacy fotografujący (1/1) , na szybko
     */
    private final String PFid = "43929664@N00";

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
        PogromcaLogiki.wykonaj(rodzajLogiki.getSelectedItem(), kgui);
    }
    
    /**
     * Włączenie/wyłączenie edycji edytowalnych komponentów na formularzu
     * @param czyDoEdycji
     */
    private void stanKomponentów(boolean czyDoEdycji) {        
        
        /*
         * Pierwsza zakładka
         */
        miesiacOd.setEnabled(czyDoEdycji);
        miesiacDo.setEnabled(czyDoEdycji);
        rokOd.setEditable(czyDoEdycji);
        rokDo.setEditable(czyDoEdycji);
        groupIdField.setEditable(czyDoEdycji);
        generuj.setEnabled(czyDoEdycji);
        rodzajLogiki.setEnabled(czyDoEdycji);

        /*
         * Druga zakładka
         */
        if (kgui.getPanelKonfiguracyjny() != null) {
            kgui.getPanelKonfiguracyjny().stanKomponentow(czyDoEdycji);
        }

        /*
         * Menu
         */
        jmDodaj.setEnabled(czyDoEdycji);
        jmOdejmij.setEnabled(czyDoEdycji);
        jmDzis.setEnabled(czyDoEdycji);
        jmGeneruj.setEnabled(czyDoEdycji);
        jmSK.setEnabled(czyDoEdycji);
        jmPf.setEnabled(czyDoEdycji);
        
    }
    
    /** Creates new form StatsFrame */
    public StatsFrame() {
        
        initComponents();

        setTitle(Constants.title);

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
        
        ustawDateDzisiejsza();

        groupIdField.setText(SKid);

        akcjaZmianaWartosciIdGrupy();

        przeladujPanelKonfiguracji();
        
    }

    /*
     * Inicjujemy daty na miesiąc przed miesiącem bieżącym
     */
    private void ustawDateDzisiejsza() {

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

    private void przeladujPanelKonfiguracji() {
        
        panelKonfiguracja.removeAll();
        panelKonfiguracja.setLayout(new BorderLayout());

        IPanelKonfiguracyjny pk =
            PogromcaLogiki.dajPanelKonfiguracyjny(
                rodzajLogiki.getSelectedItem());

        if (pk != null) {
            panelKonfiguracja.add((JPanel) pk.getPanel(), BorderLayout.CENTER);
            panelKonfiguracja.setEnabled(true);
        } else {
            panelKonfiguracja.setEnabled(false);
        }

        kgui.setPanelKoniguracyjny(pk);

        panelKonfiguracja.revalidate();

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelKryteria = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        rokOd = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        rokDo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        miesiacOd = new javax.swing.JComboBox();
        miesiacDo = new javax.swing.JComboBox();
        postepOperacji = new javax.swing.JProgressBar();
        generuj = new javax.swing.JButton();
        authLabel = new javax.swing.JLabel();
        groupIdField = new javax.swing.JTextField();
        groupNameField = new javax.swing.JTextField();
        rodzajLogiki = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        panelKonfiguracja = new javax.swing.JPanel();
        jMenu = new javax.swing.JMenuBar();
        jmPlik = new javax.swing.JMenu();
        jmGeneruj = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jmZakoncz = new javax.swing.JMenuItem();
        jmEdytuj = new javax.swing.JMenu();
        jmDzis = new javax.swing.JMenuItem();
        jmDodaj = new javax.swing.JMenuItem();
        jmOdejmij = new javax.swing.JMenuItem();
        jmUlubione = new javax.swing.JMenu();
        jmSK = new javax.swing.JMenuItem();
        jmPf = new javax.swing.JMenuItem();
        jmPomoc = new javax.swing.JMenu();
        jmOProgramie = new javax.swing.JMenuItem();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel1.setText("Od:");

        rokOd.setText("2008");
        rokOd.setInputVerifier(new TylkoPoprawnyRok());

        jLabel2.setText("Do:");

        rokDo.setText("2008");
        rokDo.setInputVerifier(new TylkoPoprawnyRok());

        jLabel3.setText("/");

        jLabel4.setText("/");

        miesiacOd.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        miesiacOd.setSelectedIndex(7);

        miesiacDo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        miesiacDo.setSelectedIndex(8);

        postepOperacji.setFocusable(false);
        postepOperacji.setString("");
        postepOperacji.setStringPainted(true);

        generuj.setText("Generuj");
        generuj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generujActionPerformed(evt);
            }
        });

        authLabel.setText("...");

        groupIdField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groupIdFieldActionPerformed(evt);
            }
        });

        groupNameField.setEditable(false);

        rodzajLogiki.setModel(PogromcaLogiki.dajModelDlaRodzaju());
        rodzajLogiki.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rodzajLogikiActionPerformed(evt);
            }
        });

        jLabel5.setText("ID grupy:");

        jLabel6.setText("Grupa:");

        jLabel7.setText("Rodzaj:");

        javax.swing.GroupLayout panelKryteriaLayout = new javax.swing.GroupLayout(panelKryteria);
        panelKryteria.setLayout(panelKryteriaLayout);
        panelKryteriaLayout.setHorizontalGroup(
            panelKryteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKryteriaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelKryteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelKryteriaLayout.createSequentialGroup()
                        .addComponent(generuj)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(authLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE))
                    .addGroup(panelKryteriaLayout.createSequentialGroup()
                        .addGroup(panelKryteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelKryteriaLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rokDo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4))
                            .addGroup(panelKryteriaLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rokOd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)))
                        .addGap(6, 6, 6)
                        .addGroup(panelKryteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(miesiacDo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(miesiacOd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(175, 175, 175)
                        .addGroup(panelKryteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelKryteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(groupIdField, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                            .addComponent(groupNameField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                            .addComponent(rodzajLogiki, 0, 186, Short.MAX_VALUE)))
                    .addComponent(postepOperacji, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelKryteriaLayout.setVerticalGroup(
            panelKryteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKryteriaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelKryteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(rokOd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(groupIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel3)
                    .addComponent(miesiacOd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelKryteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(rokDo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(groupNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(miesiacDo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelKryteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rodzajLogiki, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addComponent(postepOperacji, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelKryteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(generuj)
                    .addComponent(authLabel))
                .addGap(9, 9, 9))
        );

        jTabbedPane1.addTab("Kryteria", panelKryteria);

        javax.swing.GroupLayout panelKonfiguracjaLayout = new javax.swing.GroupLayout(panelKonfiguracja);
        panelKonfiguracja.setLayout(panelKonfiguracjaLayout);
        panelKonfiguracjaLayout.setHorizontalGroup(
            panelKonfiguracjaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 605, Short.MAX_VALUE)
        );
        panelKonfiguracjaLayout.setVerticalGroup(
            panelKonfiguracjaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 238, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Konfiguracja", panelKonfiguracja);

        jmPlik.setMnemonic('P');
        jmPlik.setText("Plik");

        jmGeneruj.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        jmGeneruj.setMnemonic('G');
        jmGeneruj.setText("Generuj");
        jmGeneruj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmGenerujActionPerformed(evt);
            }
        });
        jmPlik.add(jmGeneruj);
        jmPlik.add(jSeparator1);

        jmZakoncz.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jmZakoncz.setMnemonic('Z');
        jmZakoncz.setText("Zakończ");
        jmZakoncz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmZakonczActionPerformed(evt);
            }
        });
        jmPlik.add(jmZakoncz);

        jMenu.add(jmPlik);

        jmEdytuj.setMnemonic('E');
        jmEdytuj.setText("Edytuj");

        jmDzis.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        jmDzis.setText("Data dzisiejsza");
        jmDzis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmDzisActionPerformed(evt);
            }
        });
        jmEdytuj.add(jmDzis);

        jmDodaj.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jmDodaj.setMnemonic('D');
        jmDodaj.setText("Dodaj miesiąc");
        jmDodaj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmDodajActionPerformed(evt);
            }
        });
        jmEdytuj.add(jmDodaj);

        jmOdejmij.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jmOdejmij.setMnemonic('O');
        jmOdejmij.setText("Odejmij miesiąc");
        jmOdejmij.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmOdejmijActionPerformed(evt);
            }
        });
        jmEdytuj.add(jmOdejmij);

        jMenu.add(jmEdytuj);

        jmUlubione.setMnemonic('U');
        jmUlubione.setText("Ulubione");

        jmSK.setText("Szczere Komentarze");
        jmSK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmSKActionPerformed(evt);
            }
        });
        jmUlubione.add(jmSK);

        jmPf.setText("Polacy fotografujący (1/1) ");
        jmPf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmPfActionPerformed(evt);
            }
        });
        jmUlubione.add(jmPf);

        jMenu.add(jmUlubione);

        jmPomoc.setMnemonic('P');
        jmPomoc.setText("Pomoc");

        jmOProgramie.setMnemonic('O');
        jmOProgramie.setText("O programie");
        jmOProgramie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmOProgramieActionPerformed(evt);
            }
        });
        jmPomoc.add(jmOProgramie);

        jMenu.add(jmPomoc);

        setJMenuBar(jMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void generujActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generujActionPerformed
    akcjaGeneruj();
}//GEN-LAST:event_generujActionPerformed

private void groupIdFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupIdFieldActionPerformed
    akcjaZmianaWartosciIdGrupy();
}//GEN-LAST:event_groupIdFieldActionPerformed

private void jmZakonczActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmZakonczActionPerformed
    menuZakoncz();
}//GEN-LAST:event_jmZakonczActionPerformed

private void jmDodajActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmDodajActionPerformed
    menuDodajMiesiac();
}//GEN-LAST:event_jmDodajActionPerformed

private void jmOdejmijActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmOdejmijActionPerformed
    menuOdejmijMiesiac();
}//GEN-LAST:event_jmOdejmijActionPerformed

private void jmDzisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmDzisActionPerformed
    ustawDateDzisiejsza();
}//GEN-LAST:event_jmDzisActionPerformed

private void jmGenerujActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmGenerujActionPerformed
    akcjaGeneruj();
}//GEN-LAST:event_jmGenerujActionPerformed

private void jmSKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmSKActionPerformed
    groupIdField.setText(SKid);
    akcjaZmianaWartosciIdGrupy();
}//GEN-LAST:event_jmSKActionPerformed

private void jmPfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmPfActionPerformed
    groupIdField.setText(PFid);
    akcjaZmianaWartosciIdGrupy();
}//GEN-LAST:event_jmPfActionPerformed

private void rodzajLogikiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rodzajLogikiActionPerformed
    przeladujPanelKonfiguracji();
}//GEN-LAST:event_rodzajLogikiActionPerformed

private void jmOProgramieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmOProgramieActionPerformed

    JOptionPane.showMessageDialog(
        this,
        "<html>" +
            "<p>" + Constants.title + "</p>" +
            "<p>Licencja: <a href=\"http://www.gnu.org/licenses/gpl.html\">GNU General Public License v3</a></p>" +
            "<p>Strona domowa: <a href=\"http://code.google.com/p/jfgs/\">code.google.com/p/jfgs</a></p>" +
        "</html>",
        "O programie",
        JOptionPane.INFORMATION_MESSAGE
    );

}//GEN-LAST:event_jmOProgramieActionPerformed
    
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
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenuBar jMenu;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenuItem jmDodaj;
    private javax.swing.JMenuItem jmDzis;
    private javax.swing.JMenu jmEdytuj;
    private javax.swing.JMenuItem jmGeneruj;
    private javax.swing.JMenuItem jmOProgramie;
    private javax.swing.JMenuItem jmOdejmij;
    private javax.swing.JMenuItem jmPf;
    private javax.swing.JMenu jmPlik;
    private javax.swing.JMenu jmPomoc;
    private javax.swing.JMenuItem jmSK;
    private javax.swing.JMenu jmUlubione;
    private javax.swing.JMenuItem jmZakoncz;
    private javax.swing.JComboBox miesiacDo;
    private javax.swing.JComboBox miesiacOd;
    private javax.swing.JPanel panelKonfiguracja;
    private javax.swing.JPanel panelKryteria;
    private javax.swing.JProgressBar postepOperacji;
    private javax.swing.JComboBox rodzajLogiki;
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

    private void menuZakoncz() {
        System.exit(1);
    }

    private void menuDodajMiesiac() {
        zmienDate(1);
    }

    private void menuOdejmijMiesiac() {
        zmienDate(-1);
    }

    private void zmienDate(int miesiace) {

        Calendar cOd = Calendar.getInstance();
        Calendar cDo = Calendar.getInstance();

        try {

            cOd.setTime(dajDataOd());
            cDo.setTime(dajDataDo());
            cOd.add(Calendar.MONTH, miesiace);
            cDo.add(Calendar.MONTH, miesiace);

            rokOd.setText(""+(cOd.get(Calendar.YEAR)));
            miesiacOd.setSelectedIndex(cOd.get(Calendar.MONTH));
            rokDo.setText(""+(cDo.get(Calendar.YEAR)));
            miesiacDo.setSelectedIndex(cDo.get(Calendar.MONTH));

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}
