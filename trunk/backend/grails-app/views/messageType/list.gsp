<%@ page import="com.algos.droidactivator.backend.MessageType" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'messageType.label', default: 'MessageType')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<a href="#list-messageType" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="list-messageType" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table>
        <thead>
        <tr>

            <g:sortableColumn property="language" title="${message(code: 'messageType.language.label', default: 'Language')}"/>

            <g:sortableColumn property="summary" title="${message(code: 'messageType.summary.label', default: 'Summary')}"/>

            <g:sortableColumn property="body" title="${message(code: 'messageType.body.label', default: 'Body')}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${messageTypeInstanceList}" status="i" var="messageTypeInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show" id="${messageTypeInstance.id}">${fieldValue(bean: messageTypeInstance, field: "language")}</g:link></td>

                <td>${fieldValue(bean: messageTypeInstance, field: "summary")}</td>

                <td>${fieldValue(bean: messageTypeInstance, field: "body")}</td>

            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <g:paginate total="${messageTypeInstanceTotal}"/>
    </div>
</div>
</body>
</html>
