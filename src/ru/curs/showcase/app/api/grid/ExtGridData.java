/*
 * Ext GWT 2.2.5 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package ru.curs.showcase.app.api.grid;

import java.io.Serializable;
import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

/**
 * Класс грида из ExtGWT с данными.
 */
public class ExtGridData extends BaseTreeModel implements Serializable {

	private static final long serialVersionUID = -7861924269226866824L;
	@SuppressWarnings("unused")
	private Date dummy;

	public ExtGridData() {

	}

	public String getUsername() {
		return (String) get("username");
	}

	public void setUsername(final String username) {
		set("username", username);
	}

	public String getForum() {
		return (String) get("forum");
	}

	public void setForum(final String forum) {
		set("forum", forum);
	}

	public Date getDate() {
		return (Date) get("date");
	}

	public void setDate(final Date date) {
		set("date", date);
	}

	public String getSubject() {
		return (String) get("subject");
	}

	public void setSubject(final String subject) {
		set("subject", subject);
	}

}
