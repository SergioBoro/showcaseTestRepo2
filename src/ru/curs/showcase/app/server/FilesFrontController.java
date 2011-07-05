package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 * Front controller для работы с файлами.
 */
public final class FilesFrontController extends HttpServlet {

	static final String UNKNOWN_COMMAND_ERROR = "Неизвестная команда для FilesFrontController";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 7991801050316249555L;

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String servlet = request.getServletPath();
			servlet = servlet.replace("/secured/", "").toUpperCase();
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
		} catch (Throwable e) {
			ServletUtils.fillErrorResponce(response, e.getLocalizedMessage());
		}

	}
}
