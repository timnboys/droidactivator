package com.algos.droidactivator.backend

import org.springframework.dao.DataIntegrityViolationException

class ActivationController {

  static allowedMethods = [save: "POST", update: "POST", delete: "POST"]


  def index() {
    redirect(action: "list", params: params)
  }


  def check() {
    boolean cont = true

    def action = request.getHeader('action')
    def appName = request.getHeader('appname')

    if (action) {

      if (action.equals('checkresponding')) {
        response.setHeader("success", "true")
        render 'true'
      }

      if (action.equals('checkidpresent')) {
        def uniqueid = request.getHeader('uniqueid')
        response.setHeader("success", "true")
        render 'true'
      }

      if (action.equals('activate')) {
        def uniqueid = request.getHeader('uniqueid')
        def userid = request.getHeader('userid')
        def activationCode = request.getHeader('activationcode')
        response.setHeader("success", "false")
        response.setHeader("message", "you are not authorized to activate")
        render 'true'
      }


    }



//    def uniqueid = request.getHeader('uniqueid')
//
//    if (action && action.equals('checkresponding')) {
//      response.setHeader("success", "true")
//      render 'true'
//    } else {
//      response.setHeader("success", "true")
//      if (action && action.equals('checkid')) {
//        response.setHeader('action', 'true')
//        response.setHeader('appname', 'appname')
//        response.setHeader('uniqueid', 'true')
//        render 'true'
//      } else {
//        response.setHeader("action", "topolinoz")
//        render 'false'
//      }// fine del blocco if-else
//      render 'false'
//    }// fine del blocco if-else
  }




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
