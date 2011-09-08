package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.util.ServletUtils;

/**
 * Front controller для работы с файлами.
 */
public final class FilesFrontController extends HttpServlet {

	private static final String UNKNOWN_COMMAND_ERROR =
		"Неизвестная команда для FilesFrontController";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 7991801050316249555L;

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String servlet = request.getServletPath();
			servlet =
				servlet.replace("/" + ExchangeConstants.SECURED_SERVLET_PREFIX + "/", "")
						.toUpperCase();
			FilesFrontControllerAction action = FilesFrontControllerAction.valueOf(servlet);
			AbstractFilesHandler handler = null;
			switch (action) {
			case DOWNLOAD:
				handler = new DownloadHandler();
				break;
			case GRIDTOEXCEL:
				handler = new GridToExcelHandler();
				break;
			case UPLOAD:
				handler = new UploadHandler();
				break;
			default:
				ServletUtils.fillErrorResponce(response, UNKNOWN_COMMAND_ERROR);
			}
			handler.handle(request, response);
		} catch (Exception e) {
			ServletUtils.fillErrorResponce(response, e.getLocalizedMessage());
		}

	}
}
