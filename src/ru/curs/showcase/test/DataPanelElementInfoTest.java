package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.model.html.xform.XFormInfoFactory;

/**
 * Класс для тестирования функций DataPanelElementInfo и его наследников.
 * 
 * @author den
 * 
 */
public final class DataPanelElementInfoTest extends AbstractTestWithDefaultUserData {
	private static final String TRANSFORM_NAME = "transformName";
	private static final String PROC_NAME = "procName";

	@Test
	public void testGenerateXFormsSQLSubmissionInfo() {
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsSQLSubmissionInfo(PROC_NAME);

		assertEquals(PROC_NAME, elInfo.getProcName());
		assertEquals(DataPanelElementType.XFORMS, elInfo.getType());
		assertNotNull(elInfo.getId());
	}

	@Test
	public void testgenerateXFormsTransformationInfo() {
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsTransformationInfo(TRANSFORM_NAME);

		assertEquals(TRANSFORM_NAME, elInfo.getTransformName());
		assertEquals(DataPanelElementType.XFORMS, elInfo.getType());
		assertNotNull(elInfo.getId());
	}
}
