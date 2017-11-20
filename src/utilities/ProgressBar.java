/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.awt.BorderLayout;
import java.awt.Container;
import java.time.LocalDateTime;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

/**
 *
 * @author atlantis
 */
public class ProgressBar {

    public static JProgressBar pbar;
            
    /**
     *
     * @param size - Size of buffer 
 
     */
    /*
    public void CreateProgressBarStockList (int size)
    {
    
                //Where the GUI is constructed:
        pbar = new JProgressBar(0, size);
        pbar.setValue(0);
        pbar.setStringPainted(true);        
        
        JFrame f = new JFrame("Build Brand new Database with "+size+" symbols and "+YEAR_HISTORY_INT+" Year history. Be patient, this will take a while.");
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container content = f.getContentPane();
        pbar.setStringPainted(true);
        Border border = BorderFactory.createTitledBorder("Reading...");
        pbar.setBorder(border);
        content.add(pbar, BorderLayout.NORTH);
        f.setSize(1500, 100);
        f.setVisible(true);
       
    }
*/
        public void CreateProgressBarStockList (int size, String title)
    {
    
                //Where the GUI is constructed:
        pbar = new JProgressBar(0, size);
        pbar.setValue(0);
        pbar.setStringPainted(true);        
        
        JFrame f = new JFrame(title);
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container content = f.getContentPane();
        pbar.setStringPainted(true);
        Border border = BorderFactory.createTitledBorder("Reading...");
        pbar.setBorder(border);
        content.add(pbar, BorderLayout.NORTH);
        f.setSize(1500, 100);
        f.setVisible(true);
       
    }

    public static void UpdatePosition (ProgressBar Pbar, int pos,int total, LocalDateTime startTime)
    {
        LocalDateTime elapsedTime;
        
        String stringpos = String.valueOf(pos)+" of "+String.valueOf(total); // "Estimate completed: ";
        pbar.setString(stringpos);
        pbar.setValue(pos);

    }

    public static void ProgressBarEta (int pos,int total, LocalDateTime startTime)
    {

//      Long startTime = System.nanoTime();
//     Long elapsedTime = System.nanoTime() - startTime;
//     Long allTimeForDownloading = (elapsedTime * pos / total);
//     Long remainingTime = allTimeForDownloading - elapsedTime;
     
    }

    
    public static void CloseProgressBar ()
    {
        pbar.removeAll();
    }
    
}
