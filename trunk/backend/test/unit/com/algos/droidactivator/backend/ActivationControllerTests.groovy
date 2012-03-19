package com.algos.droidactivator.backend

import grails.test.mixin.Mock
import grails.test.mixin.TestFor

@TestFor(ActivationController)
@Mock(Activation)
class ActivationControllerTests {


    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }


    void testIndex() {
        controller.index()
        assert "/activation/list" == response.redirectedUrl
    }


    void testList() {

        def model = controller.list()

        assert model.activationInstanceList.size() == 0
        assert model.activationInstanceTotal == 0
    }


    void testCreate() {
        def model = controller.create()

        assert model.activationInstance != null
    }


    void testSave() {
        controller.save()

        assert model.activationInstance != null
        assert view == '/activation/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/activation/show/1'
        assert controller.flash.message != null
        assert Activation.count() == 1
    }


    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/activation/list'


        populateValidParams(params)
        def activation = new Activation(params)

        assert activation.save() != null

        params.id = activation.id

        def model = controller.show()

        assert model.activationInstance == activation
    }


    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/activation/list'


        populateValidParams(params)
        def activation = new Activation(params)

        assert activation.save() != null

        params.id = activation.id

        def model = controller.edit()

        assert model.activationInstance == activation
    }


    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/activation/list'

        response.reset()


        populateValidParams(params)
        def activation = new Activation(params)

        assert activation.save() != null

        // test invalid parameters in update
        params.id = activation.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/activation/edit"
        assert model.activationInstance != null

        activation.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/activation/show/$activation.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        activation.clearErrors()

        populateValidParams(params)
        params.id = activation.id
        params.version = -1
        controller.update()

        assert view == "/activation/edit"
        assert model.activationInstance != null
        assert model.activationInstance.errors.getFieldError('version')
        assert flash.message != null
    }


    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/activation/list'

        response.reset()

        populateValidParams(params)
        def activation = new Activation(params)

        assert activation.save() != null
        assert Activation.count() == 1

        params.id = activation.id

        controller.delete()

        assert Activation.count() == 0
        assert Activation.get(activation.id) == null
        assert response.redirectedUrl == '/activation/list'
    }
}
