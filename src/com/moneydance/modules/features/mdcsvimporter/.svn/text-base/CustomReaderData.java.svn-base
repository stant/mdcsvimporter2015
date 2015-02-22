package com.moneydance.modules.features.mdcsvimporter;

import java.util.ArrayList;

/**
 *
 * @author Stan Towianski
 */


public class CustomReaderData {

//    ArrayList<javax.swing.JComboBox> dataTypesList = new ArrayList<javax.swing.JComboBox>( 10 );
//    ArrayList<javax.swing.JComboBox> emptyFlagsList = new ArrayList<javax.swing.JComboBox>( 10 );
    ArrayList<String> regexsList = new ArrayList<String>( 10 );
    ArrayList<String> dataTypesList = new ArrayList<String>( 10 );
    ArrayList<String> emptyFlagsList = new ArrayList<String>( 10 );
    //ArrayList<String> dateFormatList = new ArrayList<String>( 10 );
    int fieldSeparatorChar = ',';
    int headerLines = 0;
    int footerLines = 0;
    int amountCurrencyChar = '$';
    int amountDecimalSignChar = '.';
    int amountGroupingSeparatorChar = ',';
    String amountFormat = "#,###,###,##0.00;(#)";
    boolean importReverseOrderFlg = false;
    boolean useRegexFlag = false;
    String readerName = "";
    String dateFormatString = "MM/DD/YY";
    String fileEncoding = TransactionReader.DEFAULT_ENCODING;
    String filenameMatcher = ".*\\.[Cc][Ss][Vv]";
    
    
    public ArrayList<String> getRegexsList() {
        return regexsList;
    }

    public String getRegexsListEncoded() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append( "[" );
        for( String str : regexsList )
        {
            sbuf.append( str );
            sbuf.append( "a" );
        }
        sbuf.append( "]" );
        return sbuf.toString();
    }

    public void setRegexsList(ArrayList<String> regexsList) {
        this.regexsList = regexsList;
    }

    public String getRegexsListEle( int c ) {
        return regexsList.get( c );
    }

    public void setRegexsListEle( int c, String regex) {
        this.regexsList.set( c, regex );
    }

    public ArrayList<String> getDataTypesList() {
        return dataTypesList;
    }

    public void setDataTypesList(ArrayList<String> dataTypesList) {
        this.dataTypesList = dataTypesList;
    }

    public ArrayList<String> getEmptyFlagsList() {
        return emptyFlagsList;
    }

    public void setEmptyFlagsList(ArrayList<String> emptyFlagsList) {
        this.emptyFlagsList = emptyFlagsList;
    }

    public int getFieldSeparatorChar() {
        return fieldSeparatorChar;
    }

    public void setFieldSeparatorChar(int fieldSeparatorChar) {
        this.fieldSeparatorChar = fieldSeparatorChar;
    }

    public int getHeaderLines() {
        return headerLines;
    }

    public void setHeaderLines(int headerLines) {
        this.headerLines = headerLines;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getDateFormatString() {
        return dateFormatString;
    }

    public void setDateFormatString(String dateFormatString) {
        this.dateFormatString = dateFormatString;
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

    public int getFooterLines() {
        return footerLines;
    }

    public void setFooterLines(int footerLines) {
        this.footerLines = footerLines;
    }

    public int getAmountCurrencyChar() {
        return amountCurrencyChar;
    }

    public void setAmountCurrencyChar(int amountCurrencyChar) {
        this.amountCurrencyChar = amountCurrencyChar;
    }

    public int getAmountDecimalSignChar() {
        return amountDecimalSignChar;
    }

    public void setAmountDecimalSignChar(int amountDecimalSignChar) {
        this.amountDecimalSignChar = amountDecimalSignChar;
    }

    public String getAmountFormat() {
        return amountFormat;
    }

    public void setAmountFormat(String amountFormat) {
        this.amountFormat = amountFormat;
    }

    public int getAmountGroupingSeparatorChar() {
        return amountGroupingSeparatorChar;
    }

    public void setAmountGroupingSeparatorChar(int amountGroupingSeparatorChar) {
        this.amountGroupingSeparatorChar = amountGroupingSeparatorChar;
    }

    public boolean getImportReverseOrderFlg() {
        return importReverseOrderFlg;
    }

    public void setImportReverseOrderFlg(boolean importReverseOrderFlg) {
        this.importReverseOrderFlg = importReverseOrderFlg;
    }

    public boolean getUseRegexFlag() {
        return useRegexFlag;
    }

    public void setUseRegexFlag(boolean useRegexFlag) {
        this.useRegexFlag = useRegexFlag;
    }

    public String getFileEncoding() {
        return fileEncoding;
    }

    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    public String getFilenameMatcher() {
        return filenameMatcher;
    }

    public void setFilenameMatcher(String filenameMatcher) {
        this.filenameMatcher = filenameMatcher;
    }
  
}
