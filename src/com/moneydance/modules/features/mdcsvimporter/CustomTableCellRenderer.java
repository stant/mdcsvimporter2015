package com.moneydance.modules.features.mdcsvimporter;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author stan
 */


public class CustomTableCellRenderer extends DefaultTableCellRenderer {
    
  int forRow = -1;
  int forCol = -1;
  HashMap<Integer, HashSet> errCells = new HashMap<Integer, HashSet>();
  HashSet rowSet = new HashSet();
  HashMap<String,String> toolTip = new HashMap<String,String>();
  
  public void setForRowCol( int row, int col, String cellToolTip )
  {
      forRow = row;
      forCol = col;
      if ( errCells.containsKey( row ) )
        {
        rowSet = errCells.get( row );
        }
      else
        {
        rowSet = new HashSet();
        errCells.put( row, rowSet );
        Util.logConsole( "ERROR RED Cell " + row + ", " + col );
        }
      rowSet.add( col );
      toolTip.put( row + "," + col, cellToolTip );
  }
  
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) 
    {
    JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

    if ( table.isRowSelected( row ) )
        {
        setForeground( Color.BLACK );
        }
    
    if ( errCells.containsKey( row ) )
        {
        rowSet = errCells.get( row );
        if ( rowSet.contains( col ) )
            {
            lbl.setBackground( Color.YELLOW );
            lbl.setToolTipText( toolTip.get( row + "," + col ) );
            return lbl;
            }
        }

    //lbl.setBackground( javax.swing.UIManager.getColor( "Table.dropCellBackground" ) );
    lbl.setBackground( Color.WHITE );
    lbl.setToolTipText( null );
    //Return the JLabel which renders the cell.
    return lbl;    
    }
}
