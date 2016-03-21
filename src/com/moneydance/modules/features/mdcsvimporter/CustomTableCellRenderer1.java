package com.moneydance.modules.features.mdcsvimporter;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author stan
 */


public class CustomTableCellRenderer1 extends JLabel implements TableCellRenderer {
    
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
          System.err.println( "ADD ERROR YELLOW Cell " + row + ", " + col );
      }
      rowSet.add( col );
      toolTip.put( row + "," + col, cellToolTip );
  }
  
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) 
    {
    if ( errCells.containsKey( row ) )
        {
        rowSet = errCells.get( row );
        if ( rowSet.contains( col ) )
            {
            setBackground( Color.YELLOW );
            setToolTipText( row + "," + col + toolTip.get( row + "," + col ) );
            return this;
            }
        }

    //lbl.setBackground( javax.swing.UIManager.getColor( "Table.dropCellBackground" ) );
    setBackground( Color.WHITE );
    //Return the JLabel which renders the cell.
    return this;    
    }
}
