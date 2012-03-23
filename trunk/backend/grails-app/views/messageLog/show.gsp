<%@ page import="com.algos.droidactivator.backend.MessageLog" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'messageLog.label', default: 'MessageLog')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-messageLog" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-messageLog" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list messageLog">

        <g:if test="${messageLogInstance?.language}">
            <li class="fieldcontain">
                <span id="language-label" class="property-label"><g:message code="messageLog.language.label" default="Language"/></span>

                <span class="property-value" aria-labelledby="language-label"><g:fieldValue bean="${messageLogInstance}" field="language"/></span>

            </li>
        </g:if>

        <g:if test="${messageLogInstance?.type}">
            <li class="fieldcontain">
                <span id="type-label" class="property-label"><g:message code="messageLog.type.label" default="Type"/></span>

                <span class="property-value" aria-labelledby="type-label"><g:link controller="messageType" action="show" id="${messageLogInstance?.type?.id}">${messageLogInstance?.type?.encodeAsHTML()}</g:link></span>

            </li>
        </g:if>

        <g:if test="${messageLogInstance?.appName}">
            <li class="fieldcontain">
                <span id="appName-label" class="property-label"><g:message code="messageLog.appName.label" default="App Name"/></span>

                <span class="property-value" aria-labelledby="appName-label"><g:fieldValue bean="${messageLogInstance}" field="appName"/></span>

            </li>
        </g:if>

        <g:if test="${messageLogInstance?.userID}">
            <li class="fieldcontain">
                <span id="userID-label" class="property-label"><g:message code="messageLog.userID.label" default="User ID"/></span>

                <span class="property-value" aria-labelledby="userID-label"><g:fieldValue bean="${messageLogInstance}" field="userID"/></span>

            </li>
        </g:if>

        <g:if test="${messageLogInstance?.sent}">
            <li class="fieldcontain">
                <span id="sent-label" class="property-label"><g:message code="messageLog.sent.label" default="Sent"/></span>

                <span class="property-value" aria-labelledby="sent-label"><g:formatDate date="${messageLogInstance?.sent}"/></span>

            </li>
        </g:if>

        <g:if test="${messageLogInstance?.received}">
            <li class="fieldcontain">
                <span id="received-label" class="property-label"><g:message code="messageLog.received.label" default="Received"/></span>

                <span class="property-value" aria-labelledby="received-label"><g:formatBoolean boolean="${messageLogInstance?.received}"/></span>

            </li>
        </g:if>

    </ol>
    <g:form>
        <fieldset class="buttons">
            <g:hiddenField name="id" value="${messageLogInstance?.id}"/>
            <g:link class="edit" action="edit" id="${messageLogInstance?.id}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
            <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
