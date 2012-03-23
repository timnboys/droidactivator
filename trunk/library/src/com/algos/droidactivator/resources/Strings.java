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

	cancel_button_text("Cancel", "Annulla"), //

	confirm_button_text("Activate", "Attiva"), //

	temporary_button_text("Later", "Dopo"), //

	time_remaining("Days left", "Giorni rimasti"), //

	powered_by("Powered by", "Powered by"), //

	info_dialog_default_title("Info", "Informazioni"), //

	info_dialog_button_text("Ok", "Ok"), //

	notify_dialog_default_title("Notice", "Avviso"), //

	notify_dialog_button_text("Ok", "Ok"), //

	confirm_cancel_dialog_default_title("Warning", "Attenzione"), //

	confirm_cancel_dialog_default_message("Confirm the operation?", "Confermi l'operazione?"), //

	button_cancel_dialog_text("Cancel?", "Annulla"), //

	button_confirm_dialog_text("Confirm", "Conferma"), //

	network_unavailable("Network is currently unavailable. Please check your " +
			"connection and try again.",
			"La rete non è attualmente disponibile. Controlla la connessione e riprova."), //

	backend_not_responding("The activation server is not responding. Please try again later.", 
			"Il server di attivazione non risponde. Riprova più tardi."), //

	invalid_email_address("Invalid e-mail address", "Indirizzo e-mail non valido"), //

	congratulations("Congratulations!", "Congratulazioni!"), //
	
	app_successfully_activated("has been successfully activated.\nThank You.", "è stata attivata correttamente.\nGrazie."), //

	activation_error("Activation error", "Errore di attivazione"), //

	wrong_activation_code("Wrong activation code. Make sure you typed it correctly.\n" +
			"If the problem persists, contact the support service.", 
			"Codice di attivazione errato. Controlla di averlo digitato correttamente.\n" +
			"Se il problema persiste, contatta il serizio di assistenza."), //

	wrong_app_name("Your user id was found but the application name does not match the " +
			"licensed name. Please contact the support service.", 
			"Il tuo id utente è stato riconosciuto ma il nome dell'applicazione non " +
			"corrisponde alla licenza. Contatta il servizio di assistenza."), //
			
	userid_not_found("User id not found in the activation database.\nMaybe your purchase " +
			"data has not yet been received. Please retry later.", 
			"Utente non trovato nel database di attivazione.\nProbabilmente i dati relativi " +
			"al tuo acquisto non sono ancora stati ricevuti. Riprova più tardi."), //

	unrecognized_error("Unrecognized error code", "Codice di errore non riconosciuto"), //
			
	trial_period_expired("Trial period expired", "Periodo di prova scaduto"), //

	
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
