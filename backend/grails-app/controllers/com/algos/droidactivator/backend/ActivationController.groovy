package com.algos.droidactivator.backend

import org.springframework.dao.DataIntegrityViolationException

class ActivationController {
    // utilizzo di un service con la businessLogic per l'elaborazione dei dati
    // il service viene iniettato automaticamente
    def activationService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]


    def index() {
        redirect(action: "list", params: params)
    }


    def check() {
        def action = request.getHeader('action')
        def appName = request.getHeader('appname')

        if (action && action.equals('checkresponding')) {
            render activationService.connectionRequest(request, response)
        }

        if (action && action.equals('checkidpresent')) {
            render activationService.idRequest(request, response)
        }

        /**
         * The backend receives the activation request whth the params
         * First, the Installation record is searched by Unique ID.
         * If not found, it is searched by User's e-mail address.
         *
         * @param uniqueid - Unique Id (always present)
         * @param userid - User's e-mail address (if provided)
         * @param activationcode - Activation Code (always present)
         */
        if (action && action.equals('activate')) {
            render activationService.activationRequest(request, response)
        }// fine del blocco if

        /**
         * The backend receives the update request along with the following parameters:
         *
         * @param uniqueid - the cached Unique Id
         */
        if (action && action.equals('update')) {
            render activationService.updateRequest(request, response)
        }

        render 'FAILURE'
    }// end of closure


    def list() {
        servletContext.startController = null
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [activationInstanceList: Activation.list(params), activationInstanceTotal: Activation.count()]
    }


    def create() {
        [activationInstance: new Activation(params)]
    }


    def save() {
        def activationInstance = new Activation(params)
        if (!activationInstance.save(flush: true)) {
            render(view: "create", model: [activationInstance: activationInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'activation.label', default: 'Activation'), activationInstance.id])
        redirect(action: "show", id: activationInstance.id)
    }


    def show() {
        def activationInstance = Activation.get(params.id)
        if (!activationInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'activation.label', default: 'Activation'), params.id])
            redirect(action: "list")
            return
        }

        [activationInstance: activationInstance]
    }


    def edit() {
        def activationInstance = Activation.get(params.id)
        if (!activationInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'activation.label', default: 'Activation'), params.id])
            redirect(action: "list")
            return
        }

        [activationInstance: activationInstance]
    }


    def update() {
        def activationInstance = Activation.get(params.id)
        if (!activationInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'activation.label', default: 'Activation'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (activationInstance.version > version) {
                activationInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'activation.label', default: 'Activation')] as Object[],
                        "Another user has updated this Activation while you were editing")
                render(view: "edit", model: [activationInstance: activationInstance])
                return
            }
        }

        activationInstance.properties = params

        if (!activationInstance.save(flush: true)) {
            render(view: "edit", model: [activationInstance: activationInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'activation.label', default: 'Activation'), activationInstance.id])
        redirect(action: "show", id: activationInstance.id)
    }


    def delete() {
        def activationInstance = Activation.get(params.id)
        if (!activationInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'activation.label', default: 'Activation'), params.id])
            redirect(action: "list")
            return
        }

        try {
            activationInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'activation.label', default: 'Activation'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'activation.label', default: 'Activation'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
