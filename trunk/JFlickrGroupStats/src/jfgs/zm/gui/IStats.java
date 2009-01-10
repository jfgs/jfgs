/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfgs.zm.gui;

import java.text.ParseException;
import java.util.Date;
import javax.swing.JFrame;

/**
 *
 * @author michalus
 */
public interface IStats {
    
    public JFrame dajOwner();
    
    public Date dajDataOd() throws ParseException;
    
    public Date dajDataDo() throws ParseException;
    
    
}
