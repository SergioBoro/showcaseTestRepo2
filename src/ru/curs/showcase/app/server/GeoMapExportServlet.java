package ru.curs.showcase.app.server;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

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
	protected void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		try {
			GeoMapExportSettings settings =
				(GeoMapExportSettings) deserializeObject(request
						.getParameter(GeoMapExportSettings.class.getName()));
			ImageFormat imageFormat =
				ImageFormat.valueOf(request.getParameter(ImageFormat.class.getName()));
			String svg = request.getParameter(SVG_DATA_PARAM);

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
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	public void writeStreamToResponse(final HttpServletResponse response,
			final ByteArrayOutputStream os) throws IOException {
		OutputStream out = response.getOutputStream();
		out.write(os.toByteArray());
		out.close();
	}
}
