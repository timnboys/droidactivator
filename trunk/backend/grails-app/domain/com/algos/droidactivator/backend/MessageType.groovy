package com.algos.droidactivator.backend

class MessageType {

    // internal fields name (random order)
    def String language = 'it'
    def String summary = 'test'
    def String body

    // differents constraints for fields
    // effective order for showing columns on the list
    static constraints = {
        language(nullable: false, blank: false)
        summary(nullable: false, blank: false)
        body(nullable: false, widget: 'textarea')
    }

    static mapping = {
        tablePerHierarchy false
        language type: 'text'
        summary type: 'text'
        body type: 'text'
    }


    String toString() {
        language + '-' + summary
    } // end of toString

    def beforeInsert = {
    } // end of def beforeInsert

}// end of domain class

