/*
 */

/*
 * PanelKonfiguracji.java
 *
 * Created on 2009-06-14, 14:17:07
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

        cbPodsumowanieZbiorcze.setSelected(true);
        cbPodsumowanieZbiorcze.setText("Drukuj podsumowanie zbiorcze");
        cbPodsumowanieZbiorcze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPodsumowanieZbiorczeActionPerformed(evt);
            }
        });

        cbPodsumowaniePopularnosci.setSelected(true);
        cbPodsumowaniePopularnosci.setText("Drukuj podsumowanie popularności");

        cbKostkaMiniatur.setSelected(true);
        cbKostkaMiniatur.setText("Drukuj kostkę miniatur");

        rgSposobPodsumowania.add(rbWykresSlupkowy);
        rbWykresSlupkowy.setSelected(true);
        rbWykresSlupkowy.setText("Wykres słupkowy");

        rgSposobPodsumowania.add(rbLista);
        rbLista.setText("Lista wartości");

        cbKodHTML.setSelected(true);
        cbKodHTML.setText("Drukuj kod HTML");

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
                    .addComponent(cbKodHTML))
                .addContainerGap(138, Short.MAX_VALUE))
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
                .addContainerGap(126, Short.MAX_VALUE))
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbKodHTML;
    private javax.swing.JCheckBox cbKostkaMiniatur;
    private javax.swing.JCheckBox cbPodsumowaniePopularnosci;
    private javax.swing.JCheckBox cbPodsumowanieZbiorcze;
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
    }

    public JPanel getPanel() {
        return this;
    }

}
