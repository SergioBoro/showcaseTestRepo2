package ru.curs.showcase.util;

import java.io.*;

/**
 * Класс для пакетных операций с файлами. Используется совместно с классами,
 * реализующими интерфейс {@link ru.curs.showcase.util.FileAction FileAction}:
 * 
 * {@link ru.curs.showcase.util.CopyFileAction CopyFileAction}. а также
 * интерфейс {@link java.io.FilenameFilter FilenameFilter}:
 * {@link ru.curs.showcase.util.RegexFilenameFilter RegexFilenameFilter}. Пример
 * использования: <code>
 * BatchFileProcessor fprocessor = new BatchFileProcessor(sourceDir, new
 * RegexFilenameFilter( "^[.].*", false)); try { fprocessor.process(new
 * CopyFileAction(destDir, true)); } catch (IOException e) {
 * LOGGER.error("Ошибка копирования файла:" + e.getMessage()); }
 * </code>
 * 
 * @author den
 * 
 */
public class BatchFileProcessor {
	/**
	 * Исходный каталог с файлами.
	 */
	private final File sourceDir;
	/**
	 * Фильтр для выборки файлов из каталога.
	 */
	private final FilenameFilter filter;

	/**
	 * Признак того, что нужно обработать также и исходный каталог.
	 */
	private boolean includeSourceDir = false;

	public BatchFileProcessor(final String aSourceDir, final FilenameFilter aFilter) {
		sourceDir = new File(aSourceDir);
		filter = aFilter;
	}

	public BatchFileProcessor(final String aSourceDir, final boolean aIncludeSourceDir) {
		sourceDir = new File(aSourceDir);
		filter = null;
		includeSourceDir = aIncludeSourceDir;
	}

	/**
	 * Выполняет действие над группой файлов.
	 * 
	 * @param action
	 *            - действие.
	 * @throws IOException
	 */
	public void process(final FileAction action) throws IOException {
		File[] flist = getFilesList();
		if (flist == null) {
			return;
		}
		for (File f : flist) {
			if (f.isFile()) {
				action.perform(f);
			} else if (f.isDirectory()) {
				BatchFileProcessor bfp =
					new BatchFileProcessor(getParentDir() + File.separator + f.getName(), filter);
				bfp.process(action.cloneForHandleChildDir(f.getName()));
				action.perform(f);
			}
		}
	}

	private String getParentDir() {
		if (includeSourceDir) {
			return sourceDir.getParent();
		}
		return sourceDir.getPath();
	}

	private File[] getFilesList() {
		if (includeSourceDir) {
			File[] flist = { sourceDir };
			return flist;
		} else {
			if (filter != null) {
				return sourceDir.listFiles(filter);
			} else {
				return sourceDir.listFiles();
			}
		}
	}
}
