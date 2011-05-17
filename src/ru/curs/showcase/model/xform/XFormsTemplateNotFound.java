package ru.curs.showcase.model.xform;

import ru.curs.showcase.exception.AbstractShowcaseException;

/**
 * Исключение, возникающее если не найден файл шаблона XForms.
 * 
 * @author den
 * 
 */
public class XFormsTemplateNotFound extends AbstractShowcaseException {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -4886514180537917300L;

	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES = "Не найден шаблон XForms '%s'";

	public XFormsTemplateNotFound(final String aTemplateName) {
		super(String.format(ERROR_MES, aTemplateName));
	}
}
