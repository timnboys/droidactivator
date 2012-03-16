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
      } else {
        // Failure: send back Failure flag.
        response.setHeader('success', 'false')

        // Failure: Failure code.
        response.setHeader("failurecode", '5')
      }// fine del blocco if-else
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

    if (uniqueid && activationcode) {

      // First, the Installation record is searched by Unique ID.
      activationRecord = this.findByUniqueId(uniqueid)

      if (activationRecord) {

        // Found by Unique Id. This is a renewal.
        // Check the Activation code.
        if (activationRecord.activationCode.equals(activationcode)) {

          // Success: set the Activation flag to true
          activationRecord.active = true
          activationRecord.save(flush: true)

          // put the record bundle in response
          putBundleInResponse(activationRecord, response)
          response.setHeader('success', 'true')
          risposta = 'OK'

        } else {

          // activation code wrong
          response.setHeader('success', 'false')
          response.setHeader("failurecode", '1')

        }

      } else {

        // Activation Record not found by Unique Id, search by User Id + App Name.
        String appName = request.getHeader('appname')
        activationRecord = Activation.findByUserIDAndAppName(userid, appName)

        if (activationRecord) {

          // record found by User Id + AppName
          // this is a new activation: check the activation code
          if (activationRecord.activationCode.equals(activationcode)) {

            // Success: set the Activation flag to true and register Unique Id
            activationRecord.active = true
            activationRecord.uniqueID = uniqueid
            activationRecord.save(flush: true)

            // put data bundle in response
            putBundleInResponse(activationRecord, response)

            // send back Success flag
            response.setHeader('success', 'true')
            risposta = 'OK'

          } else {

            // activationcode wrong
            response.setHeader("failurecode", '1')
            response.setHeader('success', 'false')

          }// fine del blocco if-else

        } else {

          // Record not found by User Id + AppName
          // try by user id only
          activationRecord = Activation.findByUserID(userid)
          if (activationRecord) {
            // found by user id but not appName:
            // there is an Activation Record for the user
            // but it relates to another app
            response.setHeader('success', 'false')
            response.setHeader("failurecode", '2')
          } else {
            // activation record not found
            // this is an attempt to activate the installation
            // before purchase data is added to the backend.
            response.setHeader('success', 'false')
            response.setHeader("failurecode", '3')
          }

        }// fine del blocco if-else

      }// fine del blocco if-else

    } else {

      // Missing uniqueid or activationcode in the activation request
      response.setHeader('success', 'false')
      response.setHeader("failurecode", '4')

    }// fine del blocco if-else

    return risposta
  }// fine del metodo

  /**
   * The backend receives the update request along with the following parameters:
   *
   * @param uniqueid - the cached Unique Id
   * @param userid - the cached User Id
   */
  public String updateRequest(HttpServletRequest request, HttpServletResponse response) {
    String risposta = 'FAILURE'
    Activation activationRecord
    String uniqueid = request.getHeader('uniqueid')

    if (uniqueid) {

      // The Activation record is searched by Unique Id.
      activationRecord = this.findByUniqueId(uniqueid)

      if (activationRecord) {

        // Activation Record found by Unique Id
        // This is the standard situation

        // put bundle in response and return success
        putBundleInResponse(activationRecord, response)
        response.setHeader('success', 'true')
        risposta = "OK"

      } else {

        // Activation Record not found by Unique Id
        // search it by User Id
        String userid = request.getHeader('userid')
        activationRecord = this.findByUserID(userid)

        if (activationRecord) {      // Activation Record found by User Id

          // turn off activation flag and reset Unique Id
          activationRecord.active = false
          activationRecord.uniqueID = ""
          activationRecord.save(flush: true)

          // put bundle in response and return success
          putBundleInResponse(activationRecord, response)
          response.setHeader('success', 'true')
          risposta = "OK"

        } else {           // Activation Record not found

          // return failure
          response.setHeader('success', 'false')

        }

      }

    } else {

      // Unique Id not provided in Update request
      // send back Failure flag and code
      response.setHeader('success', 'false')
      response.setHeader("failurecode", '5')

    }// fine del blocco if-else

    return risposta

  }// fine del metodo

/**
 * Puts activation record data in a response
 * @param record the Activation record
 * @param response the response where to put data
 */
  private void putBundleInResponse(Activation record, HttpServletResponse response) {

    // activation flag
    if (record.active) {
      response.setHeader('activated', 'true')
    } else {
      response.setHeader('activated', 'false')
    }// fine del blocco if-else

    // level
    response.setHeader('level', record.level.toString())

    // expiration
    // Success: Expiration Date
    if (record.expiration) {
      response.setHeader('expiration', record.expiration.getTime().toString())
    }// fine del blocco if

  }

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

}// fine del service
