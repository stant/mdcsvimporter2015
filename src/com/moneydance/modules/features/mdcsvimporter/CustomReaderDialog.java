
/*
 * CustomerReaderDialog.java
 *
 * Created on Aug 3, 2011, 11:49:09 PM
 */

package com.moneydance.modules.features.mdcsvimporter;

import com.moneydance.modules.features.mdcsvimporter.formats.CustomReader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

/**
 *
 * @author Stan Towianski
 */


public class CustomReaderDialog extends javax.swing.JDialog {
        
        //javax.swing.JDialog parent = null;
        ImportDialog parent = null;
        
        //String [] dataTypes = { "", "ignore", "-Payment-", "-Deposit-", "date", "dateAvailable", "dateInitiated", "datePosted", "datePurchased", "check number", "description", "memo", "account name" };
        
        String [] dataTypes = { 
                                        CustomReader.DATA_TYPE_BLANK 
                                        , CustomReader.DATA_TYPE_IGNORE 
                                        , CustomReader.DATA_TYPE_IGNORE_REST 
                                        , CustomReader.DATA_TYPE_PAYMENT 
                                        , CustomReader.DATA_TYPE_DEPOSIT 
                                        , CustomReader.DATA_TYPE_DATE 
                                        , CustomReader.DATA_TYPE_DATE_AVAILABLE 
                                        , CustomReader.DATA_TYPE_DATE_INITIATED 
                                        , CustomReader.DATA_TYPE_DATE_POSTED 
                                        , CustomReader.DATA_TYPE_DATE_PURCHASED 
                                        , CustomReader.DATA_TYPE_CHECK_NUMBER 
                                        , CustomReader.DATA_TYPE_DESCRIPTION 
                                        , CustomReader.DATA_TYPE_MEMO 
                                        , CustomReader.DATA_TYPE_ACCOUNT_NAME 
                                        , CustomReader.DATA_TYPE_CATEGORY_NAME
                                    };
        
        String [] allowEmptyFlag = { "", "Can Be Blank", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
//        ArrayList<javax.swing.JComboBox> dataTypesList = new ArrayList<javax.swing.JComboBox>( 10 );
   //     ArrayList<javax.swing.JComboBox> emptyFlagsList = new ArrayList<javax.swing.JComboBox>( 10 );
        ArrayList<String> regexsList = new ArrayList<String>( 10 );
        ArrayList<String> dataTypesList = new ArrayList<String>( 10 );
        ArrayList<String> emptyFlagsList = new ArrayList<String>( 10 );
        //ArrayList<String> dateFormatList = new ArrayList<String>( 10 );
        HashMap<String, CustomReaderData> ReaderConfigsHM = new HashMap<String, CustomReaderData>();
        HashMap<String, TransactionReader> ReaderHM = new HashMap<String, TransactionReader>();

       SortedMap<String, Charset> encodings = Charset.availableCharsets();
       Set<String> fileEncodingNames = encodings.keySet();
        
       //private static CitiBankCanadaReader citiBankCanadaReader = new CitiBankCanadaReader();
       //private static INGNetherlandsReader ingNetherlandsReader = new INGNetherlandsReader();
       //private static SimpleCreditDebitReader simpleCreditDebitReader = new SimpleCreditDebitReader();
       //private static WellsFargoReader wellsFargoReader = new WellsFargoReader();
       //private static YodleeReader yodleeReader = new YodleeReader();
       //private static BbvaCompassBankReader bbvaCompassReader = new BbvaCompassBankReader();


    /** Creates new form CustomerReaderDialog */
    public CustomReaderDialog( ImportDialog parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        
        regex0.setVisible( false );
        regex1.setVisible( false );
        regex2.setVisible( false );
        regex3.setVisible( false );
        regex4.setVisible( false );
        regex5.setVisible( false );
        regex6.setVisible( false );
        regex7.setVisible( false );
        regex8.setVisible( false );
        regex9.setVisible( false );
        
        this.setModal( false );
        this.addEscapeListener( this );        
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

    public boolean addReaderConfig()
        {
        message.setText( "" );
        if ( ReaderConfigsHM.containsKey( readerName.getText() ) )
            {
            message.setText( "A reader already exists by the name '" + readerName.getText() + "'" );
            return false;
            }
        
        CustomReaderData customReaderData = new CustomReaderData();
        customReaderData.setReaderName( readerName.getText() );
        customReaderData.setRegexsList( createNewRegexsList() );
        customReaderData.setDataTypesList( createNewDataTypesList() );
        customReaderData.setEmptyFlagsList( createNewEmptyFlagsList() );
        //customReaderData.setDateFormatList( readDateFormatList() );
        customReaderData.setFieldSeparatorChar( getFieldSeparatorChar() );
        customReaderData.setFileEncoding( getFileEncodingSelectedItem() );
        customReaderData.setHeaderLines( getHeaderLines() );
        customReaderData.setFooterLines( getFooterLines() );
        customReaderData.setDateFormatString( getDateFormatString() );

        customReaderData.setAmountCurrencyChar( getAmountCurrencyChar() );
        customReaderData.setAmountDecimalSignChar( getAmountDecimalSignChar() );
        customReaderData.setAmountGroupingSeparatorChar( getAmountGroupingSeparatorChar() );
        customReaderData.setAmountFormat( getAmountFormat() );
        customReaderData.setImportReverseOrderFlg( getImportReverseOrderFlg() );
        customReaderData.setUseRegexFlag( getUseRegexFlag() );
        customReaderData.setFilenameMatcher( getFilenameMatcher() );
        
        /*
        System.out.println( "add datatype===================================" );
        int i = 0;
        for ( String dataType : customReaderData.getDataTypesList() )
            {
            System.out.println( "add datatype " + i + " =" + dataType + "=" );
            i++;
            }
         */
        
        CustomReader customReader = new CustomReader( customReaderData );
        ReaderConfigsHM.put( readerName.getText(), customReaderData );
        ReaderHM.put( readerName.getText(), customReader );
        
        DefaultListModel listModel = (DefaultListModel) customReadersList.getModel();
        listModel.addElement( readerName.getText() );
        
        Settings.setCustomReaderConfig( customReaderData );
        
        this.parent.comboFileFormat1AddItem( customReader );
        
        customReader.createSupportedDateFormats( getDateFormatString() );
        this.parent.createSupportedDateFormats( getDateFormatString() );

        return true;
        }
    
    public boolean deleteReaderConfig()
        {
        message.setText( "" );
        DefaultListModel listModel = (DefaultListModel) customReadersList.getModel();
        int index = customReadersList.getSelectedIndex();
        //System.err.println( " selected index =" + index + "   item =" + listModel.getElementAt( index ) + "=" );
        
        Settings.removeCustomReaderConfig( ReaderConfigsHM.get( listModel.getElementAt( index ) ) );
        
        ReaderConfigsHM.remove( listModel.getElementAt( index ) );
        ReaderHM.remove( listModel.getElementAt( index ) );

        listModel.remove( index );
        clearReaderConfig();
        
        return true;
        }
    
    public TransactionReader getTransactionReader( String readerNameToGet )
        {
        message.setText( "" );
        if ( ! ReaderHM.containsKey( readerNameToGet ) )
            {
            message.setText( "There is no reader by that name '" + readerNameToGet + "'" );
            return null;
            }
        return ReaderHM.get( readerNameToGet );
        }
    
    public boolean getReaderConfig( String readerNameToGet )
        {
        message.setText( "" );
        if ( ! ReaderConfigsHM.containsKey( readerNameToGet ) )
            {
            message.setText( "There is no reader by that name '" + readerNameToGet + "'" );
            return false;
            }
        
        CustomReaderData customReaderData = ReaderConfigsHM.get( readerNameToGet );
        readerName.setText( readerNameToGet );
        regexsList = customReaderData.getRegexsList();
        dataTypesList = customReaderData.getDataTypesList();
        emptyFlagsList = customReaderData.getEmptyFlagsList();
        //dateFormatList = customReaderData.getDateFormatList();
        setFieldSeparatorChar( customReaderData.getFieldSeparatorChar() );
        setFileEncodingSelectedItem( customReaderData.getFileEncoding() );
        setHeaderLines( customReaderData.getHeaderLines() );
        setFooterLines( customReaderData.getFooterLines() );
        
        setAmountCurrencyChar( customReaderData.getAmountCurrencyChar() );
        setAmountDecimalSignChar( customReaderData.getAmountDecimalSignChar() );
        setAmountGroupingSeparatorChar( customReaderData.getAmountGroupingSeparatorChar() );
        setAmountFormat( customReaderData.getAmountFormat() );
        setImportReverseOrderFlg( customReaderData.getImportReverseOrderFlg() );
        setUseRegexFlag( customReaderData.getUseRegexFlag() );
        setFilenameMatcher( customReaderData.getFilenameMatcher() );

        DefaultListModel listModel = (DefaultListModel) customReadersList.getModel();
        customReadersList.setSelectedValue( readerNameToGet, true );

        System.err.println( "get regexsList arraylist =" + regexsList + "=" );
        System.err.println( "get dataTypesList arraylist =" + dataTypesList + "=" );
        System.err.println( "get emptyFlagsList arraylist =" + emptyFlagsList + "=" );

        /*
        int i = 0;
//            System.out.println( "get datatype===================================" );
//            System.out.println( "get datatype===================================" );
        for ( String dataType : dataTypesList )
            {
//            System.out.println( "get datatype " + i + " =" + dataType + "=" );
            i++;
            }
         */
        
//            System.out.println( "get regex ===================================" );
        regex0.setText( regexsList.get( 0 ) );
        regex1.setText( regexsList.get( 1 ) );
        regex2.setText( regexsList.get( 2 ) );
        regex3.setText( regexsList.get( 3 ) );
        regex4.setText( regexsList.get( 4 ) );
        regex5.setText( regexsList.get( 5 ) );
        regex6.setText( regexsList.get( 6 ) );
        regex7.setText( regexsList.get( 7 ) );
        regex8.setText( regexsList.get( 8 ) );
        regex9.setText( regexsList.get( 9 ) );

//            System.out.println( "get datatype ===================================" );
        dataType0.setSelectedItem( dataTypesList.get( 0 ) );
        dataType1.setSelectedItem( dataTypesList.get( 1 ) );
        dataType2.setSelectedItem( dataTypesList.get( 2 ) );
        dataType3.setSelectedItem( dataTypesList.get( 3 ) );
        dataType4.setSelectedItem( dataTypesList.get( 4 ) );
        dataType5.setSelectedItem( dataTypesList.get( 5 ) );
        dataType6.setSelectedItem( dataTypesList.get( 6 ) );
        dataType7.setSelectedItem( dataTypesList.get( 7 ) );
        dataType8.setSelectedItem( dataTypesList.get( 8 ) );
        dataType9.setSelectedItem( dataTypesList.get( 9 ) );

        /*
        System.out.println( "get datatype===================================" );
        System.out.println( "get datatype " + i + " =" + dataTypesList.get( 0 ) + "=" );
        System.out.println( "get datatype " + i + " =" + dataTypesList.get( 1 ) + "=" );
        System.out.println( "get datatype " + i + " =" + dataTypesList.get( 2 ) + "=" );
        System.out.println( "get datatype " + i + " =" + dataTypesList.get( 3 ) + "=" );
        System.out.println( "get datatype " + i + " =" + dataTypesList.get( 4 ) + "=" );
        System.out.println( "get datatype " + i + " =" + dataTypesList.get( 5 ) + "=" );
        System.out.println( "get datatype " + i + " =" + dataTypesList.get( 6 ) + "=" );
        System.out.println( "get datatype " + i + " =" + dataTypesList.get( 7 ) + "=" );
        System.out.println( "get datatype " + i + " =" + dataTypesList.get( 8 ) + "=" );
        System.out.println( "get datatype " + i + " =" + dataTypesList.get( 9 ) + "=" );
        */
        
        isNullable0.setSelectedItem( emptyFlagsList.get( 0 ) );
        isNullable1.setSelectedItem( emptyFlagsList.get( 1 ) );
        isNullable2.setSelectedItem( emptyFlagsList.get( 2 ) );
        isNullable3.setSelectedItem( emptyFlagsList.get( 3 ) );
        isNullable4.setSelectedItem( emptyFlagsList.get( 4 ) );
        isNullable5.setSelectedItem( emptyFlagsList.get( 5 ) );
        isNullable6.setSelectedItem( emptyFlagsList.get( 6 ) );
        isNullable7.setSelectedItem( emptyFlagsList.get( 7 ) );
        isNullable8.setSelectedItem( emptyFlagsList.get( 8 ) );
        isNullable9.setSelectedItem( emptyFlagsList.get( 9 ) );
                
        /*
        DefaultComboBoxModel dateFormatModel = new DefaultComboBoxModel();
        for ( String format : dateFormatList )
            {
            System.out.println( "add date format =" + format + "=" );
            dateFormatModel.addElement( format );
            }
        
        dateFormatCB.setModel( dateFormatModel );
            if ( this.parent != null )
                {
                this.parent.comboDateFormatSetModel( dateFormatModel );
                }
        
        TransactionReader customReader = ReaderHM.get( readerName.getText() );
        String [] tmpArray = new String[0];
        customReader.setSupportedDateFormats( dateFormatList.toArray( tmpArray ) );
         */
        
        CustomReader customReader = (CustomReader) ReaderHM.get( readerName.getText() );
        setDateFormatString( customReaderData.getDateFormatString() );

        customReader.createSupportedDateFormats( getDateFormatString() );
        this.parent.createSupportedDateFormats( getDateFormatString() );

        System.err.println( "getNumberOfCustomReaderFieldsUsed() =" + getNumberOfCustomReaderFieldsUsed() );
        return true;
        }
    
    public void clearReaderConfig()
        {
        setFieldSeparatorChar( ',' );
        setFileEncodingSelectedItem( TransactionReader.DEFAULT_ENCODING );
        setHeaderLines( 1 );
        setFooterLines( 0 );
        
        regex0.setText( "" );
        regex1.setText( "" );
        regex2.setText( "" );
        regex3.setText( "" );
        regex4.setText( "" );
        regex5.setText( "" );
        regex6.setText( "" );
        regex7.setText( "" );
        regex8.setText( "" );
        regex9.setText( "" );
        
        dataType0.setSelectedIndex( 0 );
        dataType1.setSelectedIndex( 0 );
        dataType2.setSelectedIndex( 0 );
        dataType3.setSelectedIndex( 0 );
        dataType4.setSelectedIndex( 0 );
        dataType5.setSelectedIndex( 0 );
        dataType6.setSelectedIndex( 0 );
        dataType7.setSelectedIndex( 0 );
        dataType8.setSelectedIndex( 0 );
        dataType9.setSelectedIndex( 0 );
        
        isNullable0.setSelectedIndex( 0 );
        isNullable1.setSelectedIndex( 0 );
        isNullable2.setSelectedIndex( 0 );
        isNullable3.setSelectedIndex( 0 );
        isNullable4.setSelectedIndex( 0 );
        isNullable5.setSelectedIndex( 0 );
        isNullable6.setSelectedIndex( 0 );
        isNullable7.setSelectedIndex( 0 );
        isNullable8.setSelectedIndex( 0 );
        isNullable9.setSelectedIndex( 0 );
        }

    public ArrayList<String> createNewRegexsList() 
        {
        ArrayList<String> newRegexsList = new ArrayList<String>( 10 );
//        Collections.copy( newDataTypesList, regexsList );
        
        newRegexsList.add( ((String)regex0.getText()));
        newRegexsList.add( ((String)regex1.getText()));
        newRegexsList.add( ((String)regex2.getText()));
        newRegexsList.add( ((String)regex3.getText()));
        newRegexsList.add( ((String)regex4.getText()));
        newRegexsList.add( ((String)regex5.getText()));
        newRegexsList.add( ((String)regex6.getText()));
        newRegexsList.add( ((String)regex7.getText()));
        newRegexsList.add( ((String)regex8.getText()));
        newRegexsList.add( ((String)regex9.getText()));        
                
//        for ( int i = 0; i < 10; i ++ )
//            {
//            newDataTypesList.add( new String( dataTypesList.get( i ) ) );
//            }
        return newRegexsList;
        }

    public ArrayList<String> createNewDataTypesList() 
        {
        ArrayList<String> newDataTypesList = new ArrayList<String>( 10 );
//        Collections.copy( newDataTypesList, dataTypesList );
        
        newDataTypesList.add( ((String)dataType0.getSelectedItem()));
        newDataTypesList.add( ((String)dataType1.getSelectedItem()));
        newDataTypesList.add( ((String)dataType2.getSelectedItem()));
        newDataTypesList.add( ((String)dataType3.getSelectedItem()));
        newDataTypesList.add( ((String)dataType4.getSelectedItem()));
        newDataTypesList.add( ((String)dataType5.getSelectedItem()));
        newDataTypesList.add( ((String)dataType6.getSelectedItem()));
        newDataTypesList.add( ((String)dataType7.getSelectedItem()));
        newDataTypesList.add( ((String)dataType8.getSelectedItem()));
        newDataTypesList.add( ((String)dataType9.getSelectedItem()));        
                
//        for ( int i = 0; i < 10; i ++ )
//            {
//            newDataTypesList.add( new String( dataTypesList.get( i ) ) );
//            }
        return newDataTypesList;
        }

    public ArrayList<String> createNewEmptyFlagsList() 
        {
        ArrayList<String> newEmptyFlagsList = new ArrayList<String>( 10 );
        newEmptyFlagsList.add( ((String)isNullable0.getSelectedItem()));
        newEmptyFlagsList.add( ((String)isNullable1.getSelectedItem()));
        newEmptyFlagsList.add( ((String)isNullable2.getSelectedItem()));
        newEmptyFlagsList.add( ((String)isNullable3.getSelectedItem()));
        newEmptyFlagsList.add( ((String)isNullable4.getSelectedItem()));
        newEmptyFlagsList.add( ((String)isNullable5.getSelectedItem()));
        newEmptyFlagsList.add( ((String)isNullable6.getSelectedItem()));
        newEmptyFlagsList.add( ((String)isNullable7.getSelectedItem()));
        newEmptyFlagsList.add( ((String)isNullable8.getSelectedItem()));
        newEmptyFlagsList.add( ((String)isNullable9.getSelectedItem()));        
        return newEmptyFlagsList;
        }

    /*
    public ArrayList<String> readDateFormatList() 
        {
        DefaultComboBoxModel dcbm = (DefaultComboBoxModel) dateFormatCB.getModel();
        
        int max = dcbm.getSize();
        ArrayList<String> newList = new ArrayList<String>( max );
        for ( int i = 0; i < max; i++ )
            {
            newList.add( ( (String) dcbm.getElementAt( i ) ) );
            }
        return newList;
        }
    */
    /*
    public ArrayList<javax.swing.JComboBox> createNewEmptyFlagsList() 
        {
        ArrayList<javax.swing.JComboBox> newEmptyFlagsList = new ArrayList<javax.swing.JComboBox>( 10 );
        for ( int i = 0; i < 10; i ++ )
            {
            javax.swing.JComboBox jcb = new javax.swing.JComboBox( new javax.swing.DefaultComboBoxModel( allowEmptyFlag ) );
            jcb.setSelectedItem( emptyFlagsList.get( i ).getSelectedItem() );
            newEmptyFlagsList.add( jcb );
            }
        return newEmptyFlagsList;
        }
     */
    
    public String getRegexsListSelectedItem( int index ) {
        return (String) regexsList.get( index );
    }

    public String getDataTypesListSelectedItem( int index ) {
        return (String) dataTypesList.get( index );
    }

    public String getEmptyFlagsListSelectedItem( int index ) {
        return (String) emptyFlagsList.get( index );
    }

    public String getDateFormatSelected() {
        //return (String) dateFormatCB.getSelectedItem();
        return (String) dateFormatCr.getText();
    }
    
    public void setDateFormatString( String xxx) {
        //dateFormatCB.setSelectedItem( xxx );
        dateFormatCr.setText( xxx );
    }

    public String getDateFormatString() {
        //return (String) dateFormatCB.getSelectedItem();
        return dateFormatCr.getText();
    }

    public int getNumberOfCustomReaderFieldsUsed()
        {
        int c = 0;
        int max = dataTypesList.size();
        
        for (       ; c < max; c++ )
            {
            //System.err.println( "(String) dataTypesList.get(" + c + ") =" + (String) dataTypesList.get( c ) + "=" );
            if ( ((String) dataTypesList.get( c )).equalsIgnoreCase( "" ) )
                return c;
            }
        return c;
        }

    public void setHeaderLines( int xxx ) 
        {
        headerLines.setText( String.valueOf( xxx ) );
        //System.err.println( "CustomReaderDialog.setHeaderLines(" + xxx +") text =" + headerLines.getText().trim() + "=" );
        }
    
    public int getHeaderLines() {
        int x = 0;
        //System.err.println( "CustomReaderDialog.getHeaderLines() text =" + headerLines.getText().trim() + "=" );
        try
            {
            x = Integer.parseInt( headerLines.getText().trim() );
            }
        catch ( Exception ex )
            {
            ;
            }
        return x;
    }

    public void setFooterLines( int xxx ) 
        {
        footerLines.setText( String.valueOf( xxx ) );
        //System.err.println( "CustomReaderDialog.setFooterLines(" + xxx +") text =" + footerLines.getText().trim() + "=" );
        }
    
    public int getFooterLines() {
        int x = 0;
        //System.err.println( "CustomReaderDialog.getFooterLines() text =" + footerLines.getText().trim() + "=" );
        try
            {
            x = Integer.parseInt( footerLines.getText().trim() );
            }
        catch ( Exception ex )
            {
            ;
            }
        return x;
    }

    public void setFieldSeparatorChar( int xxx) {
        fieldSeparatorChar.setText( String.valueOf( Character.toString( (char) xxx ) ) );
    }

    public int getFieldSeparatorChar() {
        return fieldSeparatorChar.getText().charAt( 0 );
    }
    
    public void setFileEncodingSelectedItem( String xxx) {
        fileEncodingCB.setSelectedItem( xxx );
    }

    public String getFileEncodingSelectedItem() {
        return (String) fileEncodingCB.getSelectedItem();
    }

    public void setAmountCurrencyChar( int xxx) {
        amountCurrencyChar.setText( String.valueOf( Character.toString( (char) xxx ) ) );
    }

    public int getAmountCurrencyChar() {
        return amountCurrencyChar.getText().charAt( 0 );
    }
    
    public void setAmountDecimalSignChar( int xxx) {
        amountDecimalSignChar.setText( String.valueOf( Character.toString( (char) xxx ) ) );
    }

    public int getAmountDecimalSignChar() {
        return amountDecimalSignChar.getText().charAt( 0 );
    }
    
    public void setAmountGroupingSeparatorChar( int xxx) {
        amountGroupingSeparatorChar.setText( String.valueOf( Character.toString( (char) xxx ) ) );
    }

    public int getAmountGroupingSeparatorChar() {
        return amountGroupingSeparatorChar.getText().charAt( 0 );
    }
    
    public void setAmountFormat( String xxx) {
        amountFormat.setText( xxx );
    }
    
    public String getAmountFormat() {
        return (String) amountFormat.getText();
    }
    
    public void setImportReverseOrderFlg( boolean xxx) {
        importReverseOrderFlg.setSelected( xxx  );
    }
    
    public boolean getImportReverseOrderFlg() {
        return importReverseOrderFlg.isSelected();
    }

    public void setUseRegexFlag( boolean xxx) {
        useRegexFlag.setSelected( xxx  );
    }
    
    public boolean getUseRegexFlag() {
        return useRegexFlag.isSelected();
    }

    public void setFilenameMatcher( String xxx) {
        filenameMatcher.setText( xxx );
    }
    
    public String getFilenameMatcher() {
        return (String) filenameMatcher.getText();
    }
    
    protected void init()
        {
        /*
        dataType0.setSelectedItem( "date" );
        dataType1.setSelectedItem( "amount" );
        dataType2.setSelectedItem( "check number" );
        dataType3 .setSelectedItem( "skip" );
        dataType4.setSelectedItem( "description" );
        dataType5.setSelectedItem( "memo" );
        isNullable2.setSelectedItem( "Can Be Blank" );
        isNullable4.setSelectedItem( "Can Be Blank" );
        isNullable5.setSelectedItem( "Can Be Blank" );
        */
        
        ReaderConfigsHM = Settings.createReaderConfigsHM();
        ReaderHM = Settings.getReaderHM();  // not great, but for now the call above must be first because it sets the value for this one to read also.

        /******  Quit using the built in readers !  Force people to make their own
        ReaderConfigsHM.put( "citiBankCanadaReader", null );
        ReaderHM.put( "citiBankCanadaReader", citiBankCanadaReader );
        
        ReaderConfigsHM.put( "ingNetherlandsReader", null );
        ReaderHM.put( "ingNetherlandsReader", ingNetherlandsReader );
        
        ReaderConfigsHM.put( "simpleCreditDebitReader", null );
        ReaderHM.put( "simpleCreditDebitReader", simpleCreditDebitReader );
        
        ReaderConfigsHM.put( "wellsFargoReader", null );
        ReaderHM.put( "wellsFargoReader", wellsFargoReader );

        ReaderConfigsHM.put( "yodleeReader", null );
        ReaderHM.put( "yodleeReader", yodleeReader );
		
        ReaderConfigsHM.put( "bbvaCompassReader", null );
        ReaderHM.put( "bbvaCompassReader", bbvaCompassReader );
	******/
        
        DefaultListModel listModel = (DefaultListModel) customReadersList.getModel();

// ???        this.parent.comboFileFormat1AddItem( "" );

        // For keys of a map
        for ( Iterator it=ReaderHM.keySet().iterator(); it.hasNext(); ) 
            {
            String readerName = (String) it.next();
            System.err.println( "fill out readerName =" + readerName + "=" );
            if ( ReaderHM.get( readerName ).isCustomReaderFlag() )
                {
                listModel.addElement( readerName );
                
                // needs to be in Settings()
//                CustomReader customReader = (CustomReader) getTransactionReader( readerName );
//                CustomReaderData customReaderData = ReaderConfigsHM.get( readerName );
//
//                customReader.createSupportedDateFormats( customReaderData.getDateFormatString() );
                }
            if ( this.parent != null )
                {
                System.err.println( "call add readerName to import dlg reader list =" + readerName + "=" );
                this.parent.comboFileFormat1AddItem( ReaderHM.get( readerName ) );
                }
            }
        
        regexsList.add( (String)regex0.getText() );
        regexsList.add( (String)regex1.getText() );
        regexsList.add( (String)regex2.getText() );
        regexsList.add( (String)regex3.getText() );
        regexsList.add( (String)regex4.getText() );
        regexsList.add( (String)regex5.getText() );
        regexsList.add( (String)regex6.getText() );
        regexsList.add( (String)regex7.getText() );
        regexsList.add( (String)regex8.getText() );
        regexsList.add( (String)regex9.getText() );
        
        dataTypesList.add( (String)dataType0.getSelectedItem() );
        dataTypesList.add( (String)dataType1.getSelectedItem() );
        dataTypesList.add( (String)dataType2.getSelectedItem() );
        dataTypesList.add( (String)dataType3.getSelectedItem() );
        dataTypesList.add( (String)dataType4.getSelectedItem() );
        dataTypesList.add( (String)dataType5.getSelectedItem() );
        dataTypesList.add( (String)dataType6.getSelectedItem() );
        dataTypesList.add( (String)dataType7.getSelectedItem() );
        dataTypesList.add( (String)dataType8.getSelectedItem() );
        dataTypesList.add( (String)dataType9.getSelectedItem() );

        emptyFlagsList.add( (String)isNullable0.getSelectedItem() );
        emptyFlagsList.add( (String)isNullable1.getSelectedItem() );
        emptyFlagsList.add( (String)isNullable2.getSelectedItem() );
        emptyFlagsList.add( (String)isNullable3.getSelectedItem() );
        emptyFlagsList.add( (String)isNullable4.getSelectedItem() );
        emptyFlagsList.add( (String)isNullable5.getSelectedItem() );
        emptyFlagsList.add( (String)isNullable6.getSelectedItem() );
        emptyFlagsList.add( (String)isNullable7.getSelectedItem() );
        emptyFlagsList.add( (String)isNullable8.getSelectedItem() );
        emptyFlagsList.add( (String)isNullable9.getSelectedItem() );

        // Populate with all possible encodings
        SortedMap<String, Charset> encodings = Charset.availableCharsets();
        Set<String> encodingNames = encodings.keySet();
        for ( Iterator<String> i = encodingNames.iterator(); i.hasNext(); ) 
            {
            fileEncodingCB.addItem( i.next() );
            }
        // Set default Encoding to be UTF-8
        for ( int i = 0; i < fileEncodingCB.getItemCount(); i++ )
            {
            if ( TransactionReader.DEFAULT_ENCODING.equals( fileEncodingCB.getItemAt( i ) ) ) 
                {
                fileEncodingCB.setSelectedIndex( i );
                break;
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

        jLabel1 = new javax.swing.JLabel();
        readerName = new javax.swing.JTextField();
        dataType1 = new javax.swing.JComboBox();
        isNullable1 = new javax.swing.JComboBox();
        dataType2 = new javax.swing.JComboBox();
        dataType4 = new javax.swing.JComboBox();
        dataType0 = new javax.swing.JComboBox();
        isNullable0 = new javax.swing.JComboBox();
        isNullable2 = new javax.swing.JComboBox();
        isNullable3 = new javax.swing.JComboBox();
        isNullable4 = new javax.swing.JComboBox();
        isNullable5 = new javax.swing.JComboBox();
        isNullable6 = new javax.swing.JComboBox();
        isNullable7 = new javax.swing.JComboBox();
        isNullable8 = new javax.swing.JComboBox();
        isNullable9 = new javax.swing.JComboBox();
        dataType3 = new javax.swing.JComboBox();
        dataType5 = new javax.swing.JComboBox();
        dataType6 = new javax.swing.JComboBox();
        dataType7 = new javax.swing.JComboBox();
        dataType8 = new javax.swing.JComboBox();
        dataType9 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        saveBtn = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        headerLines = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        customReadersList = new javax.swing.JList();
        jLabel14 = new javax.swing.JLabel();
        addBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        message = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        doneBtn = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        fieldSeparatorChar = new javax.swing.JTextField();
        resetFieldsBtn = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        dateFormatCr = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        amountGroupingSeparatorChar = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        amountDecimalSignChar = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        amountCurrencyChar = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        amountFormat = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        footerLines = new javax.swing.JTextField();
        importReverseOrderFlg = new javax.swing.JCheckBox();
        fileEncodingLbl = new javax.swing.JLabel();
        fileEncodingCB = new javax.swing.JComboBox();
        jLabel27 = new javax.swing.JLabel();
        regex0 = new javax.swing.JTextField();
        regex1 = new javax.swing.JTextField();
        regex2 = new javax.swing.JTextField();
        regex3 = new javax.swing.JTextField();
        regex4 = new javax.swing.JTextField();
        regex5 = new javax.swing.JTextField();
        regex6 = new javax.swing.JTextField();
        regex7 = new javax.swing.JTextField();
        regex8 = new javax.swing.JTextField();
        regex9 = new javax.swing.JTextField();
        useRegexFlag = new javax.swing.JCheckBox();
        filenameMatcher = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(780, 600));
        setPreferredSize(new java.awt.Dimension(780, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Reader Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        getContentPane().add(jLabel1, gridBagConstraints);

        readerName.setMinimumSize(new java.awt.Dimension(160, 25));
        readerName.setPreferredSize(new java.awt.Dimension(160, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        getContentPane().add(readerName, gridBagConstraints);

        dataType1.setModel(new javax.swing.DefaultComboBoxModel( dataTypes ) );
        dataType1.setMinimumSize(new java.awt.Dimension(150, 25));
        dataType1.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(dataType1, gridBagConstraints);

        isNullable1.setModel(new javax.swing.DefaultComboBoxModel( allowEmptyFlag ) );
        isNullable1.setMinimumSize(new java.awt.Dimension(150, 25));
        isNullable1.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(isNullable1, gridBagConstraints);

        dataType2.setModel(new javax.swing.DefaultComboBoxModel( dataTypes ) );
        dataType2.setMinimumSize(new java.awt.Dimension(150, 25));
        dataType2.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(dataType2, gridBagConstraints);

        dataType4.setModel(new javax.swing.DefaultComboBoxModel( dataTypes ) );
        dataType4.setMinimumSize(new java.awt.Dimension(150, 25));
        dataType4.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(dataType4, gridBagConstraints);

        dataType0.setModel(new javax.swing.DefaultComboBoxModel( dataTypes ) );
        dataType0.setMinimumSize(new java.awt.Dimension(150, 25));
        dataType0.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(dataType0, gridBagConstraints);

        isNullable0.setModel(new javax.swing.DefaultComboBoxModel( allowEmptyFlag ) );
        isNullable0.setMinimumSize(new java.awt.Dimension(150, 25));
        isNullable0.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(isNullable0, gridBagConstraints);

        isNullable2.setModel(new javax.swing.DefaultComboBoxModel( allowEmptyFlag ) );
        isNullable2.setMinimumSize(new java.awt.Dimension(150, 25));
        isNullable2.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(isNullable2, gridBagConstraints);

        isNullable3.setModel(new javax.swing.DefaultComboBoxModel( allowEmptyFlag ) );
        isNullable3.setMinimumSize(new java.awt.Dimension(150, 25));
        isNullable3.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(isNullable3, gridBagConstraints);

        isNullable4.setModel(new javax.swing.DefaultComboBoxModel( allowEmptyFlag ) );
        isNullable4.setMinimumSize(new java.awt.Dimension(150, 25));
        isNullable4.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(isNullable4, gridBagConstraints);

        isNullable5.setModel(new javax.swing.DefaultComboBoxModel( allowEmptyFlag ) );
        isNullable5.setMinimumSize(new java.awt.Dimension(150, 25));
        isNullable5.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(isNullable5, gridBagConstraints);

        isNullable6.setModel(new javax.swing.DefaultComboBoxModel( allowEmptyFlag ) );
        isNullable6.setMinimumSize(new java.awt.Dimension(150, 25));
        isNullable6.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(isNullable6, gridBagConstraints);

        isNullable7.setModel(new javax.swing.DefaultComboBoxModel( allowEmptyFlag ) );
        isNullable7.setMinimumSize(new java.awt.Dimension(150, 25));
        isNullable7.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(isNullable7, gridBagConstraints);

        isNullable8.setModel(new javax.swing.DefaultComboBoxModel( allowEmptyFlag ) );
        isNullable8.setMinimumSize(new java.awt.Dimension(150, 25));
        isNullable8.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(isNullable8, gridBagConstraints);

        isNullable9.setModel(new javax.swing.DefaultComboBoxModel( allowEmptyFlag ) );
        isNullable9.setMinimumSize(new java.awt.Dimension(150, 25));
        isNullable9.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(isNullable9, gridBagConstraints);

        dataType3.setModel(new javax.swing.DefaultComboBoxModel( dataTypes ) );
        dataType3.setMinimumSize(new java.awt.Dimension(150, 25));
        dataType3.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(dataType3, gridBagConstraints);

        dataType5.setModel(new javax.swing.DefaultComboBoxModel( dataTypes ) );
        dataType5.setMinimumSize(new java.awt.Dimension(150, 25));
        dataType5.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(dataType5, gridBagConstraints);

        dataType6.setModel(new javax.swing.DefaultComboBoxModel( dataTypes ) );
        dataType6.setMinimumSize(new java.awt.Dimension(150, 25));
        dataType6.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(dataType6, gridBagConstraints);

        dataType7.setModel(new javax.swing.DefaultComboBoxModel( dataTypes ) );
        dataType7.setMinimumSize(new java.awt.Dimension(150, 25));
        dataType7.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(dataType7, gridBagConstraints);

        dataType8.setModel(new javax.swing.DefaultComboBoxModel( dataTypes ) );
        dataType8.setMinimumSize(new java.awt.Dimension(150, 25));
        dataType8.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(dataType8, gridBagConstraints);

        dataType9.setModel(new javax.swing.DefaultComboBoxModel( dataTypes ) );
        dataType9.setMinimumSize(new java.awt.Dimension(150, 25));
        dataType9.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(dataType9, gridBagConstraints);

        jLabel2.setText("1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        getContentPane().add(jLabel2, gridBagConstraints);

        jLabel3.setText("2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        getContentPane().add(jLabel3, gridBagConstraints);

        jLabel4.setText("3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        getContentPane().add(jLabel4, gridBagConstraints);

        jLabel5.setText("4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        getContentPane().add(jLabel5, gridBagConstraints);

        jLabel6.setText("5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        getContentPane().add(jLabel6, gridBagConstraints);

        jLabel7.setText("6");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        getContentPane().add(jLabel7, gridBagConstraints);

        jLabel8.setText("7");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        getContentPane().add(jLabel8, gridBagConstraints);

        jLabel9.setText("8");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        getContentPane().add(jLabel9, gridBagConstraints);

        jLabel10.setText("9");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 19;
        getContentPane().add(jLabel10, gridBagConstraints);

        jLabel11.setText("10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 20;
        getContentPane().add(jLabel11, gridBagConstraints);

        saveBtn.setText("Save");
        saveBtn.setMaximumSize(new java.awt.Dimension(85, 35));
        saveBtn.setMinimumSize(new java.awt.Dimension(74, 24));
        saveBtn.setPreferredSize(new java.awt.Dimension(74, 24));
        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 22;
        getContentPane().add(saveBtn, gridBagConstraints);

        jLabel12.setText(" ");
        jLabel12.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        getContentPane().add(jLabel12, gridBagConstraints);

        jLabel13.setText("Number of Footer Lines:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        getContentPane().add(jLabel13, gridBagConstraints);

        headerLines.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        headerLines.setText("1");
        headerLines.setMinimumSize(new java.awt.Dimension(40, 25));
        headerLines.setPreferredSize(new java.awt.Dimension(40, 25));
        headerLines.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                headerLinesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        getContentPane().add(headerLines, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(160, 85));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(160, 160));

        customReadersList.setModel(new DefaultListModel() );
        customReadersList.setMaximumSize(new java.awt.Dimension(32767, 32767));
        customReadersList.setMinimumSize(new java.awt.Dimension(160, 85));
        customReadersList.setPreferredSize(new java.awt.Dimension(160, 85));
        customReadersList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                customReadersListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(customReadersList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        getContentPane().add(jScrollPane1, gridBagConstraints);

        jLabel14.setText(" ");
        jLabel14.setMaximumSize(new java.awt.Dimension(25, 15));
        jLabel14.setMinimumSize(new java.awt.Dimension(25, 15));
        jLabel14.setPreferredSize(new java.awt.Dimension(25, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 10;
        getContentPane().add(jLabel14, gridBagConstraints);

        addBtn.setText("Add");
        addBtn.setMaximumSize(new java.awt.Dimension(85, 35));
        addBtn.setMinimumSize(new java.awt.Dimension(74, 24));
        addBtn.setPreferredSize(new java.awt.Dimension(74, 24));
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(addBtn, gridBagConstraints);

        deleteBtn.setText("Delete");
        deleteBtn.setMaximumSize(new java.awt.Dimension(85, 35));
        deleteBtn.setMinimumSize(new java.awt.Dimension(74, 24));
        deleteBtn.setPreferredSize(new java.awt.Dimension(74, 24));
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(deleteBtn, gridBagConstraints);

        jLabel15.setText("List of Readers:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(jLabel15, gridBagConstraints);

        message.setForeground(new java.awt.Color(255, 0, 0));
        message.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 24;
        gridBagConstraints.gridwidth = 5;
        getContentPane().add(message, gridBagConstraints);

        jLabel16.setText(" ");
        jLabel16.setMinimumSize(new java.awt.Dimension(150, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 23;
        getContentPane().add(jLabel16, gridBagConstraints);

        jLabel17.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(jLabel17, gridBagConstraints);

        doneBtn.setText("Done");
        doneBtn.setMaximumSize(new java.awt.Dimension(85, 35));
        doneBtn.setMinimumSize(new java.awt.Dimension(74, 24));
        doneBtn.setPreferredSize(new java.awt.Dimension(74, 24));
        doneBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 22;
        getContentPane().add(doneBtn, gridBagConstraints);

        jLabel18.setText("CSV Field Separator:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        getContentPane().add(jLabel18, gridBagConstraints);

        fieldSeparatorChar.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fieldSeparatorChar.setText(",");
        fieldSeparatorChar.setMinimumSize(new java.awt.Dimension(20, 25));
        fieldSeparatorChar.setPreferredSize(new java.awt.Dimension(20, 25));
        fieldSeparatorChar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldSeparatorCharActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        getContentPane().add(fieldSeparatorChar, gridBagConstraints);

        resetFieldsBtn.setText("Reset Fields");
        resetFieldsBtn.setMinimumSize(new java.awt.Dimension(100, 25));
        resetFieldsBtn.setPreferredSize(new java.awt.Dimension(110, 25));
        resetFieldsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetFieldsBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.weightx = 0.2;
        getContentPane().add(resetFieldsBtn, gridBagConstraints);

        jLabel19.setText("Date Format:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        getContentPane().add(jLabel19, gridBagConstraints);

        dateFormatCr.setText("YYYY-MM-DD");
        dateFormatCr.setToolTipText("<html>\nYou can do things like: MM/DD/YYYY, MM.DD.YY, YY-MM-DD<br/>\nhttp://download.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html");
        dateFormatCr.setMinimumSize(new java.awt.Dimension(100, 25));
        dateFormatCr.setPreferredSize(new java.awt.Dimension(100, 25));
        dateFormatCr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateFormatCrActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        getContentPane().add(dateFormatCr, gridBagConstraints);

        jLabel20.setText("             ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        getContentPane().add(jLabel20, gridBagConstraints);

        jLabel21.setText("Grouping separator:");
        jLabel21.setEnabled(false);
        jLabel21.setFocusable(false);
        jLabel21.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        getContentPane().add(jLabel21, gridBagConstraints);

        amountGroupingSeparatorChar.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        amountGroupingSeparatorChar.setText(",");
        amountGroupingSeparatorChar.setEnabled(false);
        amountGroupingSeparatorChar.setFocusable(false);
        amountGroupingSeparatorChar.setMinimumSize(new java.awt.Dimension(20, 25));
        amountGroupingSeparatorChar.setPreferredSize(new java.awt.Dimension(20, 25));
        amountGroupingSeparatorChar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                amountGroupingSeparatorCharActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(amountGroupingSeparatorChar, gridBagConstraints);

        jLabel22.setText("Decimal Sign:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        getContentPane().add(jLabel22, gridBagConstraints);

        amountDecimalSignChar.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        amountDecimalSignChar.setText(".");
        amountDecimalSignChar.setMinimumSize(new java.awt.Dimension(20, 25));
        amountDecimalSignChar.setPreferredSize(new java.awt.Dimension(20, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(amountDecimalSignChar, gridBagConstraints);

        jLabel23.setText("Currency Symbol:");
        jLabel23.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        getContentPane().add(jLabel23, gridBagConstraints);

        amountCurrencyChar.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        amountCurrencyChar.setText(" ");
        amountCurrencyChar.setEnabled(false);
        amountCurrencyChar.setMinimumSize(new java.awt.Dimension(40, 25));
        amountCurrencyChar.setPreferredSize(new java.awt.Dimension(40, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(amountCurrencyChar, gridBagConstraints);

        jLabel24.setText("Amount Format:");
        jLabel24.setEnabled(false);
        jLabel24.setFocusable(false);
        jLabel24.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        getContentPane().add(jLabel24, gridBagConstraints);

        amountFormat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        amountFormat.setText(" ");
        amountFormat.setEnabled(false);
        amountFormat.setFocusable(false);
        amountFormat.setMinimumSize(new java.awt.Dimension(160, 25));
        amountFormat.setPreferredSize(new java.awt.Dimension(160, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(amountFormat, gridBagConstraints);

        jLabel25.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        getContentPane().add(jLabel25, gridBagConstraints);

        jLabel26.setText("Number of Header Lines:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        getContentPane().add(jLabel26, gridBagConstraints);

        footerLines.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        footerLines.setText("0");
        footerLines.setMinimumSize(new java.awt.Dimension(40, 25));
        footerLines.setPreferredSize(new java.awt.Dimension(40, 25));
        footerLines.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                footerLinesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        getContentPane().add(footerLines, gridBagConstraints);

        importReverseOrderFlg.setText("Import Transactions in Reverse Order.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(importReverseOrderFlg, gridBagConstraints);

        fileEncodingLbl.setText("File Encoding:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        getContentPane().add(fileEncodingLbl, gridBagConstraints);

        fileEncodingCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { }));
        fileEncodingCB.setMinimumSize(new java.awt.Dimension(120, 25));
        fileEncodingCB.setPreferredSize(new java.awt.Dimension(140, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        getContentPane().add(fileEncodingCB, gridBagConstraints);

        jLabel27.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        getContentPane().add(jLabel27, gridBagConstraints);

        regex0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regex0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        getContentPane().add(regex0, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        getContentPane().add(regex1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        getContentPane().add(regex2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        getContentPane().add(regex3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        getContentPane().add(regex4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        getContentPane().add(regex5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        getContentPane().add(regex6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        getContentPane().add(regex7, gridBagConstraints);

        regex8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regex8ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        getContentPane().add(regex8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        getContentPane().add(regex9, gridBagConstraints);

        useRegexFlag.setText("Use Regex to Parse Fields");
        useRegexFlag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useRegexFlagActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        getContentPane().add(useRegexFlag, gridBagConstraints);

        filenameMatcher.setText(".*\\.(csv|CSV)");
        filenameMatcher.setMinimumSize(new java.awt.Dimension(160, 25));
        filenameMatcher.setPreferredSize(new java.awt.Dimension(160, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        getContentPane().add(filenameMatcher, gridBagConstraints);

        jLabel28.setText("Filename Matcher:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        getContentPane().add(jLabel28, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
        message.setText( "" );

        if ( ! ReaderConfigsHM.containsKey( readerName.getText() ) )
            {
            message.setText( "There is no reader by that name '" + readerName.getText() + "'" );
            }
        
//        int i = 0;
//        for ( String dataType : dataTypesList )
//            {
//            System.out.println( "datatype " + i + " =" + dataType + "=" );
//            i++;
//            }

        try
            {
            int x = Integer.parseInt( headerLines.getText().trim() );
            if ( x < 0 )
                throw new Exception();
            }
        catch ( Exception ex )
            {
            message.setText( "Number of Header Lines must be 0 or more" );
            return;
            }
        
        try
            {
            int x = Integer.parseInt( footerLines.getText().trim() );
            if ( x < 0 )
                throw new Exception();
            }
        catch ( Exception ex )
            {
            message.setText( "Number of Footer Lines must be 0 or more" );
            return;
            }
        
        CustomReaderData customReaderData = ReaderConfigsHM.get( readerName.getText() );
        
        customReaderData.setReaderName( readerName.getText() );
        customReaderData.setRegexsList( createNewRegexsList() );
        customReaderData.setDataTypesList( createNewDataTypesList() );
        customReaderData.setEmptyFlagsList( createNewEmptyFlagsList() );
        //customReaderData.setDateFormatList( readDateFormatList() );
        customReaderData.setFieldSeparatorChar( getFieldSeparatorChar() );
        customReaderData.setFileEncoding( getFileEncodingSelectedItem() );
        customReaderData.setHeaderLines( getHeaderLines() );
        customReaderData.setFooterLines( getFooterLines() );
        customReaderData.setDateFormatString( getDateFormatString() );
        
        customReaderData.setAmountCurrencyChar( getAmountCurrencyChar() );
        customReaderData.setAmountDecimalSignChar( getAmountDecimalSignChar() );
        customReaderData.setAmountGroupingSeparatorChar( getAmountGroupingSeparatorChar() );
        customReaderData.setAmountFormat( getAmountFormat() );
        customReaderData.setImportReverseOrderFlg( getImportReverseOrderFlg() );
        customReaderData.setUseRegexFlag( getUseRegexFlag() );
        customReaderData.setFilenameMatcher( getFilenameMatcher() );

        ReaderConfigsHM.put( readerName.getText(), customReaderData );
        // *** I could get and replace the existing one but just do this for now until things work  ! ! !
        CustomReader customReader = new CustomReader( customReaderData );
        ReaderHM.put( readerName.getText(), customReader );

        customReader.createSupportedDateFormats( getDateFormatString() );
        this.parent.createSupportedDateFormats( getDateFormatString() );

        Settings.setCustomReaderConfig( customReaderData );
    }//GEN-LAST:event_saveBtnActionPerformed

    private void headerLinesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_headerLinesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_headerLinesActionPerformed

    private void doneBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneBtnActionPerformed
        this.setVisible( false );
        
        /**  turning all this off so we do not test all readers when they hit 'Done'
        parent.setSkipDuringInit( true );
        parent.fileChanged();
        this.parent.comboFileFormat1SetItem( ReaderHM.get( readerName.getText() ) );
        parent.setSkipDuringInit( false );
        //System.out.println( "done button  (String) dateFormatCB.getSelectedItem() =" + (String) dateFormatCB.getSelectedItem() + "=" );
        //this.parent.comboDateFormatSetItem( getDateFormatString() );
        this.parent.createSupportedDateFormats( getDateFormatString() );
        **/
    }//GEN-LAST:event_doneBtnActionPerformed

    private void fieldSeparatorCharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldSeparatorCharActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldSeparatorCharActionPerformed

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        addReaderConfig();
    }//GEN-LAST:event_addBtnActionPerformed

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        deleteReaderConfig();
    }//GEN-LAST:event_deleteBtnActionPerformed

    private void customReadersListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customReadersListMouseClicked
        DefaultListModel listModel = (DefaultListModel) customReadersList.getModel();
        int index = customReadersList.getSelectedIndex();
        getReaderConfig( (String) listModel.getElementAt( index ) );
        useRegexFlagActionPerformed( null );
    }//GEN-LAST:event_customReadersListMouseClicked

    private void resetFieldsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetFieldsBtnActionPerformed
        clearReaderConfig();
    }//GEN-LAST:event_resetFieldsBtnActionPerformed

    private void dateFormatCrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateFormatCrActionPerformed
            ;
    }//GEN-LAST:event_dateFormatCrActionPerformed

    private void amountGroupingSeparatorCharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_amountGroupingSeparatorCharActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_amountGroupingSeparatorCharActionPerformed

    private void footerLinesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_footerLinesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_footerLinesActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        DecimalFormatSymbols decForSyms = new DecimalFormatSymbols();
        DecimalFormat decFormat = (DecimalFormat) DecimalFormat.getInstance();
        int numDigits = decFormat.getCurrency().getDefaultFractionDigits();
        amountCurrencyChar.setText( decFormat.getCurrency().getSymbol() );
        String format = "0";
        int i;
    //    if ( decFormat.getmax > 0 )
    //        {
            format += decForSyms.getDecimalSeparator();
    //        }
        //System.out.println( "decFormat.getMinimumIntegerDigits() =" + decFormat.getMinimumIntegerDigits() );
        for ( i = 0; i < 2; i ++ )
            {
            format+= "0";
            }

        //amountDecimalSignChar.setText(  );
        int groupSize = decFormat.getGroupingSize();
        char groupSep = decForSyms.getGroupingSeparator();
        amountGroupingSeparatorChar.setText( groupSep + "" );
        amountDecimalSignChar.setText( decForSyms.getDecimalSeparator() + "" );

        int gscnt = 1;
        for ( i = 1; i < 10; i ++ )
            {
            if ( gscnt == groupSize )
                {
                format = groupSep + format;
                gscnt = 1;
                }
            else
                {
                gscnt ++;
                }
            format = "#" + format;
            }
        format = decForSyms.getCurrencySymbol() + format;
        amountFormat.setText( format );
    }//GEN-LAST:event_formWindowOpened

    private void regex0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regex0ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_regex0ActionPerformed

    private void regex8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regex8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_regex8ActionPerformed

    private void useRegexFlagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useRegexFlagActionPerformed
        if ( useRegexFlag.isSelected() )
            {
                // This is just for a first time to give them a working example of a usable regex
            if ( regex0.getText().isEmpty()
                     && regex1.getText().isEmpty()
                     && regex2.getText().isEmpty()
                     && regex3.getText().isEmpty()
                     && regex4.getText().isEmpty()
                     && regex5.getText().isEmpty()
                        )
                {
                regex0.setText( "([^,]*([,]|\\Z)).*" );
                regex1.setText( "([^,]*([,]|\\Z)).*" );
                regex2.setText( "(?:Check[ ]#(\\d*)|([^,]*)([,]|\\Z)).*" );
                regex3.setText( "([^,]*([,]|\\Z)).*" );
                regex4.setText( "([^,]*([,]|\\Z)).*" );
                regex5.setText( "([^,]*([,]|\\Z)).*" );
                }
            regex0.setVisible( true );
            regex1.setVisible( true );
            regex2.setVisible( true );
            regex3.setVisible( true );
            regex4.setVisible( true );
            regex5.setVisible( true );
            regex6.setVisible( true );
            regex7.setVisible( true );
            regex8.setVisible( true );
            regex9.setVisible( true );
            }
        else
            {
//            regex0.setText( "" );
//            regex1.setText( "" );
//            regex2.setText( "" );
//            regex3.setText( "" );
//            regex4.setText( "" );
//            regex5.setText( "" );

            regex0.setVisible( false );
            regex1.setVisible( false );
            regex2.setVisible( false );
            regex3.setVisible( false );
            regex4.setVisible( false );
            regex5.setVisible( false );
            regex6.setVisible( false );
            regex7.setVisible( false );
            regex8.setVisible( false );
            regex9.setVisible( false );
            }
    }//GEN-LAST:event_useRegexFlagActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                CustomReaderDialog dialog = new CustomReaderDialog( null, true );
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
    private javax.swing.JButton addBtn;
    private javax.swing.JTextField amountCurrencyChar;
    private javax.swing.JTextField amountDecimalSignChar;
    private javax.swing.JTextField amountFormat;
    private javax.swing.JTextField amountGroupingSeparatorChar;
    private javax.swing.JList customReadersList;
    private javax.swing.JComboBox dataType0;
    private javax.swing.JComboBox dataType1;
    private javax.swing.JComboBox dataType2;
    private javax.swing.JComboBox dataType3;
    private javax.swing.JComboBox dataType4;
    private javax.swing.JComboBox dataType5;
    private javax.swing.JComboBox dataType6;
    private javax.swing.JComboBox dataType7;
    private javax.swing.JComboBox dataType8;
    private javax.swing.JComboBox dataType9;
    private javax.swing.JTextField dateFormatCr;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JButton doneBtn;
    private javax.swing.JTextField fieldSeparatorChar;
    private javax.swing.JComboBox fileEncodingCB;
    private javax.swing.JLabel fileEncodingLbl;
    private javax.swing.JTextField filenameMatcher;
    private javax.swing.JTextField footerLines;
    private javax.swing.JTextField headerLines;
    private javax.swing.JCheckBox importReverseOrderFlg;
    private javax.swing.JComboBox isNullable0;
    private javax.swing.JComboBox isNullable1;
    private javax.swing.JComboBox isNullable2;
    private javax.swing.JComboBox isNullable3;
    private javax.swing.JComboBox isNullable4;
    private javax.swing.JComboBox isNullable5;
    private javax.swing.JComboBox isNullable6;
    private javax.swing.JComboBox isNullable7;
    private javax.swing.JComboBox isNullable8;
    private javax.swing.JComboBox isNullable9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel message;
    private javax.swing.JTextField readerName;
    private javax.swing.JTextField regex0;
    private javax.swing.JTextField regex1;
    private javax.swing.JTextField regex2;
    private javax.swing.JTextField regex3;
    private javax.swing.JTextField regex4;
    private javax.swing.JTextField regex5;
    private javax.swing.JTextField regex6;
    private javax.swing.JTextField regex7;
    private javax.swing.JTextField regex8;
    private javax.swing.JTextField regex9;
    private javax.swing.JButton resetFieldsBtn;
    private javax.swing.JButton saveBtn;
    private javax.swing.JCheckBox useRegexFlag;
    // End of variables declaration//GEN-END:variables
}
