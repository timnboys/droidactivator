import com.algos.droidactivator.backend.Activation
import com.algos.droidactivator.backend.Role
import com.algos.droidactivator.backend.User
import com.algos.droidactivator.backend.UserRole

class BootStrap {

    def init = { servletContext ->

        this.testData()
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


    private void securitySetup() {
        String adminNicName
        String adminPass

        // create a new admin
        adminNicName = 'Gac'
        adminPass = 'fulvia'
        this.newAdmin(adminNicName, adminPass)

        // create a new admin
        adminNicName = 'Alex'
        adminPass = 'axel01'
        this.newAdmin(adminNicName, adminPass)
    }// fine della closure

    private void newAdmin(String adminNicName, String adminPass) {
        def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save()
        def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER').save()
        User user

        if (!User.findAllByUsername(adminNicName)) {
            user = new User(username: adminNicName, password: adminPass, enabled: true)
            if (userRole && adminRole && user) {
                user.save(flush: true)
                UserRole.create user, adminRole, true
                UserRole.create user, userRole, true
            }// fine del blocco if
        }// fine del blocco if
    }// fine della closure

    def destroy = {
    }

}// end of bootstrap
