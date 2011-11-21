package ru.curs.showcase.test;

import java.lang.reflect.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.*;

/**
 * Аспект для @Repeat в модульных тестах.
 * 
 * @author den
 * 
 */
@Aspect
public final class RepeatAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(RepeatAspect.class);

	@SuppressWarnings("unused")
	@Pointcut("execution(@ru.curs.showcase.test.Repeat public void ru.curs.showcase.test.*.*())")
	private void testWithRepeat() {
	};

	@Around("testWithRepeat() && !cflow(adviceexecution())")
	public void repeat(final ProceedingJoinPoint jp) throws IllegalAccessException,
			InvocationTargetException {
		Method method = ((MethodSignature) jp.getSignature()).getMethod();

		Repeat rmAnnotation = method.getAnnotation(Repeat.class);
		int count = rmAnnotation.count();
		for (int i = 1; i <= count; i++) {
			LOGGER.trace("Проход " + i);
			method.invoke(jp.getTarget());
		}

	}
}
