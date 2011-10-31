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

package jfgs.zm;

import javax.swing.JPanel;
import jfgs.narzedzia.IPanelKonfiguracyjny;

/**
 * Panel konfiguracyjny dla klasy ZdjecieMiesiaca
 *
 * @see ZdjecieMiesiaca
 * @author michalus
 */
public class PanelKonfiguracji extends javax.swing.JPanel implements IPanelKonfiguracyjny {

    /** Creates new form PanelKonfiguracji */
    public PanelKonfiguracji() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rgSposobPodsumowania = new javax.swing.ButtonGroup();
        cbPodsumowanieZbiorcze = new javax.swing.JCheckBox();
        cbPodsumowaniePopularnosci = new javax.swing.JCheckBox();
        cbKostkaMiniatur = new javax.swing.JCheckBox();
        rbWykresSlupkowy = new javax.swing.JRadioButton();
        rbLista = new javax.swing.JRadioButton();
        cbKodHTML = new javax.swing.JCheckBox();
        cbBezKomentarzy = new javax.swing.JCheckBox();
        cbBezZdjec = new javax.swing.JCheckBox();
        lbBezKomentarzy = new javax.swing.JComboBox();
        lbBezZdjec = new javax.swing.JComboBox();
        lBezKomentarzy = new javax.swing.JLabel();
        lBezZdjec = new javax.swing.JLabel();

        cbPodsumowanieZbiorcze.setSelected(true);
        cbPodsumowanieZbiorcze.setText("Drukuj podsumowanie zbiorcze");
        cbPodsumowanieZbiorcze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPodsumowanieZbiorczeActionPerformed(evt);
            }
        });

        cbPodsumowaniePopularnosci.setText("Drukuj podsumowanie popularności");

        cbKostkaMiniatur.setText("Drukuj kostkę miniatur");

        rgSposobPodsumowania.add(rbWykresSlupkowy);
        rbWykresSlupkowy.setText("Wykres słupkowy");

        rgSposobPodsumowania.add(rbLista);
        rbLista.setSelected(true);
        rbLista.setText("Lista wartości");

        cbKodHTML.setSelected(true);
        cbKodHTML.setText("Drukuj kod HTML");

        cbBezKomentarzy.setText("Drukuj brak komentarzy w ciągu");
        cbBezKomentarzy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBezKomentarzyActionPerformed(evt);
            }
        });

        cbBezZdjec.setText("Drukuj brak zdjęć w ciągu");
        cbBezZdjec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBezZdjecActionPerformed(evt);
            }
        });

        lbBezKomentarzy.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "3", "6", "12" }));
        lbBezKomentarzy.setSelectedIndex(1);

        lbBezZdjec.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "3", "6", "12" }));
        lbBezZdjec.setSelectedIndex(2);

        lBezKomentarzy.setText("miesięcy.");

        lBezZdjec.setText("miesięcy.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbPodsumowanieZbiorcze)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbLista)
                            .addComponent(rbWykresSlupkowy)))
                    .addComponent(cbPodsumowaniePopularnosci)
                    .addComponent(cbKostkaMiniatur)
                    .addComponent(cbKodHTML)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cbBezKomentarzy)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbBezKomentarzy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lBezKomentarzy))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cbBezZdjec)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbBezZdjec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lBezZdjec)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbPodsumowanieZbiorcze)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbWykresSlupkowy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbLista)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbPodsumowaniePopularnosci)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbKostkaMiniatur)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbKodHTML)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbBezKomentarzy)
                    .addComponent(lbBezKomentarzy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lBezKomentarzy))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbBezZdjec)
                    .addComponent(lbBezZdjec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lBezZdjec))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbPodsumowanieZbiorczeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPodsumowanieZbiorczeActionPerformed
        if(cbPodsumowanieZbiorcze.isSelected()) {
            rbLista.setEnabled(true);
            rbWykresSlupkowy.setEnabled(true);
        } else {
            rgSposobPodsumowania.clearSelection();
            rbLista.setEnabled(false);
            rbWykresSlupkowy.setEnabled(false);
        }
    }//GEN-LAST:event_cbPodsumowanieZbiorczeActionPerformed

    private void cbBezKomentarzyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBezKomentarzyActionPerformed
        lbBezKomentarzy.setEnabled(cbBezKomentarzy.isSelected());
    }//GEN-LAST:event_cbBezKomentarzyActionPerformed

    private void cbBezZdjecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBezZdjecActionPerformed
        lbBezZdjec.setEnabled(cbBezZdjec.isSelected());
    }//GEN-LAST:event_cbBezZdjecActionPerformed

    public boolean dajPodsumowanieZbiorcze() {
        return cbPodsumowanieZbiorcze.isSelected();
    }

    public boolean dajPodsumowaniePopularnosci() {
        return cbPodsumowaniePopularnosci.isSelected();
    }

    public boolean dajKostkeMiniatur() {
        return cbKostkaMiniatur.isSelected();
    }

    public boolean dajKodHTML() {
        return cbKodHTML.isSelected();
    }

    public boolean dajWykresLista() {
        return rbLista.isSelected();
    }

    public boolean dajWykresSlupkowy() {
        return rbWykresSlupkowy.isSelected();
    }

    public boolean dajDrukujBezZdjec() {
        return cbBezZdjec.isSelected();
    }

    public boolean dajDrukujBezKomentarzy() {
        return cbBezKomentarzy.isSelected();
    }

    public Integer dajLiczbeMcBezZdjec() {
        Integer mc = null;
        try {
            mc = Integer.parseInt((String) lbBezZdjec.getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
            mc = new Integer(1);
        }
        return mc;
    }

    public Integer dajLiczbeMcBezKomentarzy() {
        Integer mc = null;
        try {
            mc = Integer.parseInt((String) lbBezKomentarzy.getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
            mc = new Integer(1);
        }
        return mc;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbBezKomentarzy;
    private javax.swing.JCheckBox cbBezZdjec;
    private javax.swing.JCheckBox cbKodHTML;
    private javax.swing.JCheckBox cbKostkaMiniatur;
    private javax.swing.JCheckBox cbPodsumowaniePopularnosci;
    private javax.swing.JCheckBox cbPodsumowanieZbiorcze;
    private javax.swing.JLabel lBezKomentarzy;
    private javax.swing.JLabel lBezZdjec;
    private javax.swing.JComboBox lbBezKomentarzy;
    private javax.swing.JComboBox lbBezZdjec;
    private javax.swing.JRadioButton rbLista;
    private javax.swing.JRadioButton rbWykresSlupkowy;
    private javax.swing.ButtonGroup rgSposobPodsumowania;
    // End of variables declaration//GEN-END:variables

    public void stanKomponentow(boolean czyDoEdycji) {
        cbKodHTML.setEnabled(czyDoEdycji);
        cbKostkaMiniatur.setEnabled(czyDoEdycji);
        cbPodsumowaniePopularnosci.setEnabled(czyDoEdycji);
        cbPodsumowanieZbiorcze.setEnabled(czyDoEdycji);
        rbLista.setEnabled(czyDoEdycji);
        rbWykresSlupkowy.setEnabled(czyDoEdycji);
        cbBezKomentarzy.setEnabled(czyDoEdycji);
        cbBezZdjec.setEnabled(czyDoEdycji);
        lbBezKomentarzy.setEnabled(czyDoEdycji);
        lbBezZdjec.setEnabled(czyDoEdycji);
        lBezKomentarzy.setEnabled(czyDoEdycji);
        lBezZdjec.setEnabled(czyDoEdycji);
    }

    public JPanel getPanel() {
        return this;
    }

}
