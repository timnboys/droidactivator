<%@ page import="com.algos.droidactivator.backend.Activation" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'activation.label', default: 'Activation')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-activation" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-activation" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list activation">

        <g:if test="${activationInstance?.appName}">
            <li class="fieldcontain">
                <span id="appName-label" class="property-label"><g:message code="activation.appName.label" default="App Name"/></span>

                <span class="property-value" aria-labelledby="appName-label"><g:fieldValue bean="${activationInstance}" field="appName"/></span>

            </li>
        </g:if>

        <g:if test="${activationInstance?.paid}">
            <li class="fieldcontain">
                <span id="paid-label" class="property-label"><g:message code="activation.paid.label" default="Paid"/></span>

                <span class="property-value" aria-labelledby="paid-label"><g:formatBoolean boolean="${activationInstance?.paid}"/></span>

            </li>
        </g:if>

        <g:if test="${activationInstance?.amount}">
            <li class="fieldcontain">
                <span id="amount-label" class="property-label"><g:message code="activation.amount.label" default="Amount"/></span>

                <span class="property-value" aria-labelledby="amount-label"><g:fieldValue bean="${activationInstance}" field="amount"/></span>

            </li>
        </g:if>

        <g:if test="${activationInstance?.active}">
            <li class="fieldcontain">
                <span id="active-label" class="property-label"><g:message code="activation.active.label" default="Active"/></span>

                <span class="property-value" aria-labelledby="active-label"><g:formatBoolean boolean="${activationInstance?.active}"/></span>

            </li>
        </g:if>

        <g:if test="${activationInstance?.level}">
            <li class="fieldcontain">
                <span id="level-label" class="property-label"><g:message code="activation.level.label" default="Level"/></span>

                <span class="property-value" aria-labelledby="level-label"><g:fieldValue bean="${activationInstance}" field="level"/></span>

            </li>
        </g:if>

        <g:if test="${activationInstance?.userID}">
            <li class="fieldcontain">
                <span id="userID-label" class="property-label"><g:message code="activation.userID.label" default="User ID"/></span>

                <span class="property-value" aria-labelledby="userID-label"><g:fieldValue bean="${activationInstance}" field="userID"/></span>

            </li>
        </g:if>

        <g:if test="${activationInstance?.expiration}">
            <li class="fieldcontain">
                <span id="expiration-label" class="property-label"><g:message code="activation.expiration.label" default="Expiration"/></span>

                <span class="property-value" aria-labelledby="expiration-label"><g:formatDate date="${activationInstance?.expiration}"/></span>

            </li>
        </g:if>

        <g:if test="${activationInstance?.uniqueID}">
            <li class="fieldcontain">
                <span id="uniqueID-label" class="property-label"><g:message code="activation.uniqueID.label" default="Unique ID"/></span>

                <span class="property-value" aria-labelledby="uniqueID-label"><g:fieldValue bean="${activationInstance}" field="uniqueID"/></span>

            </li>
        </g:if>

        <g:if test="${activationInstance?.userName}">
            <li class="fieldcontain">
                <span id="userName-label" class="property-label"><g:message code="activation.userName.label" default="User Name"/></span>

                <span class="property-value" aria-labelledby="userName-label"><g:fieldValue bean="${activationInstance}" field="userName"/></span>

            </li>
        </g:if>

        <g:if test="${activationInstance?.userCategory}">
            <li class="fieldcontain">
                <span id="userCategory-label" class="property-label"><g:message code="activation.userCategory.label" default="User Category"/></span>

                <span class="property-value" aria-labelledby="userCategory-label"><g:fieldValue bean="${activationInstance}" field="userCategory"/></span>

            </li>
        </g:if>

        <g:if test="${activationInstance?.userCurrentMail}">
            <li class="fieldcontain">
                <span id="userCurrentMail-label" class="property-label"><g:message code="activation.userCurrentMail.label" default="User Current Mail"/></span>

                <span class="property-value" aria-labelledby="userCurrentMail-label"><g:fieldValue bean="${activationInstance}" field="userCurrentMail"/></span>

            </li>
        </g:if>

        <g:if test="${activationInstance?.userAddress}">
            <li class="fieldcontain">
                <span id="userAddress-label" class="property-label"><g:message code="activation.userAddress.label" default="User Address"/></span>

                <span class="property-value" aria-labelledby="userAddress-label"><g:fieldValue bean="${activationInstance}" field="userAddress"/></span>

            </li>
        </g:if>

    </ol>
    <g:form>
        <fieldset class="buttons">
            <g:hiddenField name="id" value="${activationInstance?.id}"/>
            <g:link class="edit" action="edit" id="${activationInstance?.id}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
            <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
