package com.algos.droidactivator.backend

import java.text.SimpleDateFormat

class MessageLog {

    // date format
    private static SimpleDateFormat SDF = new SimpleDateFormat('d MMM yy')

    // internal fields name (random order)
    def String language = 'it'
    def MessageType type
    def String appName
    def String userID
    def Date sent = new Date()
    def boolean received = false

    // differents constraints for fields
    // effective order for showing columns on the list
    static constraints = {
        language(nullable: false, blank: false)
        type(nullable: false, blank: false)
        appName(nullable: false, blank: false)
        userID(nullable: false, blank: false, email: true)
        sent(nullable: false, formatoData: SDF)
        received()
    }

    static mapping = {
        tablePerHierarchy false
        language type: 'text'
        appName type: 'text'
        userID type: 'text'
    }

    def beforeInsert = {
    } // end of def beforeInsert

}// end of domain class
