package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.*;

import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.SelfCheckObject;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.model.datapanel.*;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.util.xml.*;

/**
 * Тесты класса XMLProcessor.
 */
public class XMLUtilsTest extends AbstractTestWithDefaultUserData {

	private static final String TEST_GOOD_XSD = "test_good.xsd";
	private static final String TEST_BAD_XSD = "test_bad.xsd";
	private static final String TEST_TEXT_SAMPLE_XML = "TestTextSample.xml";
	private static final String TEST_GOOD_XSL = "test_good.xsl";
	private static final String TEST_STR2 = "учреждениях";
	private static final String TEST_STR1 = ">II. Индикаторы задач проекта</td>";

	/**
	 * Получает выходной SQLXML по входному.
	 * 
	 * @param connection
	 *            - соединение.
	 * @param sqlxmlIn
	 *            - входной SQLXML.
	 * @return - выходной SQLXML.
	 * @throws SQLException
	 */
	private static SQLXML
			getOutputByInputSQLXML(final Connection connection, final SQLXML sqlxmlIn)
					throws SQLException {

		Statement st = connection.createStatement();

		String stmt = "DROP PROCEDURE [dbo].[_DebugXMLProcessor2]";
		try {
			st.executeUpdate(stmt);
		} catch (Exception e) {
			stmt = "";
		}
		stmt =
			"CREATE PROCEDURE [dbo].[_DebugXMLProcessor2] @par1 xml, @par2 xml Output AS set @par2 = @par1";
		st.executeUpdate(stmt);

		stmt = "{call _DebugXMLProcessor2(?,?)}";
		CallableStatement cs = connection.prepareCall(stmt);
		cs.setSQLXML(1, sqlxmlIn);
		cs.registerOutParameter(2, java.sql.Types.SQLXML);
		cs.execute();

		SQLXML sqlxmlOut = cs.getSQLXML(2);
		return sqlxmlOut;
	}

	/**
	 * Тест1 ф-ции xsltTransform.
	 */
	@Test
	public final void test1XsltTransform() throws SAXException, IOException, SQLException,
			TransformerException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML));

		Connection connection = ConnectionFactory.getConnection();
		try {
			SQLXML sqlxmlIn = XMLUtils.domToSQLXML(doc, connection);
			SQLXML sqlxmlOut = getOutputByInputSQLXML(connection, sqlxmlIn);
			String xsltFileName = TEST_GOOD_XSL;

			String out = XMLUtils.xsltTransform(sqlxmlOut, xsltFileName);

			assertTrue(out.indexOf(TEST_STR1) > -1);

			assertTrue(out.indexOf(TEST_STR2) > -1);
		} finally {
			connection.close();
		}

	}

	/**
	 * Тест2 ф-ции xsltTransform.
	 * 
	 * @throws TransformerException
	 * @throws SQLException
	 */
	@Test(expected = XSLTTransformException.class)
	public final void test2XsltTransformForCheckSAXON() throws SAXException, IOException,
			SQLException, TransformerException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML));

		Connection connection = ConnectionFactory.getConnection();

		try {
			SQLXML sqlxmlIn = XMLUtils.domToSQLXML(doc, connection);
			SQLXML sqlxmlOut = getOutputByInputSQLXML(connection, sqlxmlIn);
			String xsltFileName = "test_bad.xsl";
			String out = XMLUtils.xsltTransform(sqlxmlOut, xsltFileName);
			assertTrue(out.indexOf(TEST_STR1) > -1);
			assertTrue(out.indexOf(TEST_STR2) > -1);
		} finally {
			connection.close();
		}

	}

	/**
	 * Тест3 ф-ции xsltTransform.
	 */
	@Test
	public final void test3XsltTransform() throws SAXException, IOException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML));

		String xsltFileName = TEST_GOOD_XSL;

		String out = XMLUtils.xsltTransform(doc, xsltFileName);

		assertTrue(out.indexOf(TEST_STR1) > -1);

		assertTrue(out.indexOf(TEST_STR2) > -1);

	}

	/**
	 * Тест4 ф-ции xsltTransform.
	 */
	@Test
	public final void test4XsltTransform() {
		SAXParser saxParser = XMLUtils.createSAXParser();

		String xsltFileName = TEST_GOOD_XSL;

		String out =
			XMLUtils.xsltTransform(saxParser,
					XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML), xsltFileName);

		assertTrue(out.indexOf(TEST_STR1) > -1);

		assertTrue(out.indexOf(TEST_STR2) > -1);

	}

	/**
	 * Тест5 ф-ции xsltTransform.
	 */
	@Test
	public final void test5XsltTransform() {

		String xsltFileName = TEST_GOOD_XSL;

		String out =
			XMLUtils.xsltTransform(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML),
					xsltFileName);

		assertTrue(out.indexOf(TEST_STR1) > -1);

		assertTrue(out.indexOf(TEST_STR2) > -1);
	}

	/**
	 * Тест ф-ции xsltTransformForGrid.
	 */
	@Test
	public final void testXsltTransformForGrid() throws SAXException, IOException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream("TestGridSample.xml"));

		ByteArrayOutputStream out = XMLUtils.xsltTransformForGrid(doc);
		assertNotNull(out);

		doc = db.parse(new ByteArrayInputStream(out.toByteArray()));

		NodeList nl = doc.getElementsByTagName("Workbook");
		assertEquals(1, nl.getLength());

		nl = doc.getElementsByTagName("Worksheet");
		assertEquals(1, nl.getLength());

		nl = doc.getElementsByTagName("Styles");
		assertEquals(1, nl.getLength());

	}

	/**
	 * Тест11 ф-ции xsdValidate.
	 */
	@Test
	public final void test11ValidateXSD() throws SAXException, IOException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML));

		String xsdFileName = TEST_GOOD_XSD;

		XMLUtils.xsdValidateUserData(doc, xsdFileName);

	}

	/**
	 * Тест12 ф-ции xsdValidate.
	 */
	@Test(expected = XSDValidateException.class)
	public final void test12ValidateXSD() throws SAXException, IOException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML));

		String xsdFileName = TEST_BAD_XSD;

		XMLUtils.xsdValidateUserData(doc, xsdFileName);

	}

	/**
	 * Тест13 ф-ции xsdValidate.
	 */
	@Test
	public final void test13ValidateXSD() {
		SAXParser saxParser = XMLUtils.createSAXParser();

		String xsdFileName = TEST_GOOD_XSD;

		XMLUtils.xsdValidateUserData(saxParser,
				XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML), xsdFileName);

	}

	/**
	 * Тест14 ф-ции xsdValidate.
	 */
	@Test(expected = XSDValidateException.class)
	public final void test14ValidateXSD() {
		SAXParser saxParser = XMLUtils.createSAXParser();

		String xsdFileName = TEST_BAD_XSD;

		XMLUtils.xsdValidateUserData(saxParser,
				XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML), xsdFileName);
	}

	/**
	 * Тест15 ф-ции xsdValidate.
	 */
	@Test
	public final void test15ValidateXSD() {

		String xsdFileName = TEST_GOOD_XSD;

		XMLUtils.xsdValidateUserData(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML),
				xsdFileName);

	}

	/**
	 * Тест16 ф-ции xsdValidate.
	 */
	@Test(expected = XSDValidateException.class)
	public final void test16ValidateXSD() {

		String xsdFileName = TEST_BAD_XSD;

		XMLUtils.xsdValidateUserData(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML),
				xsdFileName);

	}

	/**
	 * Тест15 ф-ции xsdValidate.
	 */
	@Test
	public final void testDPGoodValidateXSD() {
		String xsdFileName = DataPanelFactory.DATAPANEL_XSD;

		DataPanelGateway gateway = new DataPanelFileGateway();
		XMLValidator validator = new XMLValidator(new ClassPathXSDSource());
		validator.validate(new XMLSource(gateway.getRawData(new CompositeContext(), TEST_XML)
				.getData(), TEST_XML, xsdFileName));
	}

	/**
	 * Тест16 ф-ции xsdValidate.
	 */
	@Test(expected = XSDValidateException.class)
	public final void testDPBadValidateXSD() {
		String xsdFileName = DataPanelFactory.DATAPANEL_XSD;

		DataPanelGateway gateway = new DataPanelFileGateway();
		XMLValidator validator = new XMLValidator(new ClassPathXSDSource());
		validator.validate(new XMLSource(gateway.getRawData(new CompositeContext(),
				"test.bad1.xml").getData(), "test.bad1.xml", xsdFileName));
	}

	/**
	 * Проверка функции
	 * {@link ru.curs.showcase.util.xml.XMLUtils#testXmlServiceSymbolsToNormal
	 * XMLUtils.testXmlServiceSymbolsToNormal}.
	 * 
	 */
	@Test
	public void testXmlServiceSymbolsToNormal() {
		String original = "&amp;&quot;&apos;&gt;&lt; какой-то текст";
		String result = XMLUtils.xmlServiceSymbolsToNormal(original);
		assertEquals("&\"'>< какой-то текст", result);
	}

	/**
	 * Проверяет сериализацию текущего контекста в XML.
	 */
	@Test
	public void testContextToXML() {
		CompositeContext context = CompositeContext.createCurrent();
		Document doc = XMLUtils.objectToXML(context);
		assertEquals(Action.CONTEXT_TAG, doc.getDocumentElement().getNodeName());
		assertEquals(1, doc.getDocumentElement().getElementsByTagName("additional").getLength());
		assertEquals(1, doc.getDocumentElement().getElementsByTagName("main").getLength());
		assertEquals(0, doc.getDocumentElement().getElementsByTagName(Action.FILTER_TAG)
				.getLength());
		assertEquals(0, doc.getDocumentElement().getElementsByTagName("session").getLength());
		CompositeContext context2 =
			(CompositeContext) XMLUtils.xmlToObject(doc.getDocumentElement(),
					CompositeContext.class);
		assertEquals(context, context2);
	}

	@Test
	public void testGridContextToXML() {
		CompositeContext context = new CompositeContext();
		context.setMain(null);
		context.setSession(null);
		GridContext gc = getExtGridContext(context);
		Document doc = XMLUtils.objectToXML(context.getRelated().get("01"));
		GridContext gc2 =
			(GridContext) XMLUtils.xmlToObject(doc.getDocumentElement(), GridContext.class);

		assertEquals(gc, gc2);
	}

	@Test
	public void testXFormsContextToXML() {
		CompositeContext context = new CompositeContext();
		XFormContext xc = new XFormContext();
		xc.setFormData("<schema/>");
		xc.setAdditional(ADD_CONDITION);
		context.addRelated("01", xc);
		Document doc = XMLUtils.objectToXML(context.getRelated().get("01"));
		XFormContext xc2 =
			(XFormContext) XMLUtils.xmlToObject(doc.getDocumentElement(), XFormContext.class);

		assertEquals(xc, xc2);
	}

	/**
	 * Тестирования сериализации информации об элементе панели в XML.
	 * 
	 */
	@Test
	public void testDPElementInfoToXML() {
		DataPanelElementInfo element = getTestXForms1Info();
		Document doc = XMLUtils.objectToXML(element);
		assertEquals("element", doc.getDocumentElement().getNodeName());
		SelfCheckObject el2 =
			(SelfCheckObject) XMLUtils.xmlToObject(doc.getDocumentElement(),
					DataPanelElementInfo.class);
		assertEquals(element, el2);
	}

	@Test
	public final void testDPTableLayoutXSD() {
		String xsdFileName = DataPanelFactory.DATAPANEL_XSD;

		DataPanelGateway gateway = new DataPanelFileGateway();
		XMLValidator validator = new XMLValidator(new ClassPathXSDSource());
		validator.validate(new XMLSource(gateway.getRawData(new CompositeContext(), RICH_DP)
				.getData(), RICH_DP, xsdFileName));
	}
}
