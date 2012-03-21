package com.algos.droidactivator.resources;

public enum Strings {
	dialog_title("Activation required", "Attivazione"), //

	dialog_message("Welcome!\nA confirmation e-mail containing activation data will be sent to "
			+ "you as soon as we receive your purchase data.\n\nIn the meanwhile, you can still "
			+ "activate your app temporarily by pressing [Later].",

	"Benvenuto!\nAppena riceveremo i dati relativi al vostro acquisto vi invieremo "
			+ "una e-mail con i dati per l'attivazione.\n\nNel frattempo, potete attivare "
			+ "temporaneamente l'applicazione premendo il tasto [Dopo]."), //

	input_userid_label("Enter your email", "Inserite la vostra e-mail"), //

	input_code_label("Enter your code", "Inserite il codice di attivazione"), //
	
	;

	private String en;
	private String it;


	private Strings(String en, String it) {
		this.en = en;
		this.it = it;
	}


	public String get() {
		String string = "";

		if (Languages.isEnglish()) {
			string = en;
		}

		if (Languages.isItalian()) {
			string = it;
		}

		return string;
	}
}
