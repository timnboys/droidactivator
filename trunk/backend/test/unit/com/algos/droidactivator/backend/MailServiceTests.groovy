package com.algos.droidactivator.backend

import grails.plugin.mail.MailService
import grails.test.GrailsUnitTestCase
import grails.test.mixin.TestFor

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(MailService)
class MailServiceTests extends GrailsUnitTestCase {

    // Initialize the my service.
    MailService mailService = new MailService()


    protected void setUp() {
        super.setUp()
    }


    protected void tearDown() {
        super.tearDown()
    }


    void testSomething() {
        mailService.sendMail {
            to " email@mail.com "
            from " youraccount@gmail.com "
            subject "Test mail"
            html """Hello!
               This is a test email. """
        }
    }
}
