/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.moneydance.modules.features.mdcsvimporter;

import com.moneydance.apps.md.model.Account;
import com.moneydance.apps.md.model.RootAccount;
import com.moneydance.apps.md.view.gui.MoneydanceGUI;
import com.moneydance.apps.md.view.gui.OnlineManager;
import static com.moneydance.modules.features.mdcsvimporter.TransactionReader.importDialog;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 *
 * @author miki & Stan Towianski
 */
public class ImportDialog
   extends javax.swing.JDialog
{
   private OnlineManager onlineMgr = null;
   private File selectedFile;
   private CSVData csvData;
   private Main main;
   private HashMap runArgsHM;
   protected final static String RUN_ARGS_FILE = "file";
   protected final static String RUN_ARGS_FILEFORMAT = "fileformat";
   protected final static String RUN_ARGS_DATEFORMAT = "dateformat";
   protected final static String RUN_ARGS_IMPORTACCOUNT = "importaccount";
   protected final static String RUN_ARGS_IMPORTTYPE = "importtype";
   protected final static String RUN_ARGS_PROCESSFLAG = "processflag";
   protected final static String RUN_ARGS_DELETECSVFILEFLAG = "deletecsvfileflag";
   protected final static String RUN_ARGS_NOPOPERRORSFLAG = "nopoperrorsflag";
   protected final static String RUN_ARGS_JUNIT = "junitflag";
   
   protected final static int RUN_ARGS_ERRORCODE_INVALID_FILE = 1;
   protected final static int RUN_ARGS_ERRORCODE_INVALID_IMPORTTYPE = 2;
   protected final static int RUN_ARGS_ERRORCODE_INVALID_DATEFORMAT_FOR_FILEFORMAT = 3;
   protected final static int RUN_ARGS_ERRORCODE_INVALID_IMPORTACCOUNT = 4;
   protected final static int RUN_ARGS_ERRORCODE_INVALID_FILEFORMAT_FOR_FILE = 5;
   protected final static int RUN_ARGS_ERRORCODE_INVALID_FILEFORMAT = 6;
   protected final static int RUN_ARGS_ERRORCODE_REQUIRES_FILE = 7;
   protected final static int RUN_ARGS_ERRORCODE_REQUIRES_FILEFORMAT = 8;
   protected final static int RUN_ARGS_ERRORCODE_REQUIRES_IMPORTACCOUNT = 9;
   
   private CustomReaderDialog customReaderDialog = new CustomReaderDialog( this, true );
   private ArrayList<Integer> errCodeList = new ArrayList<Integer>();
   private boolean skipDuringInit = true;
   private boolean autoProcessedAFile = false;
   
   private boolean GET_ALL_READERS = true;
   private boolean GET_COMPATIBLE_READERS = false;
           
   public ImportDialog()
   {
   }
   
   public ImportDialog( Main main, HashMap runArgsHM )
   {
      super( main.getMoneydanceWindow(), true );
      initComponents();
      this.runArgsHM = runArgsHM;
      autoProcessedAFile = false;

      customReaderDialog.init();
      customReaderDialog.setLocationRelativeTo( getRootPane() );

      /**
      textFilename.getDocument().addDocumentListener( new DocumentListener()
      {
         public void insertUpdate( DocumentEvent e )
         {
            textFilenameChanged();
         }

         public void removeUpdate( DocumentEvent e )
         {
            textFilenameChanged();
         }

         public void changedUpdate( DocumentEvent e )
         {
            textFilenameChanged();
         }
      } );
      **/
      
      this.main = main;
      
      if ( main.getMainContext() != null )
        {
        com.moneydance.apps.md.controller.Main mainApp =
               (com.moneydance.apps.md.controller.Main) main.getMainContext();
        onlineMgr = new OnlineManager( (MoneydanceGUI) mainApp.getUI() );
        
        fillAccountCombo( main );
        }

      checkDeleteFile.setSelected( Settings.getBoolean( false, "delete.file" ) );
      onlineImportTypeRB.setSelected( Settings.getBoolean( false, "importtype.online.radiobutton" ) );
              
     skipDuringInit = false;
     this.setModal( false );
     this.addEscapeListener( this );
     TransactionReader.init( customReaderDialog, this, main.getRootAccount() );
    }

    public static void addEscapeListener(final JDialog win) {
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

   protected ArrayList<Integer> processRunArguments()
       {
       errCodeList = new ArrayList<Integer>();
       boolean errorInRunArgs = false;
       
      if ( runArgsHM.containsKey( RUN_ARGS_FILE ) )
        {
        selectedFile = new File( (String) runArgsHM.get( RUN_ARGS_FILE ) );
        if ( ! selectedFile.exists() )
            {
            if ( ! runArgsHM.containsKey( RUN_ARGS_JUNIT ) && ! runArgsHM.containsKey( RUN_ARGS_NOPOPERRORSFLAG ) )
                {
                JOptionPane.showMessageDialog( this, "Cannot proceed with processing of csv file because \nfile \'" 
                                                                        + (String) runArgsHM.get( RUN_ARGS_FILE ) + "\' does not exist.", "Error", JOptionPane.ERROR_MESSAGE );
                }
            errCodeList.add( RUN_ARGS_ERRORCODE_INVALID_FILE );
            errorInRunArgs = true;
            }
        else
            {
            textFilename.setSelectedItem( selectedFile.getPath() );
            fileChanged();
            }
                        
        if ( runArgsHM.containsKey( RUN_ARGS_IMPORTTYPE ) )
            {
            if ( "ONLINE".equalsIgnoreCase( (String) runArgsHM.get( RUN_ARGS_IMPORTTYPE ) ) )
                {
                onlineImportTypeRB.setSelected( true );
                }
            else if ( "REGULAR".equalsIgnoreCase( (String) runArgsHM.get( RUN_ARGS_IMPORTTYPE ) ) )
                {
                regularImportTypeRB.setSelected( true );
                }
            else
                {
                if ( ! runArgsHM.containsKey( RUN_ARGS_JUNIT ) && ! runArgsHM.containsKey( RUN_ARGS_NOPOPERRORSFLAG ) )
                    {
                    JOptionPane.showMessageDialog( this, "Cannot proceed with processing of csv file because \nthe \'" + RUN_ARGS_IMPORTTYPE + "\' you chose \'" 
                                                                            + (String) runArgsHM.get( RUN_ARGS_IMPORTTYPE ) + "\' is not valid.", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                errCodeList.add( RUN_ARGS_ERRORCODE_INVALID_IMPORTTYPE );
                errorInRunArgs = true;
                }
            }
        
        if ( runArgsHM.containsKey( RUN_ARGS_DELETECSVFILEFLAG ) )
            {
            checkDeleteFile.setSelected( true );
            }
        
        if ( runArgsHM.containsKey( RUN_ARGS_FILEFORMAT ) )
            {
            //if ( customReaderDialog.getReaderConfig( (String) runArgsHM.get( RUN_ARGS_FILEFORMAT ) ) )
            TransactionReader reqTransReader = customReaderDialog.getTransactionReader( (String) runArgsHM.get( RUN_ARGS_FILEFORMAT ) );
            if ( reqTransReader != null )
                {
                DefaultComboBoxModel dcbm = (DefaultComboBoxModel) comboFileFormat.getModel();
                int idx = dcbm.getIndexOf( reqTransReader );

                if ( idx >= 0 )
                    {
                    comboFileFormat.setSelectedItem( reqTransReader );
                    processFileFormatChanged( reqTransReader );  // call it myself so I know when it is done.
                    if ( runArgsHM.containsKey( RUN_ARGS_DATEFORMAT ) )
                        {
                        dcbm = (DefaultComboBoxModel) comboDateFormat.getModel();
                        idx = dcbm.getIndexOf( (String) runArgsHM.get( RUN_ARGS_DATEFORMAT ) );

                        if ( idx >= 0 )
                            {
                            comboDateFormat.setSelectedItem( (String) runArgsHM.get( RUN_ARGS_DATEFORMAT ) );
                            }
                        else
                            {
                            if ( ! runArgsHM.containsKey( RUN_ARGS_JUNIT ) && ! runArgsHM.containsKey( RUN_ARGS_NOPOPERRORSFLAG ) )
                                {
                                JOptionPane.showMessageDialog( this, "Cannot proceed with processing of csv file because \nthe \'" + RUN_ARGS_DATEFORMAT + "\' you chose \'" 
                                                                                    + (String) runArgsHM.get( RUN_ARGS_DATEFORMAT ) + "\' is not valid for the \'" + RUN_ARGS_FILEFORMAT + "\' used.", "Error", JOptionPane.ERROR_MESSAGE );
                                }
                            errCodeList.add( RUN_ARGS_ERRORCODE_INVALID_DATEFORMAT_FOR_FILEFORMAT );
                            errorInRunArgs = true;
                            }
                        }

                    if ( runArgsHM.containsKey( RUN_ARGS_IMPORTACCOUNT ) )
                        {
                        dcbm = (DefaultComboBoxModel) comboAccount.getModel();
                        int max = comboAccount.getItemCount();
                        System.err.println( "runArgs at importaccount max =" + max );
                        Account foundAccount = null;

                        for ( idx = max - 1; idx >= 0; idx-- )
                            {
                            System.err.println( "getAcountName() =" + ((Account) dcbm.getElementAt( idx )).getAccountName()
                                            + "=   importaccount =" + (String) runArgsHM.get( RUN_ARGS_IMPORTACCOUNT ) + "=" );
                            if ( ((Account) dcbm.getElementAt( idx )).getAccountName().equalsIgnoreCase( (String) runArgsHM.get( RUN_ARGS_IMPORTACCOUNT ) ) )
                                {
                                foundAccount = (Account) dcbm.getElementAt( idx );
                                break;
                                }        
                            }

                        if ( idx >= 0 )
                            {
                            comboAccount.setSelectedItem( foundAccount );
                            }
                        else
                            {
                            if ( ! runArgsHM.containsKey( RUN_ARGS_JUNIT ) && ! runArgsHM.containsKey( RUN_ARGS_NOPOPERRORSFLAG ) )
                                {
                                JOptionPane.showMessageDialog( this, "Cannot proceed with processing of csv file because \nthe \'" + RUN_ARGS_IMPORTACCOUNT + "\' you chose \'" 
                                                                                        + (String) runArgsHM.get( RUN_ARGS_IMPORTACCOUNT ) + "\' is not valid.", "Error", JOptionPane.ERROR_MESSAGE );
                                }
                            errCodeList.add( RUN_ARGS_ERRORCODE_INVALID_IMPORTACCOUNT );
                            errorInRunArgs = true;
                            }
                        }
                    }
                else
                    {
                    if ( ! runArgsHM.containsKey( RUN_ARGS_JUNIT ) && ! runArgsHM.containsKey( RUN_ARGS_NOPOPERRORSFLAG ) )
                        {
                        JOptionPane.showMessageDialog( this, "Cannot proceed with processing of csv file because \nthe \'" + RUN_ARGS_FILEFORMAT + "\' you chose \'" 
                                                                                + (String) runArgsHM.get( RUN_ARGS_FILEFORMAT ) + "\' is not valid for the file you gave.", "Error", JOptionPane.ERROR_MESSAGE );
                        }
                    errCodeList.add( RUN_ARGS_ERRORCODE_INVALID_FILEFORMAT_FOR_FILE );
                    errorInRunArgs = true;
                    }
                }
            else
                {
                if ( ! runArgsHM.containsKey( RUN_ARGS_JUNIT ) && ! runArgsHM.containsKey( RUN_ARGS_NOPOPERRORSFLAG ) )
                    {
                    JOptionPane.showMessageDialog( this, "Cannot proceed with processing of csv file because \nof invalid \'" + RUN_ARGS_FILEFORMAT + "\' value \'" 
                                                                            + (String) runArgsHM.get( RUN_ARGS_FILEFORMAT ) + "\'.", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                errCodeList.add( RUN_ARGS_ERRORCODE_INVALID_FILEFORMAT );
                errorInRunArgs = true;
                }
            }  // endif fileformat
        
        
        if ( runArgsHM.containsKey( RUN_ARGS_PROCESSFLAG ) )
            {
            if ( ! runArgsHM.containsKey( RUN_ARGS_FILEFORMAT ) )
                {
                if ( ! runArgsHM.containsKey( RUN_ARGS_JUNIT ) && ! runArgsHM.containsKey( RUN_ARGS_NOPOPERRORSFLAG ) )
                    {
                    JOptionPane.showMessageDialog( this, "Cannot proceed without a \'" + RUN_ARGS_FILEFORMAT + "\' argument "
                                                                            + "if you use the \'" + RUN_ARGS_PROCESSFLAG + "\' argument.", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                errCodeList.add( RUN_ARGS_ERRORCODE_REQUIRES_FILEFORMAT );
                errorInRunArgs = true;
                }
            else if ( ! runArgsHM.containsKey( RUN_ARGS_IMPORTACCOUNT ) )
                {
                if ( ! runArgsHM.containsKey( RUN_ARGS_JUNIT ) && ! runArgsHM.containsKey( RUN_ARGS_NOPOPERRORSFLAG ) )
                    {
                    JOptionPane.showMessageDialog( this, "Cannot proceed without a \'" + RUN_ARGS_IMPORTACCOUNT + "\' argument "
                                                                            + "if you use the \'" + RUN_ARGS_PROCESSFLAG + "\' argument.", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                errCodeList.add( RUN_ARGS_ERRORCODE_REQUIRES_IMPORTACCOUNT );
                errorInRunArgs = true;
                }
            }
        
       if ( runArgsHM.containsKey( RUN_ARGS_PROCESSFLAG ) && ! errorInRunArgs )
            {
            btnProcessActionPerformed( null );
            autoProcessedAFile = true;
            }
        
        }  // END of arguments processing
      else if ( runArgsHM.size() > 0 )
            {
            if ( ! runArgsHM.containsKey( RUN_ARGS_JUNIT ) && ! runArgsHM.containsKey( RUN_ARGS_NOPOPERRORSFLAG ) )
                {
                JOptionPane.showMessageDialog( this, "Cannot proceed without a \'" + RUN_ARGS_FILE + "\' argument "
                                                                           , "Error", JOptionPane.ERROR_MESSAGE );
                }
            errCodeList.add( RUN_ARGS_ERRORCODE_REQUIRES_FILE );
            errorInRunArgs = true;
            }
       return errCodeList;
       }

   private void fillAccountCombo( Main main )
   {
      RootAccount rootAccount = main.getRootAccount();
      comboAccount.removeAllItems();

      fillAccountCombo_( rootAccount );

      if ( comboAccount.getItemCount() > 0 )
      {
         System.err.println( "Settings.getInteger( false, \"selected.account\", 0 ) =" + Settings.getInteger( false, "selected.account", 0 ) );
         try {
            comboAccount.setSelectedIndex( Settings.getInteger( false, "selected.account", 0 ) );
            }
         catch( Exception ex )
            {
            JOptionPane.showMessageDialog( rootPane, "Your 'Import to Account' is not longer valid. "
                + "You will have to choose a new one.",
                "Import to Account setting",
                JOptionPane.ERROR_MESSAGE );
            }
      }
   }

   private void fillAccountCombo_( Account parentAccount )
   {
      for ( int i = 0; i < parentAccount.getSubAccountCount(); ++i )
      {
         Account account = parentAccount.getSubAccount( i );
         if ( account.isRegisterAccount() )
         {
            comboAccount.addItem( account );
         }
         else
         {
            fillAccountCombo_( account );
         }
      }
   }

    public boolean isSkipDuringInit() {
        return skipDuringInit;
    }

    public void setSkipDuringInit(boolean skipDuringInit) {
        this.skipDuringInit = skipDuringInit;
    }

    public boolean isSelectedOnlineImportTypeRB() {
        System.err.println( "onlineImportTypeRB.isSelected() =" + onlineImportTypeRB.isSelected() + "=" );
        return onlineImportTypeRB.isSelected();
    }

    public boolean isAutoProcessedAFile() {
        return autoProcessedAFile;
    }
   
    public void setPropertiesFile() {
        this.propertiesFile.setText( Settings.getFilename().toString() );
    }
    
    private void processFileFormatChanged( TransactionReader transReader )
    {                                       
      System.err.println( "processFileFormatChanged()  --------------- " );

          if ( transReader != null )
            {
            if ( transReader.isCustomReaderFlag() )
                {
                System.err.println( "Have a custom reader. Read config for =" + transReader.toString() + "=" );
                customReaderDialog.getReaderConfig( transReader.toString() );
                
//                System.err.println( "importDialog() isSelectedOnlineImportTypeRB()) =" + isSelectedOnlineImportTypeRB()+ "=" );
//                System.err.println( "importDialog() reader.isUsingCategorynameFlag() =" + transReader.isUsingCategorynameFlag() + "=" );
//                if ( importDialog.isSelectedOnlineImportTypeRB() && transReader.isUsingCategorynameFlag() )
//                    {
//                    JOptionPane.showMessageDialog( this, "Categories will not import using \'Online\' import type. Set to \'Regular\'" 
//                                                    , "Message", JOptionPane.INFORMATION_MESSAGE );
//                    }
                }

             String[] formats = transReader.getSupportedDateFormats();
             System.err.println( "importDialog().processFileFormatChanged()  formats =" + formats + "=" );

             popComboDateFormatList( formats );

             if ( formats.length == 0 )
                {
                comboDateFormat.addItem( "Date format not recognized" );
                comboDateFormat.setEnabled( false );
                }
             else if ( formats.length == 1 )
                {
                comboDateFormat.setSelectedIndex( 0 );
                comboDateFormat.setEnabled( false );
                }
             else
                {
                comboDateFormat.setEnabled( true );
                System.err.println( "importDialog() customReaderDialog set Date Format Selected  =" + customReaderDialog.getDateFormatSelected() + "=" );
                comboDateFormat.setSelectedItem( customReaderDialog.getDateFormatSelected() );
                }
          }
    }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel3 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        lblSelectFile = new javax.swing.JLabel();
        btnBrowse = new javax.swing.JButton();
        checkDeleteFile = new javax.swing.JCheckBox();
        btnClose = new javax.swing.JButton();
        btnProcess = new javax.swing.JButton();
        lblAccount = new javax.swing.JLabel();
        comboAccount = new javax.swing.JComboBox();
        lblMessage = new javax.swing.JLabel();
        lblFileFormat = new javax.swing.JLabel();
        comboFileFormat = new javax.swing.JComboBox();
        onlineImportTypeRB = new javax.swing.JRadioButton();
        regularImportTypeRB = new javax.swing.JRadioButton();
        lblDateFormat = new javax.swing.JLabel();
        comboDateFormat = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        comboFileFormatLabel = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        propertiesFile = new javax.swing.JLabel();
        PreviewImportBtn = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        textFilename = new javax.swing.JComboBox();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        jLabel3.setText("jLabel3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Import File: " + main.VERSION_STRING);
        setName("importDialog"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        lblSelectFile.setText("Select Import File:");
        lblSelectFile.setPreferredSize(new java.awt.Dimension(120, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        getContentPane().add(lblSelectFile, gridBagConstraints);

        btnBrowse.setText("...");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(btnBrowse, gridBagConstraints);

        checkDeleteFile.setText("Securely erase file after processing.");
        checkDeleteFile.setToolTipText("If checked, the specified file will be securely erased (first overwritten, then deleted) after successful processing.");
        checkDeleteFile.setPreferredSize(new java.awt.Dimension(250, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 0);
        getContentPane().add(checkDeleteFile, gridBagConstraints);

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        getContentPane().add(btnClose, gridBagConstraints);

        btnProcess.setText("Process");
        btnProcess.setEnabled(false);
        btnProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        getContentPane().add(btnProcess, gridBagConstraints);

        lblAccount.setText("Import to Account:");
        lblAccount.setPreferredSize(new java.awt.Dimension(120, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        getContentPane().add(lblAccount, gridBagConstraints);

        comboAccount.setModel(new javax.swing.DefaultComboBoxModel(new Account[] {  }));
        comboAccount.setMaximumSize(new java.awt.Dimension(180, 29));
        comboAccount.setMinimumSize(new java.awt.Dimension(180, 29));
        comboAccount.setPreferredSize(new java.awt.Dimension(180, 29));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 10, 0);
        getContentPane().add(comboAccount, gridBagConstraints);

        lblMessage.setForeground(new java.awt.Color(255, 0, 51));
        lblMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMessage.setText(" ");
        lblMessage.setMaximumSize(new java.awt.Dimension(200, 25));
        lblMessage.setMinimumSize(new java.awt.Dimension(100, 25));
        lblMessage.setOpaque(true);
        lblMessage.setPreferredSize(new java.awt.Dimension(3, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(lblMessage, gridBagConstraints);

        lblFileFormat.setText("File Reader:");
        lblFileFormat.setPreferredSize(new java.awt.Dimension(120, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        getContentPane().add(lblFileFormat, gridBagConstraints);

        comboFileFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[] {  }));
        comboFileFormat.setMaximumSize(new java.awt.Dimension(180, 29));
        comboFileFormat.setMinimumSize(new java.awt.Dimension(180, 29));
        comboFileFormat.setPreferredSize(new java.awt.Dimension(180, 29));
        comboFileFormat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fileFormatChanged(evt);
            }
        });
        comboFileFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboFileFormatActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 10, 0);
        getContentPane().add(comboFileFormat, gridBagConstraints);

        buttonGroup1.add(onlineImportTypeRB);
        onlineImportTypeRB.setText("Online");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        getContentPane().add(onlineImportTypeRB, gridBagConstraints);

        buttonGroup1.add(regularImportTypeRB);
        regularImportTypeRB.setSelected(true);
        regularImportTypeRB.setText("Regular");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        getContentPane().add(regularImportTypeRB, gridBagConstraints);

        lblDateFormat.setText("Date Format:");
        lblDateFormat.setPreferredSize(new java.awt.Dimension(120, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        getContentPane().add(lblDateFormat, gridBagConstraints);

        comboDateFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[] {  }));
        comboDateFormat.setMaximumSize(new java.awt.Dimension(180, 29));
        comboDateFormat.setMinimumSize(new java.awt.Dimension(180, 29));
        comboDateFormat.setPreferredSize(new java.awt.Dimension(180, 29));
        comboDateFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboDateFormatActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 10, 0);
        getContentPane().add(comboDateFormat, gridBagConstraints);

        jButton1.setText("Maintain Custom File Readers");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(jButton1, gridBagConstraints);

        jLabel5.setText("Import Transactions as:");
        jLabel5.setToolTipText("<html>Online: These will not have a default category pre-set.<br/>\nRegular: These are regular transactions and they get the default category for the account.<br/>\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;You can also import a 'tag' field in the regular type.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getContentPane().add(jLabel5, gridBagConstraints);

        comboFileFormatLabel.setText(" ");
        comboFileFormatLabel.setMaximumSize(new java.awt.Dimension(60, 25));
        comboFileFormatLabel.setMinimumSize(new java.awt.Dimension(40, 25));
        comboFileFormatLabel.setPreferredSize(new java.awt.Dimension(60, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(comboFileFormatLabel, gridBagConstraints);

        jButton2.setText("Suggestions");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        getContentPane().add(jButton2, gridBagConstraints);

        jLabel1.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 14;
        getContentPane().add(jLabel1, gridBagConstraints);

        propertiesFile.setText(" ");
        propertiesFile.setMaximumSize(new java.awt.Dimension(180, 23));
        propertiesFile.setMinimumSize(new java.awt.Dimension(180, 23));
        propertiesFile.setPreferredSize(new java.awt.Dimension(180, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 10, 10);
        getContentPane().add(propertiesFile, gridBagConstraints);

        PreviewImportBtn.setText("Preview Import");
        PreviewImportBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreviewImportBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        getContentPane().add(PreviewImportBtn, gridBagConstraints);

        jButton3.setText("Find Reader(s) that Work on Import File");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        getContentPane().add(jButton3, gridBagConstraints);

        textFilename.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));
        textFilename.setMinimumSize(new java.awt.Dimension(180, 29));
        textFilename.setPreferredSize(new java.awt.Dimension(180, 29));
        textFilename.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                textFilenameItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 10, 0);
        getContentPane().add(textFilename, gridBagConstraints);

        jButton4.setText("Find Import File(s) for this Reader");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        getContentPane().add(jButton4, gridBagConstraints);

        jButton5.setText("List All Readers");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        getContentPane().add(jButton5, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnBrowseActionPerformed
    {//GEN-HEADEREND:event_btnBrowseActionPerformed
       JFileChooser dialog = new JFileChooser();
       dialog.setFileHidingEnabled( true );
       dialog.setDialogTitle( "Select text file" );
       dialog.setCurrentDirectory(
          new File( Settings.get( false, "last.directory",
          dialog.getCurrentDirectory().getAbsolutePath() ) ) );
       dialog.addChoosableFileFilter( new FileFilter()
       {
          @Override
          public boolean accept( File f )
          {
             return f.isDirectory() || f.getName().toUpperCase().endsWith( ".CSV" );
          }

          @Override
          public String getDescription()
          {
             return "Formatted Text File (*.csv)";
          }
       } );
    if ( dialog.showDialog( this, "Select" ) == JFileChooser.APPROVE_OPTION )
        {
        selectedFile = dialog.getSelectedFile();
        Settings.set( "last.directory", dialog.getCurrentDirectory().getAbsolutePath() );
        // textFilename.setSelectedItem( selectedFile.getPath() );
        String[] tt = { selectedFile.getPath() };
        popTextFilenameList( tt );
//          fileChanged();
        fileChanged2();
        btnProcess.setEnabled( false );
        }
}//GEN-LAST:event_btnBrowseActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnCloseActionPerformed
    {//GEN-HEADEREND:event_btnCloseActionPerformed
       this.setVisible( false );
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnProcessActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnProcessActionPerformed
    {//GEN-HEADEREND:event_btnProcessActionPerformed
       System.err.println( "Process button entered" );
       Settings.setYesNo( "delete.file", checkDeleteFile.isSelected() );
       Settings.setYesNo( "importtype.online.radiobutton", onlineImportTypeRB.isSelected() );
       Settings.setInteger( "selected.account", comboAccount.getSelectedIndex() );

        try
             {
             TransactionReader transReader = (TransactionReader) comboFileFormat.getSelectedItem();
             System.err.println( "comboFileFormat is string =" + transReader.toString() + "=" );
             transReader.setDateFormat( (String) comboDateFormat.getSelectedItem() );
             CSVReader csvReader = null;
    
            if ( transReader.getCustomReaderData().getUseRegexFlag() )
                {
                System.err.println( "\n================  Regex Reader" );
                csvReader = new RegexReader( new InputStreamReader( new FileInputStream( selectedFile ), Charset.forName( (String) transReader.getCustomReaderData().getFileEncoding() )), transReader.getCustomReaderData() );
                }
            else
                {
                System.err.println( "\n================  Csv Reader" );
                csvReader = new CSVReader( new InputStreamReader( new FileInputStream( selectedFile ), Charset.forName( (String) transReader.getCustomReaderData().getFileEncoding() )), transReader.getCustomReaderData() );
                }
            CSVData csvData = new CSVData( csvReader );            
       
       //System.err.println( "btnProcessActionPerformed  customReaderDialog.getFieldSeparatorChar() =" + (char)customReaderDialog.getFieldSeparatorChar() + "=" );
       //csvData.getReader().setFieldSeparator( customReaderDialog.getFieldSeparatorChar() );

          Account account = (Account) comboAccount.getSelectedItem();
          System.err.println( "starting transReader.parse..." );
          transReader.parse( main, csvData, account, main.getRootAccount() );
          csvReader.close();
          System.out.println( "finished transReader.parse" );

          //TESTING! DS
//         onlineMgr.processDownloadedTxns( account );
            }
       catch ( IOException x )
            {
          JOptionPane.showMessageDialog( rootPane, "There was a problem importing "
             + " selected file, probably because the file format was wrong. Some items "
             + "might have been added to your account.",
             "Error Importing File",
             JOptionPane.ERROR_MESSAGE );
          return;
            }

       if ( checkDeleteFile.isSelected() )
        {
          try
          {
             SecureFileDeleter.delete( selectedFile );
          }
          catch ( IOException x )
          {
             JOptionPane.showMessageDialog( rootPane, "The file was imported properly, "
                + "however it could not be erased as requested.", "Cannot Delete File",
                JOptionPane.ERROR_MESSAGE );
             return;
          }
        }

       if ( ! Settings.getBoolean( false, "success.dialog.shown", false ) )
        {
          Settings.setYesNo( "success.dialog.shown", true );
          JOptionPane.showMessageDialog( rootPane,
             "The file was imported properly. \n\n"
             + "You can view the imported items when you open the account you have \n"
             + "selected and click on the 'downloaded transactions' message at the \n"
             + "bottom of the screen.",
             "Import Successful", JOptionPane.INFORMATION_MESSAGE );
        }

       setVisible( false );
    }//GEN-LAST:event_btnProcessActionPerformed

    private void fileFormatChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_fileFormatChanged
    {//GEN-HEADEREND:event_fileFormatChanged
      System.err.println( "fileFormatChanged()  event  --------------- " + evt );
      if ( skipDuringInit )
            {
            System.err.println( "fileFormatChanged()  skipDuringInit  ---------------" );
            return;
            }
      
      if ( evt.getStateChange() == ItemEvent.SELECTED )
       {
       System.err.println( "fileFormatChanged()  event == ItemEvent.SELECTED  ---------------" );
        if ( comboFileFormat.getSelectedItem() instanceof String )
                {
                System.err.println( "comboFileFormat is string =" + (String) comboFileFormat.getSelectedItem() + "=" );
                return;
                }

        TransactionReader transReader;
          try
               {
               transReader = (TransactionReader) evt.getItem();
               }
          catch ( ClassCastException x )
                {
                transReader = null;
                }
          
          processFileFormatChanged( transReader );
       }
        
    }//GEN-LAST:event_fileFormatChanged

    private void comboFileFormat1fileFormatChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboFileFormat1fileFormatChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_comboFileFormat1fileFormatChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        setPropertiesFile();
        customReaderDialog.setVisible( true );
    }//GEN-LAST:event_jButton1ActionPerformed

private void comboDateFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboDateFormatActionPerformed
    //customReaderDialog.setDateFormat( (String)comboDateFormat.getSelectedItem() );
}//GEN-LAST:event_comboDateFormatActionPerformed

private void comboFileFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboFileFormatActionPerformed

      btnProcess.setEnabled( false );
    /*  use actionPerformed - not both !
if ( comboFileFormat.getSelectedItem() instanceof String )
        {
        System.err.println( "comboFileFormat is string =" + (String) comboFileFormat.getSelectedItem() + "=" );
        return;
        }
    
    TransactionReader transReader = (TransactionReader) comboFileFormat.getSelectedItem();
    if ( transReader != null )
          {
            if ( transReader.isCustomReaderFlag() )
                {
                System.err.println( "Have a custom reader. Read config for =" + transReader.toString() + "=" );
                customReaderDialog.getReaderConfig( transReader.toString() );
                }

             String[] formats = transReader.getSupportedDateFormats();

             comboDateFormat.removeAllItems();
             for ( String s : formats )
                {
                comboDateFormat.addItem( s );
                }

             if ( formats.length == 0 )
                {
                comboDateFormat.addItem( "Date format not recognized" );
                comboDateFormat.setEnabled( false );
                }
             else if ( formats.length == 1 )
                {
                comboDateFormat.setSelectedIndex( 0 );
                comboDateFormat.setEnabled( false );
                }
             else
                {
                comboDateFormat.setEnabled( true );
                System.err.println( "importDialog() customReaderDialog set Date Format Selected  =" + customReaderDialog.getDateFormatSelected() + "=" );
                comboDateFormat.setSelectedItem( customReaderDialog.getDateFormatSelected() );
                }
          }
*/
}//GEN-LAST:event_comboFileFormatActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        JOptionPane.showMessageDialog( this, "<html><font face=\\\"sansserif\\\" color=\\\"green\\\">Create a temporary bank account to import into.<br/>When you are ok with the imported records, then \"Batch Change\"<br/>them to the right account.<br/>Not using \"Batch Change\" will mess up your accounts.<br/><br/>-Payment- and -Deposit- are just opposite signed amounts of each other<br/>so if your amount comes into the wrong column, just flip them.<br/></font></html>" 
                                , "Message", JOptionPane.INFORMATION_MESSAGE );
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setPropertiesFile(); 
    }//GEN-LAST:event_formWindowOpened

    private void PreviewImportBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreviewImportBtnActionPerformed
       System.err.println( "Preview Import button entered" );

        try
            {
            TransactionReader transReader = (TransactionReader) comboFileFormat.getSelectedItem();
            System.err.println( "comboFileFormat is string =" + transReader.toString() + "=" );
            transReader.setDateFormat( (String) comboDateFormat.getSelectedItem() );
            CSVReader csvReader = null;
    
            if ( transReader.getCustomReaderData().getUseRegexFlag() )
                {
                System.err.println( "\n================  Regex Reader" );
                csvReader = new RegexReader( new InputStreamReader( new FileInputStream( selectedFile ), Charset.forName( (String) transReader.getCustomReaderData().getFileEncoding() )), transReader.getCustomReaderData() );
                }
            else
                {
                System.err.println( "\n================  Csv Reader" );
                csvReader = new CSVReader( new InputStreamReader( new FileInputStream( selectedFile ), Charset.forName( (String) transReader.getCustomReaderData().getFileEncoding() )), transReader.getCustomReaderData() );
                }

            CSVData csvData = new CSVData( csvReader );            
       
            //System.err.println( "btnProcessActionPerformed  customReaderDialog.getFieldSeparatorChar() =" + (char)customReaderDialog.getFieldSeparatorChar() + "=" );
            //csvData.getReader().setFieldSeparator( customReaderDialog.getFieldSeparatorChar() );

            Account account = (Account) comboAccount.getSelectedItem();
            //System.err.println( "starting transReader.parse..." );
            //transReader.parse( main, csvData, account, main.getRootAccount() );
                      
            transReader.setRootAccount( main.getRootAccount() );
                
            PreviewImportWin previewImportWin = new PreviewImportWin();
            previewImportWin.myInit( this, transReader, csvData, csvReader );
            }
       catch ( IOException x )
            {
            JOptionPane.showMessageDialog( rootPane, "There was a problem with Preview importing "
                + " selected file, probably because the file format was wrong. ",
                "Error Importing File",
                JOptionPane.ERROR_MESSAGE );
            return;
            }
    }//GEN-LAST:event_PreviewImportBtnActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        fileChanged();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        popTextFilenameList( null );
        btnProcess.setEnabled( false );
    }//GEN-LAST:event_jButton4ActionPerformed

    private void textFilenameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_textFilenameItemStateChanged
      System.err.println( "textFilenameItemStateChanged()  event  --------------- " + evt );
      if ( skipDuringInit )
            {
            System.err.println( "textFilenameItemStateChanged()  skipDuringInit  ---------------" );
            return;
            }
      
      if ( evt.getStateChange() == ItemEvent.SELECTED )
       {
       System.err.println( "textFilenameItemStateChanged()  event == ItemEvent.SELECTED  ---------------" );
        if ( textFilename.getSelectedItem() instanceof String )
                {
                System.err.println( "textFilename is string =" + (String) textFilename.getSelectedItem() + "=" );
                textFilenameChanged();
                return;
                }
       }
    }//GEN-LAST:event_textFilenameItemStateChanged

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        fileChanged2();
    }//GEN-LAST:event_jButton5ActionPerformed

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                ImportDialog dialog = new ImportDialog();
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton PreviewImportBtn;
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnClose;
    protected javax.swing.JButton btnProcess;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox checkDeleteFile;
    private javax.swing.JComboBox comboAccount;
    private javax.swing.JComboBox comboDateFormat;
    private javax.swing.JComboBox comboFileFormat;
    private javax.swing.JLabel comboFileFormatLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel lblAccount;
    private javax.swing.JLabel lblDateFormat;
    private javax.swing.JLabel lblFileFormat;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblSelectFile;
    private javax.swing.JRadioButton onlineImportTypeRB;
    private javax.swing.JLabel propertiesFile;
    private javax.swing.JRadioButton regularImportTypeRB;
    private javax.swing.JComboBox textFilename;
    // End of variables declaration//GEN-END:variables

    public void comboFileFormat1AddItem( TransactionReader customReader )
        {
        System.err.println( "importDialog() add reader item =" + customReader.toString() + "=" );
        //customReaderCB.addItem( xxx );
        comboFileFormat.addItem( customReader );
        }
    
    public void comboFileFormat1SetItem( TransactionReader customReader )
        {
        System.err.println( "importDialog() comboFileFormat1SetItem() =" + customReader.toString() + "=" );
        //customReaderCB.setSelectedItem( xxx );
        comboFileFormat.setSelectedItem( customReader );
        }
    
    public void comboDateFormatSetItem( String xxx )
        {
        System.err.println( "importDialog() comboDateFormat.setSelectedItem( xxx ) =" + xxx + "=" );
        comboDateFormat.setSelectedItem( xxx );
        }

    public void createSupportedDateFormats( String dateFormatArg ) 
        {
        DefaultComboBoxModel dateFormatModel = new DefaultComboBoxModel();
        System.out.println( "ImportDialog.createSupportedDateFormats() dateFormatArg =" + dateFormatArg + "=" );
        dateFormatModel.addElement( dateFormatArg );
        
        comboDateFormat.setModel( dateFormatModel );

        comboDateFormat.setSelectedIndex( 0 );
        }
    
    public String comboDateFormatGetItem()
        {
        System.err.println( "importDialog() comboDateFormat.comboDateFormat.getSelectedItem() =" + comboDateFormat.getSelectedItem() + "=" );
        return (String) comboDateFormat.getSelectedItem();
        }

    public void comboDateFormatAddItem( String format )
        {
        System.err.println( "importDialog() add date format item =" + comboDateFormat + "=" );
        //customReaderCB.addItem( xxx );
        comboDateFormat.addItem( format );
        }

    public void comboDateFormatSetModel( DefaultComboBoxModel model )
        {
        System.err.println( "importDialog() comboDateFormatSetModel()" );
        comboDateFormat.setModel( model );
        }

    private void textFilenameChanged()
   {
      File newFile = new File( (String) textFilename.getSelectedItem() );

      if ( ! newFile.equals( selectedFile ) )
      {
         selectedFile = newFile;
//         fileChanged();
      }
   }

    public void popTextFilenameList( String [] filenames )
        {
        System.err.println( "entered popTextFilenameList()" );
        File dir = new File( Settings.get( false, "last.directory", "" ) );
        
        if ( filenames == null )
            {
            if ( dir.equals( "" ) )
                {
                dir = (File.listRoots())[0];
                }

            TransactionReader transReader = (TransactionReader) comboFileFormat.getSelectedItem();
            if ( transReader.getCustomReaderData().getFilenameMatcher() == null ||
                 transReader.getCustomReaderData().getFilenameMatcher().equals( "" ) )
                {
                transReader.getCustomReaderData().setFilenameMatcher( ".*\\.[Cc][Ss][Vv]" );
                }

            // create new filename filter
             FilenameFilter fileNameFilter = new FilenameFilter() 
                {
                TransactionReader transReader = (TransactionReader) comboFileFormat.getSelectedItem();
                    {
                    System.err.println( "popTextFilenameList() transReader.getFormatName() >" + transReader.getFormatName() + "<" );
                    }
                @Override
                public boolean accept(File dir, String name) {
                   System.err.println( "popTextFilenameList() match name? >" + name + "<" );
                   //System.err.println( "popTextFilenameList() getFilenameMatcher() >" + transReader.getCustomReaderData().getFilenameMatcher() + "<" );
                   if ( name.matches( transReader.getCustomReaderData().getFilenameMatcher() ) )
                      {
                      return true;
                      }
                   return false;
                }
             };
             
            // returns pathnames for files and directory
            filenames = dir.list( fileNameFilter );

            textFilename.removeAllItems();
            for ( String s : filenames )
                {
                System.err.println(  "popTextFilenameList add format >" + s + "<" );
                textFilename.addItem( dir + System.getProperty( "file.separator" ) + s );
                }
            }
        else
            {
            textFilename.removeAllItems();
            for ( String s : filenames )
                {
                System.err.println(  "popTextFilenameList add format >" + s + "<" );
                textFilename.addItem( s );
                }
            }
        }
    
    public void popComboDateFormatList( String [] formats )
        {
         System.err.println(  "entered popComboDateFormatList()" );
         comboDateFormat.removeAllItems();
         for ( String s : formats )
            {
            System.err.println(  "popComboDateFormatList add format >" + s + "<" );
            comboDateFormat.addItem( s );
            }
        }
    
   protected void fileChanged()
   {
      String message = null;
      boolean error = false;
      boolean isUsingCategorynameFlag = false;
              
      // see if the file is selected
      if ( selectedFile == null || !selectedFile.exists() || !selectedFile.isFile() )
      {
         message = "Please select a valid file.";
         error = true;
      }

      // try reading the file
      /*
      if ( !error )
      {
         try
         {
            CSVReader csvReader = new CSVReader( new FileReader( selectedFile ) );
            //csvReader.setFieldSeparator( '8' );  THIS WORKED !
            csvReader.setFieldSeparator( customReaderDialog.getFieldSeparatorChar() );
            csvData = new CSVData( csvReader );
         }
         catch ( Throwable x )
         {
            error = true;
            message = "Error reading file.";
            Logger.getLogger( ImportDialog.class.getName() ).log( Level.SEVERE, null, x );
         }
      }
       * */
      
      // detect file format
      if ( ! error )
      {
// moving        TransactionReader.customReaderDialog = customReaderDialog;
        
         setLabel( "FindAReader", "Find Reader" );
         TransactionReader[] fileFormats = TransactionReader.getCompatibleReaders( GET_COMPATIBLE_READERS, selectedFile, this, main.getRootAccount() );

         comboFileFormat.removeAllItems();
         for ( TransactionReader reader : fileFormats )
            {
            comboFileFormat.addItem( reader );
            if ( reader.isUsingCategorynameFlag() )
                isUsingCategorynameFlag = true;
            }

         if ( fileFormats.length == 0 )
         {
            setLabel( "FindAReader", "No Matches" );
            comboFileFormat.addItem( "Format not recognized" );
            comboFileFormat.setEnabled( false );
            comboDateFormat.setEnabled( false );
            error = true;
            message = "Unsupported CSV file format.";
         }
         else if ( fileFormats.length == 1 )
         {
            setLabel( "FindAReader", "Found" );
            comboFileFormat.setSelectedIndex( 0 );
            comboFileFormat.setEnabled( false );
         }
         else
         {
            setLabel( "FindAReader", "Pick One" );
            comboFileFormat.setEnabled( true );
         }
      }
      else
      {
         setLabel( "FindAReader", "No File" );
         comboFileFormat.removeAllItems();
         comboFileFormat.addItem( "Format not recognized" );
         comboFileFormat.setEnabled( false );
      }

      if ( ! error )
      {
         TransactionReader reader = (TransactionReader) comboFileFormat.getSelectedItem();
         String[] formats = reader.getSupportedDateFormats();
         System.err.println( "importDialog().fileChanged()  formats =" + formats + "=" );

         popComboDateFormatList( formats );

         if ( formats.length == 0 )
         {
            comboDateFormat.addItem( "Date format not recognized" );
            comboDateFormat.setEnabled( false );
            error = true;
            message = "Cannot recognize date format used in the file.";
         }
         else if ( formats.length == 1 )
         {
            comboDateFormat.setSelectedIndex( 0 );
            comboDateFormat.setEnabled( false );
         }
         else
         {
            comboDateFormat.setEnabled( true );
            System.err.println( "importDialog() customReaderDialog.getDateFormatSelected()) =" + customReaderDialog.getDateFormatSelected() + "=" );
            comboDateFormat.setSelectedItem( customReaderDialog.getDateFormatSelected() );
         }
         
        System.err.println( "importDialog() error =" + error + "=" );
        System.err.println( "importDialog() isSelectedOnlineImportTypeRB()) =" + isSelectedOnlineImportTypeRB()+ "=" );
        System.err.println( "importDialog() reader.isUsingCategorynameFlag() =" + reader.isUsingCategorynameFlag() + "=" );
        if ( ! error && importDialog.isSelectedOnlineImportTypeRB() && isUsingCategorynameFlag )
            {
            JOptionPane.showMessageDialog( this, "Categories will not import using \'Online\' import type. Set to \'Regular\' if you want that." 
                                            , "Message", JOptionPane.INFORMATION_MESSAGE );
            }
      }
      else
      {
         comboDateFormat.removeAllItems();
         comboDateFormat.addItem( "Date format not recognized" );
         comboDateFormat.setEnabled( false );
      }

      btnProcess.setEnabled( !error );
      if ( error )
      {
         csvData = null;
      }
      setLabel( "main", message );
   }
   
   protected void fileChanged2()
   {
      String message = null;
      boolean error = false;
      boolean isUsingCategorynameFlag = false;
              
      // see if the file is selected
      if ( selectedFile == null || !selectedFile.exists() || !selectedFile.isFile() )
      {
         message = "Please select a valid file.";
         error = true;
      }

      // detect file format
      if ( ! error )
      {
// moving        TransactionReader.customReaderDialog = customReaderDialog;
        
         setLabel( "FindAReader", "Find Reader" );
         TransactionReader[] fileFormats = TransactionReader.getCompatibleReaders( GET_ALL_READERS, selectedFile, this, main.getRootAccount() );

         comboFileFormat.removeAllItems();
         for ( TransactionReader reader : fileFormats )
            {
            comboFileFormat.addItem( reader );
            if ( reader.isUsingCategorynameFlag() )
                isUsingCategorynameFlag = true;
            }

         if ( fileFormats.length == 0 )
         {
            comboFileFormat.addItem( "Format not recognized" );
            comboFileFormat.setEnabled( false );
            comboDateFormat.setEnabled( false );
            error = true;
            message = "Unsupported CSV file format.";
         }
         else if ( fileFormats.length == 1 )
         {
            comboFileFormat.setSelectedIndex( 0 );
            comboFileFormat.setEnabled( false );
         }
         else
         {
            setLabel( "FindAReader", "Pick One" );
            comboFileFormat.setEnabled( true );
         }
      }
      else
      {
         setLabel( "FindAReader", "No File" );
         comboFileFormat.removeAllItems();
         comboFileFormat.addItem( "Format not recognized" );
         comboFileFormat.setEnabled( false );
      }

      btnProcess.setEnabled( !error );
      if ( error )
      {
         csvData = null;
      }
      setLabel( "main", message );
   }
   
   public void setLabel( String objName, String message )
   {
    JLabel label = null;
       
    if ( objName.equals( "main" ) )
        {
        label = lblMessage;
        }
    else if ( objName.equals( "FindAReader" ) )
        {
        label = comboFileFormatLabel;
        }
    if ( message != null )
      {
         label.setVisible( true );
         label.setText( message );
         label.setForeground( new Color( 255, 0, 51 ) );
      }
    else
      {
         label.setVisible( false );
      }
   }
}
