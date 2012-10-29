package ru.curs.showcase.core.grid;

import org.w3c.dom.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Команда получения метаданных для LiveGrid.
 * 
 */
public class LiveGridMetadataGetCommand extends DataPanelElementCommand<LiveGridMetadata> {

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public LiveGridMetadataGetCommand(final GridContext aContext,
			final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);

	}

	@InputParam
	@Override
	public GridContext getContext() {
		return (GridContext) super.getContext();
	}

	/**
	 * @see ru.curs.showcase.core.command.ServiceLayerCommand#mainProc()
	 **/
	@Override
	protected void mainProc() throws Exception {
		includeDataPanelWidthAndHeightInSessionContext();
		GridGetCommand command = new GridGetCommand(getContext(), getElementInfo(), true);
		Grid grid = command.execute();
		setResult(GridTransformer.gridToLiveGridMetadata(grid));
	}

	private void includeDataPanelWidthAndHeightInSessionContext() throws Exception {

		Document doc = XMLUtils.stringToDocument(getContext().getSession());

		Element node = doc.createElement("currentDatapanelWidth");
		doc.getDocumentElement().appendChild(node);
		node.appendChild(doc.createTextNode(getContext().getCurrentDatapanelWidth().toString()));

		node = doc.createElement("currentDatapanelHeight");
		doc.getDocumentElement().appendChild(node);
		node.appendChild(doc.createTextNode(getContext().getCurrentDatapanelHeight().toString()));

		String result = XMLUtils.documentToString(doc);
		getContext().setSession(result);
	}

}
