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
package com.moneydance.modules.features.mdcsvimporter.formats;

import com.moneydance.apps.md.model.OnlineTxn;
import com.moneydance.modules.features.mdcsvimporter.CSVData;
import com.moneydance.modules.features.mdcsvimporter.DateGuesser;
import com.moneydance.modules.features.mdcsvimporter.TransactionReader;
import com.moneydance.util.CustomDateFormat;
import com.moneydance.util.StringUtils;
import java.io.IOException;

/**
 *
 * @author miki
 */
public class BbvaCompassBankReader
		extends TransactionReader {

	private static final String DATE = "date";
	private static final String DESCRIPTION = "description";
	private static final String CHECKNUM = "check #";
	private static final String CREDIT = "credit (+)";
	private static final String DEBIT = "debit (-)";
	private CustomDateFormat dateFormat;
	private String[] compatibleDateFormats;
	private String dateFormatString;
	private String dateString;
	private String description;
	private String checknum;
	private String debit;
	private String credit;
	private static final int headerRows = 5;

	@Override
	public boolean canParse(CSVData data) {
//		System.out.println("enter BbvaCompassBankReader.canParse");
		try {
			data.parseIntoLines(0);
		} catch (IOException ex) {
			//Logger.getLogger(CustomReader.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}

		for (int i = 0; i < headerRows -1; i++) {
			data.nextLine();
			if (null == data) {
				continue;
			}
			//this next statement raises an exception - don't use it
			//System.out.println("BbvaReader:skip: " + data.printCurrentLine());
		}

		boolean retVal = data.nextLine()
				&& data.nextField() && DATE.equalsIgnoreCase(data.getField())
				&& data.nextField() && DESCRIPTION.equalsIgnoreCase(data.getField())
				&& data.nextField() && CHECKNUM.equalsIgnoreCase(data.getField())
				&& data.nextField() && DEBIT.equalsIgnoreCase(data.getField())
				&& data.nextField() && CREDIT.equalsIgnoreCase(data.getField())
				&& !data.nextField();

		// find guessable date formats
		if (retVal) {
			DateGuesser guesser = new DateGuesser();
			while (data.nextLine()) {
				if (data.nextField()) {
					guesser.checkDateString(data.getField());
				}
			}

			compatibleDateFormats = guesser.getPossibleFormats();
			if (dateFormatString == null
					|| !find(compatibleDateFormats, dateFormatString)) {
				setDateFormat(guesser.getBestFormat());
			}
		}
		System.out.println("BbvaCompassBankReader.dateFormat = " + getDateFormat());
		System.out.println("BbvaCompassBankReader.canParse = " + String.valueOf(retVal));
		return retVal;
	}

	@Override
	public String getFormatName() {
		return "BBVA Compass Bank NA";
	}

	@Override
	protected boolean parseNext() throws IOException {
		System.out.println("enter BbvaCompassBankReader parseNext");
		csvData.nextField();
		dateString = csvData.getField();
		if (dateString == null || dateString.length() == 0) { // empty line
			return false;
		}

		csvData.nextField();
		description = csvData.getField();

		csvData.nextField();
		checknum = csvData.getField();

		csvData.nextField();
		debit = csvData.getField();
		
		csvData.nextField();
		credit = csvData.getField();

		if (credit == null && debit == null) {
			System.out.println("BbvaCompassBankReader Invalid line-debit and credit are null.");
			throwException("Invalid line");
		}

		if (credit.length() == 0 && debit.length() == 0) {
			System.out.println("BbvaCompassBankReader Invalid line-debit and credit are empty.");
			throwException("Credit and debit fields are both empty.");
		}
		System.out.println("exit BbvaCompassBankReader parseNext");
		return true;
	}

	@Override
	protected boolean assignDataToTxn(OnlineTxn txn) throws IOException {
		System.out.println("enter BbvaCompassBankReader assignDataToTxn");
		long amount = 0;
		try {
			double amountDouble;
			if (credit.length() > 0) {
				System.out.println("credit = " + String.valueOf(credit));
				amountDouble = StringUtils.parseDoubleWithException(credit, '.');
			} else {
				System.out.println("credit.length() <= 0");
				amountDouble = -StringUtils.parseDoubleWithException(debit, '.');
			}
			System.out.println("amountDouble = " + String.valueOf(amountDouble));
			amount = currency.getLongValue(amountDouble);
		} catch (Exception x) {
			throwException("Invalid amount.");
		}

		int date = dateFormat.parseInt(dateString);

		txn.setAmount(amount);
		txn.setTotalAmount(amount);
		txn.setPayeeName(description);
		txn.setMemo(description);
		txn.setCheckNum(checknum);
		// MOVED to TransactionReader so everyone creates it the same way.
		//txn.setFITxnId(date + ":" + currency.format(amount, '.') + ":" + description);
		//for temp testing. TODO: remove next line after testing. 2011.11.25 ds
		txn.setRefNum(date + ":" + currency.format(amount, '.') + ":" + description);
		txn.setDatePostedInt(date);
		txn.setDateInitiatedInt(date);
		txn.setDateAvailableInt(date);
		System.out.println("exit BbvaCompassBankReader assignDataToTxn");
		return true;
	}

	@Override
	public String[] getSupportedDateFormats() {
		return compatibleDateFormats;
	}

	@Override
	public void setSupportedDateFormats(String[] supportedDateFormats) {
		compatibleDateFormats = supportedDateFormats;
	}

	@Override
	public String getDateFormat() {
		return dateFormatString;
	}

	@Override
	public void setDateFormat(String format) {
		if (format == null) {
			return;
		}

		if (!format.equals(dateFormatString)) {
			dateFormat = new CustomDateFormat(format);
			dateFormatString = format;
		}
	}

	private static boolean find(String[] compatibleDateFormats, String dateFormatString) {
		if (dateFormatString == null) {
			return false;
		}

		for (String s : compatibleDateFormats) {
			if (dateFormatString.equals(dateFormatString)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected int getHeaderCount() {
		return headerRows;
	}
}
