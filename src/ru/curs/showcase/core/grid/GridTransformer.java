package ru.curs.showcase.core.grid;

import java.io.IOException;
import java.text.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.grid.*;

import com.extjs.gxt.ui.client.data.*;

/**
 * Класс, преобразующий Grid в ExtGrid.
 * 
 */
public final class GridTransformer {
	private GridTransformer() {
		throw new UnsupportedOperationException();
	}

	private static List<ExtGridData> posts;

	public static ExtGridMetadata gridToExtGridMetadata(final Grid grid) {

		ExtGridMetadata egm = new ExtGridMetadata();

		egm.setHeader(grid.getHeader());
		egm.setFooter(grid.getFooter());

		return egm;
	}

	public static PagingLoadResult<ExtGridData> gridToExtGridData(final Grid grid,
			final PagingLoadConfig loadConfig) {

		if (posts == null) {
			loadPosts();
		}

		if (loadConfig.getSortInfo().getSortField() != null) {
			final String sortField = loadConfig.getSortInfo().getSortField();
			if (sortField != null) {
				Collections.sort(
						posts,
						loadConfig.getSortInfo().getSortDir()
								.comparator(new Comparator<ExtGridData>() {
									@Override
									public int compare(final ExtGridData p1, final ExtGridData p2) {
										if ("forum".equals(sortField)) {
											return p1.getForum().compareTo(p2.getForum());
										} else if ("username".equals(sortField)) {
											return p1.getUsername().compareTo(p2.getUsername());
										} else if ("subject".equals(sortField)) {
											return p1.getSubject().compareTo(p2.getSubject());
										} else if ("date".equals(sortField)) {
											return p1.getDate().compareTo(p2.getDate());
										}
										return 0;
									}
								}));
			}
		}

		ArrayList<ExtGridData> sublist = new ArrayList<ExtGridData>();
		int start = loadConfig.getOffset();
		int limit = posts.size();
		if (loadConfig.getLimit() > 0) {
			limit = Math.min(start + loadConfig.getLimit(), limit);
		}
		for (int i = loadConfig.getOffset(); i < limit; i++) {
			sublist.add(posts.get(i));
		}
		return new BasePagingLoadResult<ExtGridData>(sublist, loadConfig.getOffset(), posts.size());

	}

	private static void loadPosts() {
		posts = new ArrayList<ExtGridData>();

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;

			db = dbf.newDocumentBuilder();
			Document doc =
				db.parse(Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("../../resources/extgwt/posts.xml"));
			doc.getDocumentElement().normalize();

			NodeList nodeList = doc.getElementsByTagName("row");

			for (int s = 0; s < nodeList.getLength(); s++) {
				Node fstNode = nodeList.item(s);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					Element fstElmnt = (Element) fstNode;
					NodeList fields = fstElmnt.getElementsByTagName("field");
					ExtGridData p = new ExtGridData();
					p.setForum(getValue(fields, 0));
					p.setDate(sf.parse(getValue(fields, 1)));
					p.setSubject(getValue(fields, 2));
					p.setUsername(getValue(fields, 2 + 2));
					posts.add(p);
				}
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String getValue(final NodeList fields, final int index) {
		NodeList list = fields.item(index).getChildNodes();
		if (list.getLength() > 0) {
			return list.item(0).getNodeValue();
		} else {
			return "";
		}
	}

}
