package ru.curs.showcase.model;

import ru.curs.showcase.runtime.ProfileReader;

/**
 * Стратегия для применения каких-либо настроек на основе профайла.
 * 
 * @author den
 * 
 */
public abstract class ProfileBasedSettingsApplyStrategy {

	public ProfileBasedSettingsApplyStrategy() {
		super();
	}

	public ProfileBasedSettingsApplyStrategy(final ProfileReader aPropsReader) {
		super();
		reader = aPropsReader;
	}

	public void apply() {
		applyByDefault();
		applyFromProfile();
	}

	protected abstract void applyFromProfile();

	protected abstract void applyByDefault();

	private ProfileReader reader;

	public ProfileReader reader() {
		return reader;
	}

	public ProfileReader getReader() {
		return reader;
	}

	public void setReader(final ProfileReader aReader) {
		reader = aReader;
	}
}
