import org.grails.plugins.settings.Setting
import org.springframework.mail.MailSender
import com.algos.droidactivator.backend.*

class BootStrap {

    // utilizzo di un service con la businessLogic per l'elaborazione dei dati
    // il service viene iniettato automaticamente
    MailSender mailSender

    def init = { servletContext ->

        this.testData()
        this.testMessage()
        this.mailSettings()
        this.securitySetup()

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
            new Activation(appName: 'eStudio', paid: true, amount: 59, active: false, level: 1, userID: 'alex@algos.it', expiration: new Date() + 7).save(failOnError: true)
            new Activation(appName: 'eStudio', paid: true, amount: 59, active: true, level: 1, userID: 'gac@algos.it', uniqueID: 'g8F3GvVNqi1RpJqIXN1QeJRibRME1fo8UZwiKZCddE').save(failOnError: true)
        }// fine del blocco if
    }


    private void testMessage() {
        // Check whether the test data already exists.
        if (!MessageType.count()) {
            new MessageType(body: 'Prova in italiano. \nFunziona?').save(failOnError: true)
        }// fine del blocco if
    }


    private void mailSettings() {

        // host
        this.mailProperty('host', 'mailHost', 'smtp.somemailprovider.com')

        // userName
        this.mailProperty('username', 'mailUser', 'user@domain.com')

        // password
        this.mailProperty('password', 'mailPassword', 'password')

    }


    private void mailProperty(String property, String code, String value) {
        if (!Setting.valueFor(code)) {
            new Setting(code: code, type: 'string', value: value).save(flush: true)
        }// fine del blocco if

        mailSender."$property" = Setting.valueFor(code)
    }


    private void securitySetup() {
        String adminNicName
        String adminPass

        // create a new admin
        adminNicName = 'admin'
        adminPass = 'admin'
        this.newAdmin(adminNicName, adminPass)

    }// fine della closure

    private void newAdmin(String adminNicName, String adminPass) {
        def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save()
        User user

        if (!User.findAllByUsername(adminNicName)) {
            user = new User(username: adminNicName, password: adminPass, enabled: true)
            if (adminRole && user) {
                user.save(flush: true)
                UserRole.create user, adminRole, true
            }// fine del blocco if
        }// fine del blocco if
    }// fine della closure

    def destroy = {
    }

}// end of bootstrap
