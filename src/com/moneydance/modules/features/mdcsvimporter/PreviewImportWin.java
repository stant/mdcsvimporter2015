package com.moneydance.modules.features.mdcsvimporter;

import static com.moneydance.modules.features.mdcsvimporter.formats.CustomReader.DATA_TYPE_IGNORE;
import static com.moneydance.modules.features.mdcsvimporter.formats.CustomReader.DATA_TYPE_IGNORE_REST;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 *
 * @author stan
 */


public class PreviewImportWin extends javax.swing.JFrame {

    private ImportDialog importDialog = null;
    private TransactionReader transReader = null;
    private CSVData csvData = null;
    private CSVReader csvReader = null;
    
    /**
     * Creates new form PreviewImportWin
     */
    public PreviewImportWin() {
        try {
            initComponents();
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

    public void myInit( ImportDialog importDialog, TransactionReader transReaderArg )  //, CSVData csvDataArg, CSVReader csvReader )
    {
        Util.logConsole( "entered PreviewImportWin.myInit()" + "< ==============================" );
        this.importDialog = importDialog;
        transReader = transReaderArg;
        selectedFile.setText( importDialog.getSelectedFile().getPath() );
      
        parseFile();
    }
    
    public void parseFile()
    {
      boolean gotError = false;
      
        try {
            CSVReader csvReader = null;
    
            if ( transReader.getCustomReaderData().getUseRegexFlag() )
                {
                Util.logConsole( "\n================  Regex Reader" );
                csvReader = new RegexReader( new InputStreamReader( new FileInputStream( importDialog.getSelectedFile() ), Charset.forName( (String) transReader.getCustomReaderData().getFileEncoding() )), transReader.getCustomReaderData() );
                }
            else
                {
                Util.logConsole( "\n================  Csv Reader" );
                csvReader = new CSVReader( new InputStreamReader( new FileInputStream( importDialog.getSelectedFile() ), Charset.forName( (String) transReader.getCustomReaderData().getFileEncoding() )), transReader.getCustomReaderData() );
                }

            CSVData csvData = new CSVData( csvReader );            
       
            //Util.logConsole( "btnProcessActionPerformed  customReaderDialog.getFieldSeparatorChar() =" + (char)customReaderDialog.getFieldSeparatorChar() + "=" );
            //csvData.getReader().setFieldSeparator( customReaderDialog.getFieldSeparatorChar() );
                
            csvData.reset();
            if ( transReader.canParse( csvData, TransactionReader.PARSE_THRU_ERRORS_CONTINUE ) )
                  {
                  this.setTitle( "For Reader: " + transReader.toString() + "    - Parse file works having " + csvData.getData().length + " rows" );
                  importDialog.btnProcess.setEnabled( true );
                  processBtn.setEnabled( true );
                  processBtn.requestFocusInWindow();
                  Util.logConsole( "=============== at canparse WORKS for >" + transReader.getFormatName() + "< ===============" );
                  }
            else
                  {
                  this.setTitle( "For Reader: " + transReader.toString() + "    - Parse file does not work!" );
                  importDialog.btnProcess.setEnabled( false );
                  processBtn.setEnabled( false );
                  parseFileBtn.requestFocusInWindow();
                  Util.logConsole( "=============== at canparse NOT WORK for >" + transReader.getFormatName() + " at row,col " 
                          + csvData.getCurrentLineIndex() + "," + csvData.getCurrentFieldIndex() + "< ===============" );
                  gotError = true;
                  }
      
            //csvData.parseIntoLines( transReader.getCustomReaderData().getFieldSeparatorChar() );
            Util.logConsole( "after parse row count =" + csvData.getData().length );
            Util.logConsole( "after parse col count =" + (csvData.getData())[ transReader.getHeaderCount() ].length );

            // Find and Insert User DataTypes as a Header row to show if they line up:
            
            int fieldIndex = 0;
            int colCount = 0;
            int maxFieldIndex = transReader.getCustomReaderData().getNumberOfCustomReaderFieldsUsed();
            Util.logConsole(  "maxFieldIndex =" + maxFieldIndex );
            ArrayList<String> headerDataTypesList = new ArrayList();
            
            for (           ; fieldIndex < maxFieldIndex; fieldIndex ++ )
                {
                String dataTypeExpecting = transReader.getCustomReaderData().getDataTypesList().get( fieldIndex );
                //Util.logConsole(  "dataTypeExpecting =" + dataTypeExpecting + "=  fieldIndex = " + fieldIndex );

                if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_IGNORE_REST ) )
                   {
                   headerDataTypesList.add( dataTypeExpecting );
                   colCount++;
                   break;
                   }
                else if ( dataTypeExpecting.equalsIgnoreCase( DATA_TYPE_IGNORE ) )
                   {
                   int x = 1;
                   try
                       {
                       x = Integer.parseInt( transReader.getCustomReaderData().getEmptyFlagsList().get( fieldIndex ).trim() );
                       Util.logConsole(  "ignore " + x + " lines" );
                       }
                   catch ( Exception ex )
                       {
                       Util.logConsole(  "assume ignore 1 column on field =" + transReader.getCustomReaderData().getEmptyFlagsList().get( fieldIndex ).trim() + "=" );
                       }
                   int cnt = x;
                   headerDataTypesList.add( dataTypeExpecting + "-" + cnt );
                   colCount++;
                   while ( x > 1 )
                       {
                       headerDataTypesList.add( dataTypeExpecting + "-" + cnt );
                       colCount++;
                       x--;
                       }
                   }
                else
                   {
                   headerDataTypesList.add( dataTypeExpecting );
                   colCount++;
                   }
                }
            Util.logConsole( "after parse col count =" + colCount );
            previewImportTbl.setModel( new PreviewImportTblModel( headerDataTypesList, csvData.getData(),  colCount ) );

            CustomTableCellRenderer customTableCellRenderer = new CustomTableCellRenderer();
          //  previewImportTbl.setDefaultRenderer( Object.class, customTableCellRenderer );
            previewImportTbl.setDefaultRenderer( Object.class, customTableCellRenderer );
            
            // Colorize errors
            int totalErrs = 0;
            int maxr = csvData.getDataErr().length;
            for ( int r = 0; r < maxr; r++ )
               {
                //Util.logConsole( "Check Data Err row =" + r );
                int maxc = (csvData.getDataErr())[r].length;
                for ( int c = 0; c < maxc; c++ )
                  {
                  //Util.logConsole( "Check Data Err col =" + c );
                    if ( ! csvData.getFieldErr(r, c).equals( "" ) )
                      {
                      Util.logConsole( "dataErr [" + r + "][" + c + "] =" + csvData.getFieldErr(r, c) );
                      customTableCellRenderer.setForRowCol( r, c, csvData.getFieldErr(r, c) );
                      totalErrs++;
                      //previewImportTbl.getColumnModel().getColumn( csvData.getCurrentFieldIndexWithinBounds() ).setCellRenderer( customTableCellRenderer );
                      }
                  }
               }
            message.setText( "Errors =" + totalErrs );
            
//            if ( 1 == 2 && gotError )
//                {
//                    //csvData.getCurrentLineIndex() + "," + csvData.getCurrentFieldIndex()
//                CustomTableCellRenderer customTableCellRenderer = new CustomTableCellRenderer();
//                customTableCellRenderer.setForRowCol( csvData.getCurrentLineIndex(), csvData.getCurrentFieldIndex() );
//                //previewImportTbl.getColumnModel().getColumn( csvData.getCurrentFieldIndexWithinBounds() ).setCellRenderer( customTableCellRenderer );
//                previewImportTbl.setDefaultRenderer( Object.class, customTableCellRenderer );
//                }
            } 
        catch (Exception ex) 
            {
            //Logger.getLogger(CustomReader.class.getName()).log(Level.SEVERE, null, ex);
            //return false;
            }
        }
    
    public void addEscapeListener(final JFrame win) {
        ActionListener escListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //Util.logConsole( "previewImportWin formWindow dispose()" );
                    if (csvReader != null) {
                        csvReader.close();
                    }
                    csvData = null;
                    transReader = null;
                } catch (IOException ex) {
                    Logger.getLogger(PreviewImportWin.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally
                    {
                    win.dispatchEvent( new WindowEvent( win, WindowEvent.WINDOW_CLOSING )); 
                    win.dispose();
                    }
            }
        };

        win.getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }    

        public void desktopEdit( File file )
        {
        //File file = fpath.toFile();
        //first check if Desktop is supported by Platform or not
        if ( ! Desktop.isDesktopSupported() )
            {
            System.out.println("Desktop is not supported");
            return;
            }
         
        Desktop desktop = Desktop.getDesktop();
        try {
            if ( file.exists() )
                {
                desktop.edit( file );
                }
            } 
        catch (Exception ex) 
            {
            //logger.log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog( this, "Edit not supported in this desktop.\nWill try Open.", "Error", JOptionPane.ERROR_MESSAGE );
            desktopOpen( file );
            }
        }

    public void desktopOpen( File file )
        {
        //File file = fpath.toFile();
        //first check if Desktop is supported by Platform or not
        if ( ! Desktop.isDesktopSupported() )
            {
            System.out.println("Desktop is not supported");
            return;
            }
         
        Desktop desktop = Desktop.getDesktop();
        try {
            if ( file.exists() )
                {
                desktop.open( file );
                }
            } 
        catch (Exception ex) 
            {
            //logger.log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog( this, "Open not supported in this desktop", "Error", JOptionPane.ERROR_MESSAGE );
            }
        
//        //let's try to open PDF file
//        fpath = new File("/Users/pankaj/java.pdf");
//        if(fpath.exists()) desktop.open(fpath);
        
//        //let's try to open PDF file
//        fpath = new File("/Users/pankaj/java.pdf");
//        if(fpath.exists()) desktop.open(fpath);
        
//        //let's try to open PDF file
//        fpath = new File("/Users/pankaj/java.pdf");
//        if(fpath.exists()) desktop.open(fpath);
        
//        //let's try to open PDF file
//        fpath = new File("/Users/pankaj/java.pdf");
//        if(fpath.exists()) desktop.open(fpath);
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
                dialog.previewImportTbl.setModel( new PreviewImportTblModel( header, data, 3 ) );
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
        processBtn = new javax.swing.JButton();
        deleteFileBtn = new javax.swing.JButton();
        parseFileBtn = new javax.swing.JButton();
        message = new javax.swing.JLabel();
        selectedFile = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

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
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 456;
        gridBagConstraints.ipady = 400;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        processBtn.setText("Process");
        processBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getContentPane().add(processBtn, gridBagConstraints);

        deleteFileBtn.setText("Delete File");
        deleteFileBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteFileBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        getContentPane().add(deleteFileBtn, gridBagConstraints);

        parseFileBtn.setText("Parse File");
        parseFileBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parseFileBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getContentPane().add(parseFileBtn, gridBagConstraints);

        message.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        message.setText("   ");
        message.setMaximumSize(new java.awt.Dimension(9999, 23));
        message.setMinimumSize(new java.awt.Dimension(90, 23));
        message.setPreferredSize(new java.awt.Dimension(90, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(message, gridBagConstraints);

        selectedFile.setText("  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        getContentPane().add(selectedFile, gridBagConstraints);

        jButton1.setText("Open");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        getContentPane().add(jButton1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            Util.logConsole( "previewImportWin formWindowClosing()" );
        } catch (Exception ex) {
            Logger.getLogger(PreviewImportWin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosing

    private void deleteFileBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteFileBtnActionPerformed
        importDialog.deleteCsvFile();
    }//GEN-LAST:event_deleteFileBtnActionPerformed

    private void processBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processBtnActionPerformed
        importDialog.processActionPerformed( evt );
        this.dispose();
    }//GEN-LAST:event_processBtnActionPerformed

    private void parseFileBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parseFileBtnActionPerformed
        //csvData.reset();
        parseFile();
    }//GEN-LAST:event_parseFileBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        desktopEdit( new File( selectedFile.getText() ) );
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteFileBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel message;
    private javax.swing.JButton parseFileBtn;
    private javax.swing.JTable previewImportTbl;
    private javax.swing.JButton processBtn;
    private javax.swing.JLabel selectedFile;
    // End of variables declaration//GEN-END:variables
}
