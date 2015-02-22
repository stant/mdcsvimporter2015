package com.moneydance.modules.features.mdcsvimporter;

import static com.moneydance.modules.features.mdcsvimporter.formats.CustomReader.DATA_TYPE_IGNORE;
import static com.moneydance.modules.features.mdcsvimporter.formats.CustomReader.DATA_TYPE_IGNORE_REST;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 *
 * @author stan
 */


public class PreviewImportWin extends javax.swing.JFrame {

    private TransactionReader transReader = null;
    private CSVData csvData = null;
            
    /**
     * Creates new form PreviewImportWin
     */
    public PreviewImportWin() {
        initComponents();
    }

    public void myInit( ImportDialog importDialog, TransactionReader transReaderArg, CSVData csvDataArg, CSVReader csvReader )
    {
      System.err.println( "entered PreviewImportWin.myInit()" + "< ==============================" );
      transReader = transReaderArg;
      csvData = csvDataArg;
      boolean gotError = false;
      
        try {
            if ( transReader.canParse( csvData ) )
                  {
                  this.setTitle( "For Reader: " + transReader.toString() + "    - Parse file works having " + csvData.getData().length + " rows" );
                  importDialog.btnProcess.setEnabled( true );
                  System.err.println( "=============== at canparse WORKS for >" + transReader.getFormatName() + "< ===============" );
                  }
            else
                  {
                  this.setTitle( "For Reader: " + transReader.toString() + "    - Parse file does not work!" );
                  importDialog.btnProcess.setEnabled( false );
                  System.err.println( "=============== at canparse NOT WORK for >" + transReader.getFormatName() + " at row,col " 
                          + csvData.getCurrentLineIndex() + "," + csvData.getCurrentFieldIndex() + "< ===============" );
                  gotError = true;
                  }
      
            //csvData.parseIntoLines( transReader.getCustomReaderData().getFieldSeparatorChar() );
            System.err.println( "after parse row count =" + csvData.getData().length );
            System.err.println( "after parse col count =" + (csvData.getData())[0].length );

            // Find and Insert User DataTypes as a Header row to show if they line up:
            
            int fieldIndex = 0;
            int maxFieldIndex = transReader.getCustomReaderData().getNumberOfCustomReaderFieldsUsed();
            System.err.println(  "maxFieldIndex =" + maxFieldIndex );
            ArrayList<String> headerDataTypesList = new ArrayList();
            
            for (           ; fieldIndex < maxFieldIndex; fieldIndex ++ )
                {
                String dataTypeExpecting = transReader.getCustomReaderData().getDataTypesList().get( fieldIndex );
                System.err.println(  "dataTypeExpecting =" + dataTypeExpecting + "=  fieldIndex = " + fieldIndex );

                if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_IGNORE_REST ) )
                   {
                   headerDataTypesList.add( dataTypeExpecting );
                   break;
                   }
                else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_IGNORE ) )
                   {
                   int x = 1;
                   try
                       {
                       x = Integer.parseInt( transReader.getCustomReaderData().getEmptyFlagsList().get( fieldIndex ).trim() );
                       System.err.println(  "ignore " + x + " lines" );
                       }
                   catch ( Exception ex )
                       {
                       System.err.println(  "ignore 1 line by erro on field =" + transReader.getCustomReaderData().getEmptyFlagsList().get( fieldIndex ).trim() + "=" );
                       }
                   int cnt = x;
                   headerDataTypesList.add( dataTypeExpecting + "-" + cnt );
                   while ( x > 1 )
                       {
                       headerDataTypesList.add( dataTypeExpecting + "-" + cnt );
                       x--;
                       }
                   }
                else
                   {
                   headerDataTypesList.add( dataTypeExpecting );
                   }
                }
            previewImportTbl.setModel( new PreviewImportTblModel( headerDataTypesList, csvData.getData() ) );
            if ( gotError )
                {
                    //csvData.getCurrentLineIndex() + "," + csvData.getCurrentFieldIndex()
                CustomTableCellRenderer customTableCellRenderer = new CustomTableCellRenderer();
                customTableCellRenderer.setForRowCol( csvData.getCurrentLineIndex(), csvData.getCurrentFieldIndex() );
                //previewImportTbl.getColumnModel().getColumn( csvData.getCurrentFieldIndexWithinBounds() ).setCellRenderer( customTableCellRenderer );
                previewImportTbl.setDefaultRenderer( Object.class, customTableCellRenderer );
                }
            } 
        catch (Exception ex) 
            {
            //Logger.getLogger(CustomReader.class.getName()).log(Level.SEVERE, null, ex);
            //return false;
            }
        finally
            {
            try
                {
                csvReader.close();
                csvData = null;
                transReader = null;
                this.setSize( 800, 600 );
                this.setLocationRelativeTo( getRootPane() );
                this.setVisible( true );
                this.validate();
                this.addEscapeListener( this );
                }
            catch( Exception fex )
                {
                    ;
                }
            }
    }
    
    public static void addEscapeListener(final JFrame win) {
        ActionListener escListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //System.err.println( "previewImportWin formWindow dispose()" );
                win.dispose();
            }
        };

        win.getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }    

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                PreviewImportWin dialog = new PreviewImportWin();
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                    ArrayList<String> header = new ArrayList<String>(Arrays.asList( "H1", "H2", "H3" ) );
                    String[][] data = {
                    {"User", "Password", "Age"},
                    {"1", "2", "3"},                        
                    {"10", "20", "30"},                        
                    };

                //dialog.myInit( null, null );
                dialog.previewImportTbl.setModel( new PreviewImportTblModel( header, data ) );
                dialog.setSize( 800, 600 );
                dialog.setVisible(true);
                dialog.addEscapeListener( dialog );
            }
        });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        previewImportTbl = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        previewImportTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(previewImportTbl);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 456;
        gridBagConstraints.ipady = 400;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jScrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            System.err.println( "previewImportWin formWindowClosing()" );
        } catch (Exception ex) {
            Logger.getLogger(PreviewImportWin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable previewImportTbl;
    // End of variables declaration//GEN-END:variables
}
