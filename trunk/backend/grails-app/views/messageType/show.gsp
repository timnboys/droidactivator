<%@ page import="com.algos.droidactivator.backend.MessageType" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'messageType.label', default: 'MessageType')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-messageType" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-messageType" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list messageType">

        <g:if test="${messageTypeInstance?.language}">
            <li class="fieldcontain">
                <span id="language-label" class="property-label"><g:message code="messageType.language.label" default="Language"/></span>

                <span class="property-value" aria-labelledby="language-label"><g:fieldValue bean="${messageTypeInstance}" field="language"/></span>

            </li>
        </g:if>

        <g:if test="${messageTypeInstance?.summary}">
            <li class="fieldcontain">
                <span id="summary-label" class="property-label"><g:message code="messageType.summary.label" default="Summary"/></span>

                <span class="property-value" aria-labelledby="summary-label"><g:fieldValue bean="${messageTypeInstance}" field="summary"/></span>

            </li>
        </g:if>

        <g:if test="${messageTypeInstance?.body}">
            <li class="fieldcontain">
                <span id="body-label" class="property-label"><g:message code="messageType.body.label" default="Body"/></span>

                <span class="property-value" aria-labelledby="body-label"><g:fieldValue bean="${messageTypeInstance}" field="body"/></span>

            </li>
        </g:if>

    </ol>
    <g:form>
        <fieldset class="buttons">
            <g:hiddenField name="id" value="${messageTypeInstance?.id}"/>
            <g:link class="edit" action="edit" id="${messageTypeInstance?.id}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
            <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
