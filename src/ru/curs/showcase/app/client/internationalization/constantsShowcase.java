/**
 * 
 */
package ru.curs.showcase.app.client.internationalization;

import com.google.gwt.i18n.client.Messages;

/**
 * @author a.lugovtsov
 * 
 */

public interface constantsShowcase extends Messages {

	@DefaultMessage("sendButtonText")
	String sendButtonText();

	@DefaultMessage("Cancel")
	String selectorCancelText();

	@DefaultMessage("Starts with (Ctrl+B)")
	String selectorStartsWithText();

	@DefaultMessage("Not Found")
	String multySelectorStringNotFound();

	@DefaultMessage("when receiving data from the server to the table")
	String gridErrorGetTable();

	@DefaultMessage("Export to Excel")
	String gridExportToExcelCaption();

	@DefaultMessage("The table is empty. Export to Excel will not run.")
	String gridExportToExcelEmptyTable();

	@DefaultMessage("Loading data")
	String treeGridLoadingData();

	@DefaultMessage("Downloadable entry with ID")
	String treeGridLoadingDataDuplicateRecord1();

	@DefaultMessage("already present in the grid. Entries are not loaded.")
	String treeGridLoadingDataDuplicateRecord2();

}
