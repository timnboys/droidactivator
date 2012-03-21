package com.algos.droidactivator.resources;

import java.util.Locale;

public enum Languages {
	english("en"), //
	french("fr"), //
	german("de"), //
	ido("io"), //
	italian("it"), //
	japanese("ja"), //
	portuguese("pt"), //
	russian("ru"), //
	spanish("es"), //
	swedish("sv"), //
	;

	// the ISO 639-1 two-characters language code
	private String code;

	// current device language
	private static Languages currLanguage = findByCode(Locale.getDefault().getLanguage());

	private Languages(String code) {
		this.code = code;
	}


	private String getCode() {
		return code;
	}


	/**
	 * Find an element by code
	 * @param the code
	 * @return the corresponding element
	 */
	static private Languages findByCode(String code) {
		Languages lang = null;
		Languages[] list = Languages.values();
		for (int i = 0; i < list.length; i++) {
			lang = list[i];
			if (lang.getCode().equals(code)) {
				break;
			}
		}
		return lang;
	}


	/**
	 * @return true if the current language is equal to the given language
	 */
	private static boolean isLanguage(Languages language) {
		return currLanguage.equals(language);
	}


	/** utility methods to directly test languages */
	public static boolean isEnglish() {
		return isLanguage(Languages.english);
	}


	public static boolean isFrench() {
		return isLanguage(Languages.french);
	}


	public static boolean isGerman() {
		return isLanguage(Languages.german);
	}


	public static boolean isItalian() {
		return isLanguage(Languages.italian);
	}


	public static boolean isJapanese() {
		return isLanguage(Languages.japanese);
	}


	public static boolean isPortuguese() {
		return isLanguage(Languages.portuguese);
	}


	public static boolean isRussian() {
		return isLanguage(Languages.russian);
	}


	public static boolean isSpanish() {
		return isLanguage(Languages.spanish);
	}

}
