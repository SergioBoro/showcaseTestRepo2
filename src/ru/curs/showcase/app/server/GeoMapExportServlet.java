package ru.curs.showcase.app.server;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.model.svg.SVGGetCommand;
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

	private static final String SVG_DATA_PARAM = "svg";
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

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		GeoMapExportSettings settings = null;
		ImageFormat imageFormat = null;
		String svg = null;

		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(request);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				String name = item.getFieldName();
				InputStream input = item.openStream();
				// несмотря на то, что нам нужен InputStream - его приходится
				// преобразовывать в OutputStream - т.к. чтение из InputStream
				// возможно только в данном цикле
				ByteArrayOutputStream out = StreamConvertor.inputToOutputStream(input);
				String paramValue = out.toString();
				if (GeoMapExportSettings.class.getName().equals(name)) {
					settings = (GeoMapExportSettings) deserializeObject(paramValue);
				} else if (ImageFormat.class.getName().equals(name)) {
					imageFormat = ImageFormat.valueOf(paramValue);
				} else if (SVG_DATA_PARAM.equals(name)) {
					svg = paramValue;
				}
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
		String fileName = settings.getFileName() + "." + imageFormat.toString().toLowerCase();

		response.setHeader("Content-Disposition",
				String.format("attachment; filename=\"%s\"", fileName));

		Map<String, List<String>> params = ServletUtils.prepareURLParamsMap(request);
		params.remove(GeoMapExportSettings.class.getName());
		params.remove(SVG_DATA_PARAM);
		params.remove(ImageFormat.class.getName());
		CompositeContext context = new CompositeContext(params);

		switch (imageFormat) {
		case PNG:
			SVGConvertor convertor = new SVGConvertor(settings);
			ByteArrayOutputStream os = convertor.svgStringToPNG(svg);

			response.setContentType("image/png");
			writeStreamToResponse(response, os);
			break;
		case JPG:
			convertor = new SVGConvertor(settings);
			os = convertor.svgStringToJPEG(svg);

			response.setContentType("image/jpg");
			writeStreamToResponse(response, os);
			break;
		case SVG:
			SVGGetCommand command = new SVGGetCommand(context, settings, imageFormat, svg);
			response.setCharacterEncoding(TextUtils.DEF_ENCODING);
			response.setContentType("application/svg+xml");
			response.getWriter().append(command.execute());
			break;
		default:
			break;
		}
	}

	public void writeStreamToResponse(final HttpServletResponse response,
			final ByteArrayOutputStream os) throws IOException {
		OutputStream out = response.getOutputStream();
		out.write(os.toByteArray());
		out.close();
	}
}
