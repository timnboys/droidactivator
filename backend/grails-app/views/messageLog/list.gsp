<%@ page import="com.algos.droidactivator.backend.MessageLog" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'messageLog.label', default: 'MessageLog')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<a href="#list-messageLog" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="list-messageLog" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table>
        <thead>
        <tr>

            <g:sortableColumn property="language" title="${message(code: 'messageLog.language.label', default: 'Language')}"/>

            <th><g:message code="messageLog.type.label" default="Type"/></th>

            <g:sortableColumn property="appName" title="${message(code: 'messageLog.appName.label', default: 'App Name')}"/>

            <g:sortableColumn property="userID" title="${message(code: 'messageLog.userID.label', default: 'User ID')}"/>

            <g:sortableColumn property="sent" title="${message(code: 'messageLog.sent.label', default: 'Sent')}"/>

            <g:sortableColumn property="received" title="${message(code: 'messageLog.received.label', default: 'Received')}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${messageLogInstanceList}" status="i" var="messageLogInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show" id="${messageLogInstance.id}">${fieldValue(bean: messageLogInstance, field: "language")}</g:link></td>

                <td>${fieldValue(bean: messageLogInstance, field: "type")}</td>

                <td>${fieldValue(bean: messageLogInstance, field: "appName")}</td>

                <td>${fieldValue(bean: messageLogInstance, field: "userID")}</td>

                <td><g:formatDate date="${messageLogInstance.sent}"/></td>

                <td><g:formatBoolean boolean="${messageLogInstance.received}"/></td>

            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <g:paginate total="${messageLogInstanceTotal}"/>
    </div>
</div>
</body>
</html>
