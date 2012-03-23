package com.algos.droidactivator.backend

import org.springframework.dao.DataIntegrityViolationException

class MessageLogController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]


    def index() {
        redirect(action: "list", params: params)
    }


    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [messageLogInstanceList: MessageLog.list(params), messageLogInstanceTotal: MessageLog.count()]
    }


    def create() {
        [messageLogInstance: new MessageLog(params)]
    }


    def save() {
        def messageLogInstance = new MessageLog(params)
        if (!messageLogInstance.save(flush: true)) {
            render(view: "create", model: [messageLogInstance: messageLogInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'messageLog.label', default: 'MessageLog'), messageLogInstance.id])
        redirect(action: "show", id: messageLogInstance.id)
    }


    def show() {
        def messageLogInstance = MessageLog.get(params.id)
        if (!messageLogInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'messageLog.label', default: 'MessageLog'), params.id])
            redirect(action: "list")
            return
        }

        [messageLogInstance: messageLogInstance]
    }


    def edit() {
        def messageLogInstance = MessageLog.get(params.id)
        if (!messageLogInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'messageLog.label', default: 'MessageLog'), params.id])
            redirect(action: "list")
            return
        }

        [messageLogInstance: messageLogInstance]
    }


    def update() {
        def messageLogInstance = MessageLog.get(params.id)
        if (!messageLogInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'messageLog.label', default: 'MessageLog'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (messageLogInstance.version > version) {
                messageLogInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'messageLog.label', default: 'MessageLog')] as Object[],
                        "Another user has updated this MessageLog while you were editing")
                render(view: "edit", model: [messageLogInstance: messageLogInstance])
                return
            }
        }

        messageLogInstance.properties = params

        if (!messageLogInstance.save(flush: true)) {
            render(view: "edit", model: [messageLogInstance: messageLogInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'messageLog.label', default: 'MessageLog'), messageLogInstance.id])
        redirect(action: "show", id: messageLogInstance.id)
    }


    def delete() {
        def messageLogInstance = MessageLog.get(params.id)
        if (!messageLogInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'messageLog.label', default: 'MessageLog'), params.id])
            redirect(action: "list")
            return
        }

        try {
            messageLogInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'messageLog.label', default: 'MessageLog'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'messageLog.label', default: 'MessageLog'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
