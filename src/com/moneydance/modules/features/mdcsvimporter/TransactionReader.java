/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
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

import com.moneydance.apps.md.model.TransactionSet;
import com.moneydance.apps.md.model.Account;
import com.moneydance.apps.md.model.RootAccount;
import com.moneydance.apps.md.model.CurrencyType;
import com.moneydance.apps.md.model.AbstractTxn;
import com.moneydance.apps.md.model.OnlineTxn;
import com.moneydance.apps.md.model.OnlineTxnList;
import com.moneydance.apps.md.model.ParentTxn;
import com.moneydance.apps.md.model.SplitTxn;

import com.moneydance.apps.md.model.TxnSet;
import com.moneydance.apps.md.view.gui.MoneydanceGUI;
import com.moneydance.apps.md.view.gui.OnlineManager;
import com.moneydance.modules.features.mdcsvimporter.formats.CustomReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author miki and Stan Towianski
 */
public abstract class TransactionReader
{
   private boolean customReaderFlag = false;
   private CustomReaderData customReaderData = null;
   
   protected static ImportDialog importDialog = null;
   protected static CustomReaderDialog customReaderDialog = null;
   protected static RootAccount rootAccount = null;

   protected CSVData csvData;
   protected Account account;
   protected String accountNameFromCSV;//for reading account from CSV file
   protected String priorAccountNameFromCSV = "";//for efficiency only
   protected OnlineTxnList transactionList;
   protected TransactionSet txnSet;
   protected CurrencyType currency;
   protected HashSet tsetMatcherKey = new HashSet<String>();
   protected HashSet tsetFITxnIdMatcherKey = new HashSet<String>();
   //protected HashSet onlineMatcherKey = new HashSet<String>();
   protected int defProtocolId = 999;  // per Sean at MD
   protected final static String DEFAULT_ENCODING = "UTF-8";  // "UTF-16LE"; "windows-1250"; Preselect this value in Encoding JComboBox.
   //protected String fileEncoding = DEFAULT_ENCODING;
   protected boolean isUsingCategorynameFlag = false;

   protected abstract boolean canParse( CSVData data );

   protected abstract boolean parseNext() throws IOException;

// protected abstract boolean parseNext(OnlineTxn txn) throws IOException;

   protected abstract boolean assignDataToTxn( OnlineTxn txn ) throws IOException;

   public abstract String getFormatName();

   public abstract String[] getSupportedDateFormats();
   
   public abstract void setSupportedDateFormats( String[] supportedDateFormats );

   public abstract String getDateFormat();

   public abstract void setDateFormat( String format );

   //public abstract int getFieldSeparator();

   //public abstract void setFieldSeparator( int xxx );

   protected final void throwException( String message )
      throws IOException
   {
      throw new IOException( message );
   }

   public static void init( CustomReaderDialog customReaderDialogArg, ImportDialog importDialogArg, RootAccount rootAccountArg )
    {
    customReaderDialog = customReaderDialogArg;
    importDialog = importDialogArg;
    rootAccount = rootAccountArg;
    }
   
   protected final void setRootAccount( RootAccount rootAccount )
   {
        this.rootAccount = rootAccount;
   }

   public final String calcFITxnIdAbstract( AbstractTxn atxn )
      throws IOException
       {
       //System.err.println(  "\n---------   entered TransactionReader().calcFITxnId( AbstractTxn )  -------------" );
            //System.err.println( "key.getDescription() =" + atxn.getDescription() + "=   atxn.getFiTxnId( 1 ) =" + atxn.getFiTxnId( 1 ) + "=" );
            //System.err.println( "atxn.getFiTxnId( 1 ) [" + k + "] =" + atxn.getFiTxnId( 1 ) + "=   atxn.getFiTxnId( 0 ) [" + k + "] =" + atxn.getFiTxnId( 0 ) + "=" );
            //tsetMatcherKey.add( atxn.getFiTxnId( 1 ) );
            
              // Here I am manually recreating the FiTxnId that I set on imported txn's because I could not figure
              // out how to simply read it.
              
            
            //String tmp = atxn.getDateInt() + ":" + currency.format( atxn.getValue(), '.' ) + ":" + atxn.getDescription() + ":" + atxn.getCheckNumber();

      // Create a pattern to match comments
 //dave Pattern ckNumPat = Pattern.compile("^.*\\\"chknum\\\" = \\\"(.+?)\\\"\n.*$", Pattern.MULTILINE);
        // I think I want to stick with (.*) instead of (.+) because I want to catch () either way. Stan
    Pattern ckNumPat = Pattern.compile( "^.*\\\"chknum\\\" = \\\"(.*?)\\\"\n.*$", Pattern.MULTILINE );
    Pattern amtPat = Pattern.compile( "^.*\\\"amt\\\" = \\\"(.*?)\\\"\n.*$", Pattern.MULTILINE );
    String amt = null;
    String origCheckNumber = null;
    String desc = null;
      
       /*
  <TAG>
   <KEY>ol.orig-txn</KEY>
   <VAL>{&#10;  "dtinit-int" = "20110824"&#10;  "name" = "whatever desc"&#10;  "amt" = "-9824"&#10;  "fitxnid" = "20110824:-98.24:whatever desc"&#10;  "dtpstd-int" = "20110824"&#10;  "dtavail-int" = "20110824"&#10;  "invst.totalamt" = "-9824"&#10;  "chknum" = "001234"&#10;  "ptype" = "1"&#10;}&#10;</VAL>
  </TAG>
              */
              
      String origtxn = atxn.getTag( "ol.orig-txn" );
      //String origCheckNumber = origtxn.replaceAll( ".*\\\"chknum\\\" = \\\"(.*?)\\\"\\\n.*", "$1" );

      //System.out.println( "\norigtxn ="+origtxn + "=" );

      // Run some matches
      if ( origtxn != null )
          {
          Matcher m = ckNumPat.matcher( origtxn );
          if ( m.find() )
                {
                origCheckNumber = m.group( 1 );
                //System.out.println("Found orig check num ="+m.group( 1 ) + "=" );
                }
          else
                {
                origCheckNumber = atxn.getCheckNumber();
                //System.out.println("have orig-txn but no check num so use getchecknum() ="+origCheckNumber + "=" );
                }
          
          m = amtPat.matcher( origtxn );
          if ( m.find() )
                {
                long lamt = Long.valueOf( m.group( 1 ) ).longValue();
                amt = currency.format( lamt, '.' );
                //System.out.println("Found orig amt ="+m.group( 1 ) + "= formatted =" + amt );
                }
          else
                {
                amt = currency.format( atxn.getValue(), '.' );
                //System.out.println("have orig-txn but no check num so use getchecknum() ="+origCheckNumber + "=" );
                }
          }
      else
          {
          origCheckNumber = atxn.getCheckNumber();
          //System.out.println("no orig check num so use getchecknum() ="+origCheckNumber + "=" );
          amt = currency.format( atxn.getValue(), '.' );
          }

        //long value = atxn.getParentTxn().getValue();
        
        if ( atxn.getTag( "ol.orig-payee" ) == null )
            {
            desc = atxn.getDescription();
            }
        else
            {
            desc = atxn.getTag( "ol.orig-payee" );
            }

        // This new way compare using the ORIGINAL payee and memo fields so if the user changes them, it will still match. Stan
        String tmp = atxn.getDateInt() + ":" + amt
                           + ":" + desc
                           + ":" + (origCheckNumber == null ? "" : origCheckNumber.replaceAll( "^0*(.*)", "$1" ) )    // strip leading 0's
                           + ":" + (atxn.getTag( "ol.orig-memo" ) == null ? "" : atxn.getTag( "ol.orig-memo" ));

        //System.err.println( "calc abstract FITxnld >" + tmp + "<" );
        return tmp;
       }


   public final String calcFITxnId( OnlineTxn onlinetxn )
      throws IOException
       {
       //System.err.println(  "\n---------   entered TransactionReader().calcFITxnId( onlinetxn )  -------------" );
       //      txn.setFITxnId( date + ":" + currency.format( amount, '.' ) + ":" + description + ":" + txn.getCheckNum() + ":" + txn.getMemo() );

        String tmp = onlinetxn.getDateInitiatedInt() + ":" + currency.format( onlinetxn.getAmount(), '.' )
                           + ":" + (onlinetxn.getName() == null ? "" : onlinetxn.getName() )    // used payeeName once
                           + ":" + (onlinetxn.getCheckNum() == null ? "" : onlinetxn.getCheckNum().replaceAll( "^0*(.*)", "$1" ) )    // strip leading 0's
                           + ":" + (onlinetxn.getMemo() == null ? "" : onlinetxn.getMemo() )
                                  ;            

       //System.err.println(  "calc online FITxnld >" + tmp + "<" );
       return tmp;
       }

      private final void makeSetOfExistingTxns( TxnSet tset )
                throws IOException 
        {
        int k = 0;
        for ( AbstractTxn atxn : tset )
            {
            String tmp = calcFITxnIdAbstract( atxn );

            //System.err.println( "tmp string [" + k + "] =" + tmp + "=" );
            tsetMatcherKey.add( tmp );
            tsetFITxnIdMatcherKey.add( atxn.getFiTxnId( OnlineTxn.PROTO_TYPE_OFX ) );
            tsetFITxnIdMatcherKey.add( atxn.getFiTxnId( defProtocolId ) );

            k++;
            //if ( k > 9 )
            //   break;
            }
        //System.err.println(  "\n---------   end: make set of existing account transactions  -------------" );
        }

      /*                
       ************************************************************************************************
       */
        public final void parse( Main main, CSVData csvDataArg, Account accountIn, RootAccount rootAccount )
                throws IOException 
        {
        System.err.println("\n---------   entered TransactionReader().parse()  -------------");

        this.csvData = csvDataArg;
        this.rootAccount = rootAccount;
        this.txnSet = rootAccount.getTransactionSet();
        this.tsetMatcherKey = new HashSet();
        this.tsetFITxnIdMatcherKey = new HashSet();

//      //begin testing
//      //this is part of what would be needed to match account names
//      //using regex or partial matching instead of exact and complete matching.
//      HashMap<String, Account> accountMap = new HashMap<String, Account>();
//      Enumeration accountListEnum = rootAccount.getSubAccounts();
//      while (accountListEnum.hasMoreElements()) {
//      Account a = (Account)accountListEnum.nextElement();
//      accountMap.put(a.getAccountName(), a);
//          }
//      //getAllAccountNames - is only path from root to present acct
//      //end testing

        System.err.println("\n---------   beg: make set of existing account transactions  -------------");
        //System.err.println(  "number of trans list =" +this.txnSet.getTransactionsForAccount( account ).getSize()  );
        System.err.println("size of txnSet.getAllTxns = " + this.txnSet.getAllTxns().getSize());
        // cannot get just for account because I am putting them into a temp/empty account !
        //Enumeration<AbstractTxn> tenums = this.txnSet.getTransactionsForAccount( account ).getAllTxns();
        TxnSet tset = this.txnSet.getAllTxns();

        //TODO: refactor this.
        //Currently, if the CSV file contains transactions from different accounts
        //with different currencies, we don't handle that. However, we could.
        //By separating the parsing from the matching, we could easily handle it.
        //For now, the account selected on the dialog will provide the currency
        //for all accounts.
        //Fixing this is a low priority because
        //	1) not everyone has multiple accounts in a single file
        //	2) most people with multiple accounts will have them in the same currency
        //If someone needs multiple currencies and accounts in one file,
        //it can be implemented as described above.
        this.currency = accountIn.getCurrencyType();
        //TODO: after refacting, call this only after each line of CSV file has been processed
        //TODO: parse CSV first, then iterate again to match FITxnId
        makeSetOfExistingTxns( tset );

      /*
     while ( tenums.hasMoreElements() ) 
            {
            AbstractTxn key = tenums.nextElement();
            System.err.println( "key.getDescription() =" + key.getDescription() + "=   key.getFiTxnId( 0 )" + key.getFiTxnId( 0 ) + "=" );
            tsetMatcherKey.add( key.getFiTxnId( 0 ) );
            }
       * 
       */

      /*   THIS DOES NOT SEEM TO HAVE ENTRIES SO i AM LEAVING IT OUT
      int max = transactionList.getTxnCount();
      for ( k = 0; k < max; k++ ) // OnlineTxn onlinetxn : transactionList )
          {
          OnlineTxn onlinetxn = transactionList.getTxn( k );
            String tmp = calcFITxnId( onlinetxn );
            
            //System.err.println( "tmp string [" + k + "] =" + tmp + "=" );
            onlineMatcherKey.add( tmp );
            
            //if ( k > 9 )
             //   break;
          }
      */
      System.err.println(  "\n---------   end: make set of existing account online transactions  -------------" );
      
      //csvData.reset();
        if ( this instanceof CustomReader )
            {
            csvData.parseIntoLines( customReaderData );
            }
        else
            {
            csvData.parseIntoLines( null );
            }

      //System.err.println( "at parse getFieldSeparator() =" + (char)csvData.getReader().getFieldSeparator() + "=" );
      //csvData.getReader().setFieldSeparator( customReaderDialog.getFieldSeparatorChar() );
      //System.err.println( "at parse getFieldSeparator() after set =" + (char)csvData.getReader().getFieldSeparator() + "=" );

        csvData.reset();
        long fileLineCount = 0;
        long endingBlankLines = 0;
        //----- Count File Lines to know where Footer starts  -----
        while ( csvData.nextLine() )
            {
            fileLineCount ++;
            if ( ! csvData.hasZeroFields() )
                {
                endingBlankLines ++;
                System.err.println(  "endingBlankLines =" + endingBlankLines );
                }
            else
                {
                endingBlankLines = 0;
                }
            }
        System.err.println(  "fileLineCount =" + fileLineCount );

        
      csvData.reset();
      //----- Skip Header Lines  -----
        System.err.println(  "getHeaderCount() =" + getHeaderCount() );
        for ( int hdrCnt = getHeaderCount(); hdrCnt > 0; --hdrCnt )
            {
            csvData.nextLine(); // skip the header
            System.err.println( "skip header" );
            }
      long begAtLine = csvData.getCurrentLineIndex() + 1;
      
      //testing
      com.moneydance.apps.md.controller.Main mainApp =
                        (com.moneydance.apps.md.controller.Main) main.getMainContext();
      OnlineManager onlineMgr = new OnlineManager( (MoneydanceGUI) mainApp.getUI() );

//		this.account = account;
//		this.transactionList = account.getDownloadedTxns();
      long totalProcessed = 0;
      long totalAccepted = 0;
      long totalRejected = 0;
      long totalDuplicates = 0;
      long stopAtLine = fileLineCount - getHeaderCount() - getCustomReaderData().getFooterLines() - endingBlankLines;
//		priorAccountNameFromCSV = "";
//		System.out.println("calling while (csvData.nextLine())...");
    boolean accountMissingError = false;

//    csvData.printFile();
        System.err.println( "ImportReverseOrderFlg(): " + getCustomReaderData().getImportReverseOrderFlg() );
        if ( getCustomReaderData().getImportReverseOrderFlg() )
            {
            csvData.reverseListRangeOrder( begAtLine, stopAtLine - 1 );
        //    csvData.printFile();
            }

      while ( csvData.nextLine() && totalProcessed < stopAtLine )
        {
        accountNameFromCSV = "";
        totalProcessed++;
        //			System.out.println("calling parseNext...");
        
        if ( parseNext() )
            {
            if ( null == accountNameFromCSV || accountNameFromCSV.isEmpty() )
                {
                System.out.println( "accountNameFromCSV is empty. Used selected acct." );
                this.account = accountIn;
                }
            else
                {
                this.account = rootAccount.getAccountByName( accountNameFromCSV );
                System.out.println( "accountNameFromCSV: " +  accountNameFromCSV );
                if ( this.account == null )
                    {
                    System.err.println( "ERROR: account is null" );
                    //TODO: make new account?
                    if ( ! accountMissingError )
                        {
                        JOptionPane.showMessageDialog(importDialog, "The account in the CSV file must \nalready exist in Money Dance. \nPlease create it first.");
                        }
                    accountMissingError = true;
                    totalRejected++;
                    continue;
                    }
                System.out.println( "account.getAccountName(): " + this.account.getAccountName() );
                }
          //TODO: per-account currency assignment is unfinished.
          //it requires separating parsing logic from matching logic. 2011.11.25 ds
          //this.currency = account.getCurrencyType();


        //  if (null != this.transactionList &&
        //  ! accountNameFromCSV.contentEquals(priorAccountNameFromCSV)) {
        //  priorAccountNameFromCSV = accountNameFromCSV;
          this.transactionList = account.getDownloadedTxns();//TODO: move this out of loop
          System.err.println( "tset.getSize() = " + tset.getSize() + "   online txns.getSize() = " + transactionList.getTxnCount() );

        //				}
          System.out.println("OnlineTxn txn = transactionList.newTxn();");
          OnlineTxn txn = transactionList.newTxn();
          assignDataToTxn( txn );
          txn.setProtocolType( OnlineTxn.PROTO_TYPE_OFX );

          /*
          if ( ! importDialog.isSelectedOnlineImportTypeRB() )
          {
          // Flip signs for regular txn's
          txn.setAmount( -txn.getAmount() );
          txn.setTotalAmount( -txn.getAmount() );
          }
            */
            System.err.println( "if (account.balanceIsNegated())" );
            if ( account.balanceIsNegated() )
                {
                txn.setAmount( -txn.getAmount() );
                txn.setTotalAmount( -txn.getAmount() );
                }
            
            //System.err.println( "call to calc fitxnid - should be online type" );
            String onlineMatchKey = calcFITxnId( txn );
            txn.setFITxnId( onlineMatchKey );
            
            // ! onlineMatcherKey.contains( onlineMatchKey )  &&
            if ( ! tsetMatcherKey.contains( onlineMatchKey )  &&
                 ! tsetFITxnIdMatcherKey.contains( onlineMatchKey )
                    )
                {
                System.err.println( "will add transaction with txn.getFITxnId( ) =" + txn.getFITxnId( ) + "=   txn.getFIID() =" + txn.getFIID() + "=" );
                //                     + "\n                              or onlineMatchKey =" + onlineMatchKey + "=" );
                //System.err.println( "importDialog =" + importDialog + "=" );
                
                /*  NOTE: This is to convert the online txn to an regular txn. This would let me set categories and tags 
                 * on incoming txn's,  but it automatically sets the category to the default account one and I like it
                 * better using the onlineTxn where it prompts the user to select a category for imported txn's. Stan
                 */
                if ( importDialog.isSelectedOnlineImportTypeRB() )
                    {
                    System.err.println( "add new onlineTxn" );
                    transactionList.addNewTxn( txn );
                    }
                else
                    {
                    System.err.println( "add new parentTxn/splitTxn" );
                    ParentTxn pTxn = onlineToParentTxn( account, rootAccount, txn );
                    if ( pTxn != null )
                        {
                        txnSet.addNewTxn( pTxn );
                        }
                    }
                totalAccepted ++;
                // I don't know why, but for now this works here, but not below, after the main loop - Stan. Maybe because of using multiple account names?
                System.err.println( "onlineMgr.processDownloadedTxns for account :" + account.getAccountName() );
                onlineMgr.processDownloadedTxns( account );
                }
            else
                {
                System.err.println( "will NOT add Duplicate transaction with txn.getFITxnId( ) =" + txn.getFITxnId( ) + "=" );
                totalDuplicates ++;
                }
              }  // parseNext()
          else
              {
              // need to fixxx that it counts blank lines which it skips, as rejecteds
              csvData.printCurrentLine();
              totalRejected++;
             }
         }  // end while()
      
      JOptionPane.showMessageDialog( importDialog, "Total Records Process: " + totalProcessed
                                                                            + "\nRecords Imported: " + totalAccepted
                                                                            + "\nDuplicates Skipped: " + totalDuplicates
                                                                            + "\nRejected Records: " + totalRejected
                                                        );
      
      
      /** NOTE: This is what I would like to do but I do not understand enough about this
       * transactionList and why you create a newTxn() and then later call addNewTxn().
       * newTxn seems to be some kind of 'service' as opposed to a regular object???   Stan
       */
      /*
      int ans = JOptionPane.showConfirmDialog( importDialog, "Total Records Processed: " + totalProcessed
                                                                            + "\nNew Records: " + totalAccepted
                                                                            + "\nDuplicate Records: " + totalDuplicates
                                                                            + "\nRejected Records: " + totalRejected
                                                                            + "\n\nDo you want to import the New records ?"
                                                                    , "Results", JOptionPane.YES_NO_OPTION );
      System.err.println( "ans =" + ans + "=    JOptionPane.YES_OPTION =" + JOptionPane.YES_OPTION + "=    JOptionPane.NO_OPTION =" + JOptionPane.NO_OPTION + "=" );
      if ( ans == JOptionPane.YES_OPTION )
          {
              
          OnlineTxnList transactionListCurrent = account.getDownloadedTxns();
          int max = transactionList.getTxnCount();
          System.err.println( "getTxnCount()/max =" + max + "=" );
          
          for ( int j = 0; j < max; j ++ )
              {
              System.err.println( "transactionList.getTxn( " + j + " ) =" + transactionList.getTxn( j ) + "=" );
              transactionListCurrent.addNewTxn( transactionList.getTxn( j ) );
              }
               
               
          JOptionPane.showMessageDialog( importDialog, totalAccepted + " records were added" );
          }
      else
          {
          JOptionPane.showMessageDialog( importDialog, "No records were added" );
          }
      */
      
      /*
      int max = transactionList.getTxnCount();
          
          for ( int j = 0; j < max; j ++ )
              {
              transactionList.removeTxn( j );
              }
       * 
       */
   }
   
   /*
    * Note: Create a ParentTxn from a filled out OnlineTxn
    */
 //  @ Override
   protected ParentTxn onlineToParentTxn( Account account, RootAccount rootAccount, OnlineTxn oTxn )
      throws IOException
   {
        Account category = null;

        String ckNum = oTxn.getCheckNum().replaceAll( "^0*(.*)", "$1" );

        // Don't know why I have to do this but I had to to make Online and Regular transactions use the same sign.
        // actually I noticed that the 'Type of Account' determines the sign of an amount also.
        // Bank accounts input as: Payment, Deposit
        // Charge accounts input as: Charge, Payment and are reversed sign !
        // I am not going to worry about that at this point. It doesn't really matter. Just define your reader to compensate.
        oTxn.setAmount( - oTxn.getAmount() );  
        oTxn.setTotalAmount( - oTxn.getTotalAmount() );  
        
        ParentTxn pTxn = new ParentTxn( oTxn.getDateInitiatedInt(), oTxn.getDateInitiatedInt(), oTxn.getDateInitiatedInt()
                                                          , ckNum, account, oTxn.getName(), oTxn.getMemo()
                                                          , -1, AbstractTxn.STATUS_UNRECONCILED );
       try {
           System.err.println( "find category for oTxn.getSubAccountTo() =" + oTxn.getSubAccountTo() + "=" );
           category = getAccount( account, oTxn.getSubAccountTo(), com.moneydance.apps.md.model.AccountUtil.getDefaultCategoryForAcct( account ).getAccountName()  //rr.getString("default_category"),
                                  , oTxn.getAmount() <= 0 ? Account.ACCOUNT_TYPE_EXPENSE : Account.ACCOUNT_TYPE_INCOME );
           System.err.println( "found category =" + category + "=" );

       } catch (Exception ex) {
           Logger.getLogger(TransactionReader.class.getName()).log(Level.SEVERE, null, ex);
           return null;   // skip this transaction - do not add
       }
        
        SplitTxn sptxn = new SplitTxn( pTxn, oTxn.getAmount(), oTxn.getAmount(), 1.0
                                        , category  //com.moneydance.apps.md.model.AccountUtil.getDefaultCategoryForAcct(account)  /* category */
                                        , pTxn.getDescription(), -1, AbstractTxn.STATUS_UNRECONCILED );
        
        sptxn.setIsNew( true );
        pTxn.addSplit( sptxn );
        
        pTxn.setIsNew( true );
        
        pTxn.setFiTxnId( defProtocolId, oTxn.getFITxnId( ) );
        sptxn.setFiTxnId( defProtocolId, oTxn.getFITxnId( ) );
        
        return pTxn;
    }
  
   public void setCustomReaderDialog( CustomReaderDialog customReaderDialog )
        {
        System.err.println( "custreader set custreaderdialog" );
        this.customReaderDialog = customReaderDialog;
        }
   
   public int getNumberOfCustomReaderFieldsUsed()
        {
        if ( this.customReaderDialog == null ) 
            return 0;
        else 
            return this.customReaderDialog.getNumberOfCustomReaderFieldsUsed();
        }
   
   public static TransactionReader[] getCompatibleReaders( boolean getAllReadersList, File selectedFile, ImportDialog importDialogArg, RootAccount rootAccount )
   {
      ArrayList<TransactionReader> formats = new ArrayList<TransactionReader>();
// moving      importDialog = importDialogArg;
      
      System.err.println( "getCompatibleReaders() call cust read canParse()" );
      CSVReader csvReader = null;
      
      for ( String key : Settings.getReaderHM().keySet() )
            {
            TransactionReader transactionReader = Settings.getReaderHM().get( key );
            System.err.println( "\n================  at canparse for transReader >" + key + "< ===============" );
            
             try
                {
                System.err.println( "using fileEncoding >" + transactionReader.getCustomReaderData().getFileEncoding() + "< ===============" );
                if ( transactionReader.getCustomReaderData().getUseRegexFlag() )
                    {
                    System.err.println( "\n================  Regex Reader" );
                    csvReader = new RegexReader( new InputStreamReader( new FileInputStream( selectedFile ), Charset.forName( transactionReader.getCustomReaderData().getFileEncoding() ) ), transactionReader.getCustomReaderData() );
                    }
                else
                    {
                    System.err.println( "\n================  Csv Reader" );
                    csvReader = new CSVReader( new InputStreamReader( new FileInputStream( selectedFile ), Charset.forName( transactionReader.getCustomReaderData().getFileEncoding() ) ), transactionReader.getCustomReaderData() );
                    }
                CSVData csvData = new CSVData( csvReader );
            
                transactionReader.setRootAccount( rootAccount );
                if ( getAllReadersList )
                      {
                      System.err.println( "=============== add all readers for >" + key + "< ===============" );
                      formats.add( transactionReader );
                      }
                else if ( transactionReader.canParse( csvData ) )
                      {
                      System.err.println( "=============== at canparse WORKS for >" + key + "< ===============" );
                      formats.add( transactionReader );
                      }
                else
                      {
                      System.err.println( "=============== at canparse NOT WORK for >" + key + "< ===============" );
                      }
                }
             catch ( Throwable x )
                 {
                 System.err.println( "at canparse error reading file !" );
                 System.err.println( "=============== at canparse NOT WORK for >" + key + "< ===============" );
                 System.err.println( "File Error: " );
                 x.printStackTrace();
                 }
             finally
                {
                try
                    {
                    csvReader.close();
                    }
                catch( Exception fex )
                    {
                        ;
                    }
                }
            }
      
      /*
      if ( customerReaderName != null && ! customerReaderName.equals( "" ) )
s        {

          System.err.println( "at canparse getFieldSeparator() =" + (char)data.getReader().getFieldSeparator() + "=" );

          //data.getReader().setFieldSeparator( customReaderDialog.getFieldSeparatorChar() );
          //System.err.println( "at canparse getFieldSeparator() after set =" + (char)data.getReader().getFieldSeparator() + "=" );

//s          System.err.println( "at canparse getFieldSeparator() after set =" + (char)data.getReader().getFieldSeparator() + "=" );

          customReader.setDateFormat( importDialog.comboDateFormatGetItem() );
          System.err.println( "at canparse importDialog.comboDateFormatGetItem() after set =" + importDialog.comboDateFormatGetItem() + "=" );
          
          if ( customReader.canParse( data ) )
              {
             formats.add( customReader );
            }
        }
      
      if ( citiBankCanadaReader.canParse( data ) )
      {
         formats.add( citiBankCanadaReader );
      }

      if ( ingNetherlandsReader.canParse( data ) )
      {
         formats.add( ingNetherlandsReader );
      }

      if ( simpleCreditDebitReader.canParse( data ) )
      {
         formats.add( simpleCreditDebitReader );
      }

      if ( wellsFargoReader.canParse( data ) )
      {
         formats.add( wellsFargoReader );
      }
       */
      
      TransactionReader[] retVal = new TransactionReader[formats.size()];
      formats.toArray( retVal );
      return retVal;
   }

   @Override
   public String toString()
   {
      return getFormatName();
   }

   //protected abstract boolean haveHeader();
   protected abstract int getHeaderCount();
   
   /*
   protected String convertParensToMinusSign( String amt )
   {
       return amt.replaceAll( "\(.*\)", "-\$1" );
   }
    */

    public boolean isCustomReaderFlag() {
        return customReaderFlag;
    }

    public void setCustomReaderFlag(boolean customReaderFlag) {
        this.customReaderFlag = customReaderFlag;
    }

    public CustomReaderData getCustomReaderData() {
        return customReaderData;
    }

    public void setCustomReaderData(CustomReaderData customReaderData) {
        this.customReaderData = customReaderData;
    }
    
    public boolean isUsingCategorynameFlag() {
        return isUsingCategorynameFlag;
    }
            
    public void setUsingCategorynameFlag(boolean xx) {
        this.isUsingCategorynameFlag = xx;
        //System.err.println( "set isUsingCategorynameFlag to =" + isUsingCategorynameFlag + "=" );
    }
    
    /**************************************************************************************/

  /** Find and return the ACCOUNT field in the appropriate format. */
  private final Account getAccount( Account account, String categoryName, String defaultAccount,
                                    int defaultAcctType )
    throws Exception
  {
    //String acctStr = getField(fieldValues, 1 /*ACCOUNT*/, null);
    if ( categoryName == null ) return addNewAccount( defaultAccount, account.getCurrencyType(),
                                           rootAccount, "", defaultAcctType, true, -1 );
    //acctStr = acctStr.trim();
    return addNewAccount( categoryName, account.getCurrencyType(), rootAccount, "",
                          defaultAcctType, true, -1 );
  }
  
  /**************************************************
   * Copied from Text File Importer code with permission from Reilly Technologies, L.L.C. 
   * I unfortunately do not understand what this code does.
  **************************************************/
  
  private Account addNewAccount( String accountName, CurrencyType currencyType,
                                Account parentAccount, String description,
                                int accountType, boolean lenientMatch,
                                int currAccountId)
    throws Exception
  {
    if(accountName.indexOf(':')==0 &&
       parentAccount.getAccountType()==Account.ACCOUNT_TYPE_ROOT) {
      accountName = accountName.substring(1);
    }

    int colIndex = accountName.indexOf(':');
    String restOfAcctName;
    String thisAcctName;
    if(colIndex>=0) {
      restOfAcctName = accountName.substring(colIndex+1);
      thisAcctName = accountName.substring(0,colIndex);
    } else {
      restOfAcctName = null;
      thisAcctName = accountName;
    }

    Account newAccount = null;
    for(int i=0; i<parentAccount.getSubAccountCount(); i++) {
      Account subAcct = parentAccount.getSubAccount(i);
      if((lenientMatch && subAcct.getAccountName().equalsIgnoreCase(thisAcctName)) ||
         (subAcct.getAccountType()==accountType &&
          subAcct.getAccountName().equalsIgnoreCase(thisAcctName))) {
        newAccount = subAcct;
        break;
      }
    }

    if(newAccount==null) {
      newAccount = 
        Account.makeAccount(accountType, thisAcctName, currencyType, parentAccount);
      if(newAccount instanceof com.moneydance.apps.md.model.BankAccount) {
        ((com.moneydance.apps.md.model.BankAccount)newAccount).setBankName(description);
      } else if(newAccount instanceof com.moneydance.apps.md.model.InvestmentAccount) {
        (( com.moneydance.apps.md.model.InvestmentAccount)newAccount).setAccountDescription(description);
      }
      parentAccount.addSubAccount(newAccount);
    }

    if(restOfAcctName!=null) {
      return addNewAccount(restOfAcctName, currencyType, newAccount,
                           description, accountType,lenientMatch,
                           currAccountId);
    } else {
      if(newAccount.getAccountNum()==currAccountId) {
        // if the found account is the same as the container account
        // create another account with the same name and return it
        if(accountType==Account.ACCOUNT_TYPE_BANK &&
           parentAccount==rootAccount) {
          newAccount = Account.makeAccount(Account.ACCOUNT_TYPE_INCOME, 
                                           thisAcctName+"X", currencyType, 
                                           parentAccount);
        } else {
          newAccount = Account.makeAccount(accountType, thisAcctName+"X", 
                                           currencyType, parentAccount);
        }
        if(newAccount instanceof com.moneydance.apps.md.model.BankAccount) {
          (( com.moneydance.apps.md.model.BankAccount)newAccount).setBankName(description);
        }
        parentAccount.addSubAccount(newAccount);
      }
      return newAccount;
    }
  }  
    
}
