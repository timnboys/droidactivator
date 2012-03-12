import com.algos.droidactivator.backend.Activation

class BootStrap {

    def init = { servletContext ->

        this.testData()

        // nome dell'eventuale controller da invocare automaticamente
        // alla partenza del programma.
        // parte il metodo di default del controller.
        // se non definita visualizza un elenco dei moduli/controller visibili
        // vedi index.gsp
        servletContext.startController = "/activator/activation"
    }


    private void testData() {
        // Check whether the test data already exists.
        if (!Activation.count()) {
            new Activation(appName: 'eStudio', paid: true, amount: 59, level: 1, userGoogleMail: 'alex@algos.it', expiration: new Date() + 7).save(failOnError: true)
            new Activation(appName: 'eStudio', paid: true, amount: 59, active: true, level: 1, userGoogleMail: 'gac@algos.it', uniqueID: 'g8F3GvVNqi1RpJqIXN1QeJRibRME1fo8UZwiKZCddE').save(failOnError: true)
        }// fine del blocco if
    }


    def destroy = {
    }

}// end of bootstrap
