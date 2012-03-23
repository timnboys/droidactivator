package com.algos.droidactivator.backend

import java.text.SimpleDateFormat

class Activation {

    // date format
    private static SimpleDateFormat SDF = new SimpleDateFormat('d MMM yy')

    // internal fields name (random order)
    def String appName
    def boolean paid = false
    def int amount
    def boolean active = false
    def boolean trackingOnly = false
    def String uniqueID = ''
    def int level = 0
    def String userID
    def Date expiration
    def String activationCode = ''
    def String userName = ''
    def String userAddress = ''
    def String userCategory = ''
    def String userCurrentMail = ''

    // differents constraints for fields
    // effective order for showing columns on the list
    static constraints = {
        appName(nullable: false, blank: false)
        paid(nullable: false)
        amount(nullable: true, blank: true)
        active(nullable: false)
        trackingOnly(nullable: false)
        uniqueID(nullable: true, blank: true, editable: false)
        level(nullable: false, blank: false)
        userID(nullable: false, blank: false, email: true)
        expiration(nullable: true, formatoData: SDF)
        activationCode(nullable: true, blank: true)
        userName(nullable: true, blank: true)
        userCategory(nullable: true, blank: true)
        userCurrentMail(nullable: true, blank: true)
    }

    static mapping = {
        // le sottoclassi usano tavole specifiche
        tablePerHierarchy false
    }

}// end of domain class
