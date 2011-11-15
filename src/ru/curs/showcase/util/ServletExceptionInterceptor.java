package ru.curs.showcase.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.aspectj.lang.annotation.*;
import org.slf4j.*;

import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Перехватчик для исключений при вызове сервлетов.
 * 
 * @author den
 * 
 */
@Aspect
public final class ServletExceptionInterceptor {

	private static final String ERROR_MES = "Сообщение об ошибке";
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ServletExceptionInterceptor.class);

	@SuppressWarnings("unused")
	@Pointcut("args(request, response) && execution(protected void javax.servlet.http.HttpServlet.do*(..))")
	private
			void servletExecutionPointcut(final HttpServletRequest request,
					final HttpServletResponse response) {
	};

	@Before("servletExecutionPointcut(request, response)")
	public void logInput(final HttpServletRequest request, final HttpServletResponse response) {
	}

	@AfterThrowing(pointcut = "servletExecutionPointcut(request, response)", throwing = "e")
	public void logException(final HttpServletRequest request, final HttpServletResponse response,
			final Throwable e) throws IOException {
		Throwable exc = e;
		if ((exc instanceof ServletException) && (exc.getCause() != null)) {
			exc = exc.getCause();
		}
		if (!(exc instanceof GeneralException) && !(exc instanceof BaseException)) {
			LOGGER.error(ERROR_MES, exc);
		}
		ServletUtils.fillErrorResponce(response, exc.getLocalizedMessage());
	}
}
