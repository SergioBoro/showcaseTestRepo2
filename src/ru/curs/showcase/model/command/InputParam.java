package ru.curs.showcase.model.command;

import java.lang.annotation.*;

/**
 * Признак входного параметра для команды.
 * 
 * @author den
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InputParam {

}
