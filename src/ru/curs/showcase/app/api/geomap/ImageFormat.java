package ru.curs.showcase.app.api.geomap;

import javax.xml.bind.annotation.*;

/**
 * Формат изображения.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public enum ImageFormat {
	SVG, PNG, JPG
}
