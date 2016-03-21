package com.moneydance.modules.features.mdcsvimporter;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author stan
 */


public class PreviewImportTblModel extends AbstractTableModel 
{
    private ArrayList<String>colNames;
    private String[][] data;
    private int colCount;    
    
    public PreviewImportTblModel( ArrayList<String> colNamesArg, String[][] dataArg, int colCountArg )
        {
        colNames = colNamesArg;
        data = dataArg;
        //headerCount = headerCountArg;
        colCount = colCountArg;
        
        System.err.println( "row count =" + data.length );
        System.err.println( "col count =" + colCount );
        }

    public int getColumnCount() { return colCount; }
    public int getRowCount() { return data.length;}
    public Object getValueAt(int row, int col) 
        {
        //System.err.println( "getValueAt row =" + row + "  col =" + col );
        try {
            if ( data[row][col] == "" ) 
                {
                //System.err.println( "NOT EXISTS getValueAt row =" + row + "  col =" + col );
                }
            } 
        catch( Exception ex )
            {
            return "";
            }
        return data[row][col]; 
        }
    
    public String getColumnName(int col) {
        return colNames.get( col );
    }
    
    /*
    public int getColumnCount() { return 10; }
    public int getRowCount() { return 10;}
    public Object getValueAt(int row, int col) { return new Integer(row*col); }
    */
}
