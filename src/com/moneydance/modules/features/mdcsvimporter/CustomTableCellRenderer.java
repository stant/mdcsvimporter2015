package com.moneydance.modules.features.mdcsvimporter;

import java.awt.Color;
import java.awt.Component;
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
  
  public void setForRowCol( int row, int col )
  {
      forRow = row;
      forCol = col;
  }
  
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) 
    {
    JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

    //Get the status for the current row.
    PreviewImportTblModel tableModel = (PreviewImportTblModel) table.getModel();
    if ( (row == forRow || forRow < 0) && (col == forCol || forCol < 0) )
        {
        lbl.setBackground( Color.RED );
        }
    else
        {
        //lbl.setBackground( javax.swing.UIManager.getColor( "Table.dropCellBackground" ) );
        lbl.setBackground( Color.WHITE );
        }
    //Return the JLabel which renders the cell.
    return lbl;    
    }
}
