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
import com.moneydance.modules.features.mdcsvimporter.TransactionReader;
import com.moneydance.util.CustomDateFormat;
import com.moneydance.util.StringUtils;
import java.io.IOException;

/*
 *
 * Status	(e.g., posted)
 * Date	 (YYYY-MM-DD)
 * Original Description	(Payee)
 * Split Type
 * Category
 * Currency
 * Amount
 * User Description
 * Memo
 * Classification	(Tag)
 * Account Name

 */
public class YodleeReader
		extends TransactionReader {

	private static final String DATE_FORMAT_US = "MM/DD/YYYY";
	private static final String DATE_FORMAT_EU = "DD/MM/YY";
	private static final String DATE_FORMAT_JP = "YY/MM/DD";
	private static final String DATE_FORMAT_INTN = "YYYY-MM-DD";
	private String dateFormatStringSelected = DATE_FORMAT_INTN;
	private static String[] SUPPORTED_DATE_FORMATS = {
		DATE_FORMAT_US, DATE_FORMAT_EU, DATE_FORMAT_JP, DATE_FORMAT_INTN
	};
	private CustomDateFormat dateFormat = new CustomDateFormat(DATE_FORMAT_INTN);
	private String dateFormatString;
	private String status;
	private String description;
	private String splitType;
	private String category;
	private String currencyString;
	private String amountString;
	private long amount;
	private String userDescription;
	private String memo;
	private String classification;
	private int date;
	private static final String STATUS = "status";
	private static final String DATE = "date";
	private static final String DESCRIPTION = "original description";
	private static final String SPLIT = "split type";
	private static final String CATEGORY = "category";
	private static final String CURRENCY = "currency";
	private static final String AMOUNT = "amount";
	private static final String USER_DESCRIPTION = "user description";
	private static final String MEMO = "memo";
	private static final String CLASSIFICATION = "classification";
	private static final String ACCOUNT = "account name";

	@Override
	public boolean canParse(CSVData data) {
		try {
			data.parseIntoLines(0);
		} catch (IOException ex) {
			return false;
		}

		boolean retVal = data.nextLine()
				&& data.nextField() && STATUS.equals(data.getField().toLowerCase())
				&& data.nextField() && DATE.equals(data.getField().toLowerCase())
				&& data.nextField() && DESCRIPTION.equals(data.getField().toLowerCase())
				&& data.nextField() && SPLIT.equals(data.getField().toLowerCase())
				&& data.nextField() && CATEGORY.equals(data.getField().toLowerCase())
				&& data.nextField() && CURRENCY.equals(data.getField().toLowerCase())
				&& data.nextField() && AMOUNT.equals(data.getField().toLowerCase())
				&& data.nextField() && USER_DESCRIPTION.equals(data.getField().toLowerCase())
				&& data.nextField() && MEMO.equals(data.getField().toLowerCase())
				&& data.nextField() && CLASSIFICATION.equals(data.getField().toLowerCase())
				&& data.nextField() && ACCOUNT.equals(data.getField().toLowerCase())
				&& !data.nextField();

		System.out.println( "can parse Yodlee format =" + String.valueOf(retVal) + "=" );
		return retVal;
	}

	@Override
	public String getFormatName() {
		return "Yodlee";
	}

	/*
	 *
	 * Status	(e.g., posted)
	 * Date	 (YYYY-MM-DD)
	 * Original Description	(Payee)
	 * Split Type
	 * Category
	 * Currency
	 * Amount
	 * User Description
	 * Memo
	 * Classification	(Tag)
	 * Account Name
	 *
	 */
	@Override
	protected boolean parseNext() throws IOException {

		csvData.nextField();
		status = csvData.getField();
//		System.out.println("status: " + status);

//		System.out.println("getting Yodlee dateString...");
		csvData.nextField();
		String dateString = csvData.getField();
		if (dateString == null || dateString.isEmpty()) {
			// skip lines without valid dates (or empty)
			return false;
		}
//		System.out.println("dateString: " + dateString);

		csvData.nextField();
		description = csvData.getField();
//		System.out.println("description: " + description);

		csvData.nextField();
		splitType = csvData.getField();
//		System.out.println("splitType: " + splitType);

		csvData.nextField();
		category = csvData.getField();//NOTE: don't set in Txn object!
//		System.out.println("category: " + category);

		csvData.nextField();
		currencyString = csvData.getField();//NOTE: not used
//		System.out.println("currencyString: " + currencyString);

		csvData.nextField();
		amountString = csvData.getField().trim();
//		System.out.println("amountString: " + amountString);

		csvData.nextField();
		userDescription = csvData.getField();
//		System.out.println("userDescription: " + userDescription);

		csvData.nextField();
		memo = csvData.getField();
//		System.out.println("memo: " + memo);

		csvData.nextField();
		classification = csvData.getField();
//		System.out.println("classification: " + classification);

		csvData.nextField();
		this.accountNameFromCSV = csvData.getField();
//		System.out.println("accountNameFromCSV: " + accountNameFromCSV);

//		System.out.println( "parsing Yodlee amount...");
//		amount = 0;
//		try {
//			double amountDouble;
//			amountDouble = StringUtils.parseDoubleWithException(amountString, '.');
//			System.out.println( "after parseDoubleWithException...");
//			amount = currency.getLongValue(amountDouble);
//		} catch (Exception x) {
//			throwException("Invalid amount.");
//		}
//		System.out.println( "parsing Yodlee date...");
		date = dateFormat.parseInt(dateString);

//		System.out.println( "parsed Yodlee txn on " + dateString + " for " + accountNameFromCSV);
		return true;
	}

	/*
	 *
	 * Status	(e.g., posted)
	 * Date	 (YYYY-MM-DD)
	 * Original Description	(Payee)
	 * Split Type
	x Category
	x Currency
	 * Amount
	 * User Description
	 * Memo
	x Classification	(Tag)
	^ Account Name
	 *
	 */
	@Override
	protected boolean assignDataToTxn(OnlineTxn txn) throws IOException {
		amount = 0;
		try {
			double amountDouble;
			amountDouble = StringUtils.parseDoubleWithException(amountString, '.');
			System.out.println( "after parseDoubleWithException...");
			amount = currency.getLongValue(amountDouble);
		} catch (Exception x) {
			throwException("Invalid amount.");
		}
		txn.setAmount(amount);
		txn.setTotalAmount(amount);
		txn.setPayeeName(description);
		txn.setFITxnId(date + ":" + currency.format(amount, '.') + ":" + description);
		txn.setDatePostedInt(date);
		txn.setDateInitiatedInt(date);
		txn.setDateAvailableInt(date);
		txn.setMemo(memo);
		txn.setRefNum(status + ":" + splitType + ":" + userDescription);
		//TODO: classification (e.g., business or personal)
		return true;
	}

	@Override
	public String[] getSupportedDateFormats() {
		return SUPPORTED_DATE_FORMATS;
	}

	@Override
	public void setSupportedDateFormats(String[] supportedDateFormats) {
		SUPPORTED_DATE_FORMATS = supportedDateFormats;
	}

	public void createSupportedDateFormats(String dateFormatArg) {
		System.err.println("\n---------   entered createSupportedDateFormats() dateFormatArg =" + dateFormatArg + "=  -------------");
		String[] tmp = new String[1];
		tmp[0] = dateFormatArg;
		SUPPORTED_DATE_FORMATS = tmp;
		setDateFormat(dateFormatArg);
	}

	@Override
	public String getDateFormat() {
		return DATE_FORMAT_INTN;
	}

//	@Override
//	public void setDateFormat(String format) {
//		if (!DATE_FORMAT.equals(format)) {
//			throw new UnsupportedOperationException("Not supported yet.");
//		}
//	}
	@Override
	public void setDateFormat(String format) {
		if (format == null) {
			return;
		}

		System.err.println("setDateFormat() format =" + format + "=   dateFormatString =" + dateFormatString + "=");
		if (!format.equals(dateFormatStringSelected)) {
			dateFormat = new CustomDateFormat(format);
			dateFormatStringSelected = format;
		}

	}

	@Override
	protected int getHeaderCount() {
		return 1;
	}
}
