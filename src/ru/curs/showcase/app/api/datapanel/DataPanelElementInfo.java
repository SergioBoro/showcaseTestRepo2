package ru.curs.showcase.app.api.datapanel;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.beta2.extra.gwt.ui.SerializableElement;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.AppLogicError;

/**
 * Класс с описанием элемента информационной панели. Примечание: свойство
 * {@link #tabPosition} в классе носит справочный характер и поэтому не
 * учитывается при сравнении и не сериализуется в XML!
 * 
 * @author den
 * 
 */
@XmlRootElement(name = "element")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataPanelElementInfo extends TransferableElement implements SerializableElement {
	static final String KEEP_USER_SETTINGS_ERROR =
		"Невозможно получить значение keepUserSettings для действия, не содержащего блока для работы с инф. панелью";
	static final String UNKNOWN_ELEMENT_TYPE = "Неизвестный тип элемента информационной панели";
	public static final int DEF_TIMER_INTERVAL = 600;
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -6461216659708261808L;

	/**
	 * Идентификатор элемента.
	 */
	private String id;

	/**
	 * Позиция элемента на вкладке.
	 */
	private Integer position;

	/**
	 * Тип элемента панели управления.
	 */
	private DataPanelElementType type;

	/**
	 * Наименование хранимой процедуры, которая загружает данные в элемент. Для
	 * тестовых файловых шлюзов - имя файла, из которого загружаются данные.
	 */
	private String procName;

	/**
	 * Наименование файла XSLT трансформации, используемой для преобразования
	 * данных, полученных из БД. Требуется только для элементов типа "WEBTEXT".
	 * 
	 */
	private String transformName;

	/**
	 * Наименование файла шаблона, используемого для отображения элемента.
	 * Шаблон преобразуется в HTML с помощью XSLT. Требуется только для
	 * элементов типа "XFORMS", причем является обязательным для этих элементов.
	 */
	private String templateName;

	/**
	 * Признак того, что при первой отрисовке элемента он прячется и не
	 * наполняется реальными данными. Необходим для оптимизации быстродействия.
	 */
	private Boolean hideOnLoad = false;

	/**
	 * Признак того, что элемент никогда не будет показываться внутри вкладки, а
	 * будет отрываться в внешнем по отношению к вкладке окне.
	 */
	private Boolean neverShowInPanel = false;

	/**
	 * Имя класс CSS для элемента. Т.об. можно задать класс для нескольких
	 * элементов.
	 */
	private String styleClass;

	/**
	 * Признак того, что нужно сохранять данные элемента и не обращаться к
	 * серверу повторно при возврате на вкладку или панель элемента. На
	 * выполнение действий с типом RELOAD_ELEMENTS данная опция не влияет. В
	 * режиме cacheData = true должна быть возможность принудительного
	 * обновления элемента. Элементы кэшируются по составному ключу, включающему
	 * в себя FullId элемента и main context.
	 */
	private Boolean cacheData = false;

	/**
	 * Признак того, нужно ли обновлять содержимое элемента по таймеру. При этом
	 * время отсчитывается от последнего из 3 событий: 1) последней загрузки
	 * данных элемента, инициированной из UI, 2) выполнения действия,
	 * обновившего данные текущего элемента, 3) последнего обновления по
	 * таймеру.
	 */
	private Boolean refreshByTimer = false;
	/**
	 * Интервал обновления панели в секундах. Используется только если
	 * refreshByTimer=true.
	 */
	private Integer refreshInterval = DEF_TIMER_INTERVAL;

	/**
	 * Дополнительные процедуры для элемента панели управления. Используются для
	 * элементов XForms.
	 */
	private Map<String, DataPanelElementProc> procs = new TreeMap<String, DataPanelElementProc>();

	/**
	 * Ссылка на вкладку панели, на которой расположен элемент.
	 */
	@XmlTransient
	@ExcludeFromSerialization
	private DataPanelTab tab;

	public DataPanelElementInfo(final Integer aPosition, final DataPanelTab aTab) {
		super();
		position = aPosition;
		tab = aTab;
	}

	public DataPanelElementInfo() {
		super();
	}

	/**
	 * Проверка на то, что данные для элемента заданы корректно. Необходима в
	 * дополнение к проверке XSD, т.к. не всегда панель приходит к нам в виде
	 * XML.
	 * 
	 * @return результат проверки.
	 */
	public Boolean isCorrect() {
		Boolean baseCheckRes = id != null;
		switch (type) {
		case WEBTEXT:
			return baseCheckRes && ((procName != null) || (transformName != null));
		case GRID:
		case CHART:
		case GEOMAP:
			return baseCheckRes && (procName != null);
		case XFORMS:
			return baseCheckRes && (templateName != null);
		default:
			throw new Error(UNKNOWN_ELEMENT_TYPE);
		}
	}

	public DataPanelElementInfo(final String aId, final DataPanelElementType aType) {
		super();
		id = aId;
		type = aType;
	}

	public final String getId() {
		return id;
	}

	public final void setId(final String aId) {
		this.id = aId;
	}

	public final DataPanelElementType getType() {
		return type;
	}

	public final void setType(final DataPanelElementType aType) {
		this.type = aType;
	}

	public final String getProcName() {
		return procName;
	}

	public final void setProcName(final String aProcName) {
		this.procName = aProcName;
	}

	public final String getTransformName() {
		return transformName;
	}

	public final void setTransformName(final String aTransformName) {
		this.transformName = aTransformName;
	}

	public final Integer getPosition() {
		return position;
	}

	public final void setPosition(final Integer aPosition) {
		position = aPosition;
	}

	/**
	 * Возвращает процедуру для сохранения данных.
	 */
	public DataPanelElementProc getSaveProc() {
		return getProcByType(DataPanelElementProcType.SAVE);
	}

	/**
	 * Возвращает процедуру для получения метаданных. Если данная процедура
	 * отсутствует - значит для загрузки данных и метаданных используется одна и
	 * та же процедура - getProcName().
	 */
	public DataPanelElementProc getMetadataProc() {
		return getProcByType(DataPanelElementProcType.METADATA);
	}

	public final Boolean getHideOnLoad() {
		return hideOnLoad;
	}

	public final void setHideOnLoad(final Boolean aHideOnLoad) {
		hideOnLoad = aHideOnLoad;
	}

	/**
	 * Возвращает процедуру определенного типа. Использовать данную процедуру
	 * имеет смысл только с теми типами, который могут содержаться в одном
	 * экземпляре.
	 * 
	 * @param procType
	 *            - тип процедуры.
	 */
	private DataPanelElementProc getProcByType(final DataPanelElementProcType procType) {
		for (DataPanelElementProc cur : procs.values()) {
			if (cur.getType() == procType) {
				return cur;
			}
		}
		return null;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(final String aTemplateName) {
		templateName = aTemplateName;
	}

	public Map<String, DataPanelElementProc> getProcs() {
		return procs;
	}

	public void setProcs(final Map<String, DataPanelElementProc> aProcs) {
		procs = aProcs;
	}

	// CHECKSTYLE:OFF
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cacheData == null) ? 0 : cacheData.hashCode());
		result = prime * result + ((hideOnLoad == null) ? 0 : hideOnLoad.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((neverShowInPanel == null) ? 0 : neverShowInPanel.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((procName == null) ? 0 : procName.hashCode());
		result = prime * result + ((procs == null) ? 0 : procs.hashCode());
		result = prime * result + ((refreshByTimer == null) ? 0 : refreshByTimer.hashCode());
		result = prime * result + ((refreshInterval == null) ? 0 : refreshInterval.hashCode());
		result = prime * result + ((styleClass == null) ? 0 : styleClass.hashCode());
		result = prime * result + ((templateName == null) ? 0 : templateName.hashCode());
		result = prime * result + ((transformName == null) ? 0 : transformName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	// CHECKSTYLE:ON

	// CHECKSTYLE:OFF
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DataPanelElementInfo)) {
			return false;
		}
		DataPanelElementInfo other = (DataPanelElementInfo) obj;
		if (cacheData == null) {
			if (other.cacheData != null) {
				return false;
			}
		} else if (!cacheData.equals(other.cacheData)) {
			return false;
		}
		if (hideOnLoad == null) {
			if (other.hideOnLoad != null) {
				return false;
			}
		} else if (!hideOnLoad.equals(other.hideOnLoad)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (neverShowInPanel == null) {
			if (other.neverShowInPanel != null) {
				return false;
			}
		} else if (!neverShowInPanel.equals(other.neverShowInPanel)) {
			return false;
		}
		if (position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!position.equals(other.position)) {
			return false;
		}
		if (procName == null) {
			if (other.procName != null) {
				return false;
			}
		} else if (!procName.equals(other.procName)) {
			return false;
		}
		if (procs == null) {
			if (other.procs != null) {
				return false;
			}
		} else if (!procs.equals(other.procs)) {
			return false;
		}
		if (refreshByTimer == null) {
			if (other.refreshByTimer != null) {
				return false;
			}
		} else if (!refreshByTimer.equals(other.refreshByTimer)) {
			return false;
		}
		if (refreshInterval == null) {
			if (other.refreshInterval != null) {
				return false;
			}
		} else if (!refreshInterval.equals(other.refreshInterval)) {
			return false;
		}
		if (styleClass == null) {
			if (other.styleClass != null) {
				return false;
			}
		} else if (!styleClass.equals(other.styleClass)) {
			return false;
		}
		if (templateName == null) {
			if (other.templateName != null) {
				return false;
			}
		} else if (!templateName.equals(other.templateName)) {
			return false;
		}
		if (transformName == null) {
			if (other.transformName != null) {
				return false;
			}
		} else if (!transformName.equals(other.transformName)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	// CHECKSTYLE:ON

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(final String aStyleClass) {
		styleClass = aStyleClass;
	}

	public DataPanelTab getTab() {
		return tab;
	}

	public void setTab(final DataPanelTab aTab) {
		tab = aTab;
	}

	public Boolean getNeverShowInPanel() {
		return neverShowInPanel;
	}

	public void setNeverShowInPanel(final Boolean aNeverShowInPanel) {
		neverShowInPanel = aNeverShowInPanel;
	}

	@Override
	public String toString() {
		return "DataPanelElementInfo [id=" + id + ", position=" + position + ", type=" + type
				+ ", procName=" + procName + ", transformName=" + transformName
				+ ", templateName=" + templateName + ", hideOnLoad=" + hideOnLoad
				+ ", neverShowInPanel=" + neverShowInPanel + ", styleClass=" + styleClass
				+ ", cacheData=" + cacheData + ", refreshByTimer=" + refreshByTimer
				+ ", refreshInterval=" + refreshInterval + ", procs=" + procs + "]";
	}

	/**
	 * Возвращает значение keepUserSettings, заданное в действии для данного
	 * элемента.
	 * 
	 * @param ac
	 *            - действие.
	 * @return - keepUserSettings.
	 */
	public Boolean getKeepUserSettings(final Action ac) {
		if (ac.getDataPanelLink() != null) {
			for (DataPanelElementLink link : ac.getDataPanelLink().getElementLinks()) {
				if (link.getId().equals(id)) {
					return link.getKeepUserSettings();
				}
			}

			return ac.getKeepUserSettings();
		}
		throw new AppLogicError(KEEP_USER_SETTINGS_ERROR);
	}

	/**
	 * Возвращает текущий контекст для элемента из переданного действия.
	 * 
	 * @param ac
	 *            - действие.
	 * @return - контекст.
	 */
	public CompositeContext getContext(final Action ac) {
		DataPanelLink dpLink = ac.getDataPanelLink();
		if (dpLink != null) {
			for (DataPanelElementLink link : dpLink.getElementLinks()) {
				if (link.getId().equals(id)) {
					return link.getContext();
				}
			}
			return ac.getContext();
		}
		return null;
	}

	/**
	 * Возвращает полный уникальный идентификатор элемента, включающий в себя
	 * идентификатор панели.
	 * 
	 * @return - идентификатор.
	 */
	public String getFullId() {
		return "dpe_" + tab.getDataPanel().getId() + "_" + id;
	}

	/**
	 * Возвращает ключ, который может быть использован для кэширования элементов
	 * на клиентской стороне.
	 * 
	 * @param context
	 *            - контектс для элемента.
	 * @return - ключ.
	 */
	public String getKeyForCaching(final CompositeContext context) {
		return getFullId() + "_" + context.getMain();
	}

	public Integer getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(final Integer aRefreshInterval) {
		refreshInterval = aRefreshInterval;
	}

	public Boolean getRefreshByTimer() {
		return refreshByTimer;
	}

	public void setRefreshByTimer(final Boolean aRefreshByTimer) {
		refreshByTimer = aRefreshByTimer;
	}

	public Boolean getCacheData() {
		return cacheData;
	}

	public void setCacheData(final Boolean aCacheData) {
		cacheData = aCacheData;
	}

	/**
	 * Признак того, что для загрузки элемента используется только одна
	 * процедура.
	 */
	public boolean loadByOneProc() {
		return getMetadataProc() == null;
	}
}
