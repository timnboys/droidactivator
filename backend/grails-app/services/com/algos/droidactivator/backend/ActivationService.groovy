package com.algos.droidactivator.backend

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ActivationService {

    /**
     * Just check the connection
     */

    public String connectionRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("success", "true")
        return 'OK'
    }

    /**
     * Check the existence of Unique ID.
     *
     * @param uniqueid - the cached Unique Id
     */

    public String idRequest(HttpServletRequest request, HttpServletResponse response) {
        String risposta = 'FAILURE'
        String uniqueid = request.getHeader('uniqueid')
        Activation activationRecord

        if (uniqueid) {
            activationRecord = this.findByUniqueId(uniqueid)
            if (activationRecord) {
                // Success: send back Success flag,
                response.setHeader('success', 'true')
                risposta = 'OK'
            }// fine del blocco if
        }// fine del blocco if

        return risposta
    }

    /**
     * The backend receives the activation request whth the params
     * First, the Installation record is searched by Unique ID.
     * If not found, it is searched by User's e-mail address.
     *
     * @param uniqueid - Unique Id (always present)
     * @param userid - User's e-mail address (if provided)
     * @param activationcode - Activation Code (always present)
     * @param appname - Application name (always present)
     */

    public String activationRequest(HttpServletRequest request, HttpServletResponse response) {
        String risposta = 'FAILURE'
        Activation activationRecord
        String uniqueid = request.getHeader('uniqueid')
        String userid = request.getHeader('userid')
        String activationcode = request.getHeader('activationcode')
        String appName = request.getHeader('appname')

        if (uniqueid && activationcode) {
            // First, the Installation record is searched by Unique ID.
            activationRecord = this.findByUniqueId(uniqueid)
            if (activationRecord) {

                // This is a renewal. Check the Activation code.
                if (isActivationCodeTrue(activationcode)) {
                    // Success: set the Activation flag to true
                    activationRecord.active = true
                    activationRecord.save(flush: true)

                    // Success: send back Success flag,
                    response.setHeader('success', 'true')
                    risposta = 'OK'

                    // Success: Expiration Date
                    if (activationRecord.expiration) {
                        response.setHeader('expiration', activationRecord.expiration.getTime().toString())
                    }// fine del blocco if

                    // Success: Application Level
                    response.setHeader('level', activationRecord.level.toString())
                } else {
                    // Failure: send back Failure flag.
                    response.setHeader('success', 'false')

                    // Failure: Failure code.
                    response.setHeader("failurecode", "3")
                }// fine del blocco if-else
            } else {
                // If not found, it is searched by User's e-mail address.
                activationRecord = this.findByUserID(userid)
                if (activationRecord) {

                    // This is a new activation: check the code.
                    if (isActivationCodeAndAppName(activationcode, appName)) {
                        // Success: set the Activation flag to true
                        activationRecord.active = true
                        activationRecord.save(flush: true)

                        // Success: register the Unique id
                        activationRecord.uniqueID = uniqueid
                        activationRecord.save(flush: true)

                        // Success: send back Success flag
                        response.setHeader('success', 'true')
                        risposta = 'OK'

                        // Success: Expiration Date
                        if (activationRecord.expiration) {
                            response.setHeader('expiration', activationRecord.expiration.getTime().toString())
                        }// fine del blocco if

                        // Success: Application Level.
                        response.setHeader('level', activationRecord.level.toString())
                    } else {
                        // Failure: send back Failure flag.
                        response.setHeader('success', 'false')

                        // Failure: Failure code.
                        response.setHeader("failurecode", "4")
                    }// fine del blocco if-else

                } else {
                    // This is an attempt to activate the installation before purchase data is added to the backend.

                    // Failure: send back Failure flag.
                    response.setHeader('success', 'false')

                    // Failure: Failure code.
                    response.setHeader("failurecode", "5")
                }// fine del blocco if-else
            }// fine del blocco if-else
        } else {
            // Failure: send back Failure flag.
            response.setHeader('success', 'false')

            // Failure: Failure code.
            response.setHeader("failurecode", "6")
        }// fine del blocco if-else

        return risposta
    }// fine del metodo

    /**
     * The backend receives the update request along with the following parameters:
     *
     * @param uniqueid - the cached Unique Id
     */

    public String updateRequest(HttpServletRequest request, HttpServletResponse response) {
        String risposta = 'FAILURE'
        Activation activationRecord
        String uniqueid = request.getHeader('uniqueid')

        if (uniqueid) {
            // The Activation record is searched by Unique Id.
            activationRecord = this.findByUniqueId(uniqueid)
            if (activationRecord) {
                // Activation Record found
                // This is the standard situation

                // Send back Success code and data bundle. Activation parameters could be changed by the backend.
                response.setHeader('success', 'true')
                risposta = 'OK'

                // Success: set the Activation flag to true
                activationRecord.active = true
                activationRecord.save(flush: true)

                // Success: Expiration Date
                if (activationRecord.expiration) {
                    response.setHeader('expiration', activationRecord.expiration.getTime().toString())
                }// fine del blocco if

                // Success: Application Level.
                response.setHeader('level', activationRecord.level.toString())
            } else {
                // Activation Record not found
                // This is an attempt to update before Installation record or Unique Id are available to the backend

                // Failure: send back Failure flag.
                response.setHeader('success', 'false')

                // Failure: Failure code.
                response.setHeader("failurecode", "7")
            }// fine del blocco if-else

        }// fine del blocco if


        return risposta
    }// fine del metodo

    /**
     * First, the Installation record is searched by Unique ID.
     *
     * @param uniqueid - Unique Id (always present)
     */

    private Activation findByUniqueId(String uniqueid) {
        Activation activationRecord = null

        if (uniqueid) {
            activationRecord = Activation.findByUniqueID(uniqueid)
        }// fine del blocco if

        return activationRecord
    }// fine del metodo

    /**
     * If not found, it is searched by User's e-mail address.
     *
     * @param userid - User's e-mail address (if provided)
     */

    private Activation findByUserID(String userid) {
        Activation activationRecord = null

        if (userid) {
            activationRecord = Activation.findByUserID(userid)
        }// fine del blocco if

        return activationRecord
    }// fine del metodo

    /**
     * This is a renewal. Check the Activation code.
     * This is a new activation: check the code.
     *
     * @param activationcode - Activation Code (always present)
     */

    private boolean isActivationCodeTrue(String activationcode) {
        boolean found = false
        Activation activationRecord
        String activationCodeRecord
        String activationCodeDevice

        if (activationcode) {
            activationRecord = Activation.findByActivationCode(activationcode)
            if (activationRecord) {

                activationCodeDevice = activationcode
                activationCodeRecord = activationRecord.activationCode

                if (activationCodeDevice.equals(activationCodeRecord)) {
                    found = true
                } else {
                    found = false
                }// fine del blocco if-else

            }// fine del blocco if
        }// fine del blocco if

        return found
    }// fine del metodo

    /**
     * This is a new activation: check the code and the appName.
     *
     * @param activationcode - Activation Code (always present)
     * @param appName - Application name
     */

    private boolean isActivationCodeAndAppName(String activationcode, String appName) {
        boolean found = false
        Activation activationRecord

        if (activationcode && appName) {
            if (isActivationCodeTrue(activationcode)) {
                activationRecord = Activation.findByActivationCode(activationcode)
                if (appName.equals(activationRecord.appName)) {
                    found = true
                }// fine del blocco if
            }// fine del blocco if
        }// fine del blocco if

        return found
    }// fine del metodo

}// fine del service
