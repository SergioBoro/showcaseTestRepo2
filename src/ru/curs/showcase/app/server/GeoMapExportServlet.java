package ru.curs.showcase.app.server;

import java.io.*;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.util.*;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamReader;

/**
 * Обработчик экспорта в PNG.
 * 
 * @author den
 * 
 */
public class GeoMapExportServlet extends HttpServlet {

	private static final long serialVersionUID = -4464217023791442531L;

	public GeoMapExportServlet() {
		super();
	}

	protected Object deserializeObject(final String data) throws SerializationException {
		ServerSerializationStreamReader streamReader =
			new ServerSerializationStreamReader(Thread.currentThread().getContextClassLoader(),
					null);
		streamReader.prepareToRead(data);
		return streamReader.readObject();
	}

	protected String decodeParamValue(final String value) throws UnsupportedEncodingException {
		return URLDecoder.decode(value, TextUtils.DEF_ENCODING);
	}

	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		try {
			GeoMapExportSettings settings =
				(GeoMapExportSettings) deserializeObject(decodeParamValue(request
						.getParameter(GeoMapExportSettings.class.getName())));
			ImageFormat imageFormat =
				ImageFormat.valueOf(request.getParameter(ImageFormat.class.getName()));
			String fileName = settings.getFileName() + "." + imageFormat.toString().toLowerCase();

			if (ServletUtils.isOldIE(request)) {
				response.setContentType("application/force-download");
			} else {
				response.setContentType("application/octet-stream");
			}
			// По агентурным данным для старых версий IE
			// "application/octet-stream"
			// обрабатывается некорректно.
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=\"%s\"", fileName));
			String svg = request.getParameter("svg");
			switch (imageFormat) {
			case PNG:
				SVGConvertor convertor = new SVGConvertor(settings);
				ByteArrayOutputStream os = (ByteArrayOutputStream) convertor.svgStringToPNG(svg);
				OutputStream out = response.getOutputStream();
				out.write(os.toByteArray());
				out.close();
				break;
			case JPG:
				convertor = new SVGConvertor(settings);
				os = (ByteArrayOutputStream) convertor.svgStringToJPEG(svg);
				out = response.getOutputStream();
				out.write(os.toByteArray());
				out.close();
				break;
			case SVG:
				response.getWriter().append(svg);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
