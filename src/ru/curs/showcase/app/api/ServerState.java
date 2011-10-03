package ru.curs.showcase.app.api;

import javax.xml.bind.annotation.*;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Класс текущего состояния приложения.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class ServerState implements SerializableElement {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -6409374109173355019L;

	/**
	 * Версия приложения.
	 */
	private String appVersion;

	/**
	 * Имя текущего пользователя.
	 */
	private String userName;

	/**
	 * Признак того, что это собственный пользователь приложения. Альтернатива -
	 * пользователь из AuthServer.
	 */
	private Boolean isNativeUser;

	/**
	 * Версия контейнера сервлетов.
	 */
	private String servletContainerVersion;

	/**
	 * Версия Java, которая используется на сервере.
	 */
	private String javaVersion;

	/**
	 * Время генерации данного объекта на сервере.
	 */
	private String serverTime;

	/**
	 * Версия SQL сервера.
	 */
	private String sqlVersion;

	private String gwtVersion;

	private String dojoVersion;

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(final String aAppVersion) {
		appVersion = aAppVersion;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(final String aUserName) {
		userName = aUserName;
	}

	public Boolean getIsNativeUser() {
		return isNativeUser;
	}

	public void setIsNativeUser(final Boolean aIsNativeUser) {
		isNativeUser = aIsNativeUser;
	}

	public String getServletContainerVersion() {
		return servletContainerVersion;
	}

	public void setServletContainerVersion(final String aServletContainerVersion) {
		servletContainerVersion = aServletContainerVersion;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(final String aJavaVersion) {
		javaVersion = aJavaVersion;
	}

	public String getServerTime() {
		return serverTime;
	}

	public void setServerTime(final String aServerTime) {
		serverTime = aServerTime;
	}

	public String getSqlVersion() {
		return sqlVersion;
	}

	public void setSqlVersion(final String aSqlVersion) {
		sqlVersion = aSqlVersion;
	}

	public String getGwtVersion() {
		return gwtVersion;
	}

	public void setGwtVersion(final String aGwtVersion) {
		gwtVersion = aGwtVersion;
	}

	public String getDojoVersion() {
		return dojoVersion;
	}

	public void setDojoVersion(final String aDojoVersion) {
		dojoVersion = aDojoVersion;
	}

}
