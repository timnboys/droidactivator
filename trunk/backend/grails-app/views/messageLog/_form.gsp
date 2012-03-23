<%@ page import="com.algos.droidactivator.backend.MessageLog" %>



<div class="fieldcontain ${hasErrors(bean: messageLogInstance, field: 'language', 'error')} required">
    <label for="language">
        <g:message code="messageLog.language.label" default="Language"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="language" required="" value="${messageLogInstance?.language}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: messageLogInstance, field: 'type', 'error')} required">
    <label for="type">
        <g:message code="messageLog.type.label" default="Type"/>
        <span class="required-indicator">*</span>
    </label>
    <g:select id="type" name="type.id" from="${com.algos.droidactivator.backend.MessageType.list()}" optionKey="id" required="" value="${messageLogInstance?.type?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: messageLogInstance, field: 'appName', 'error')} required">
    <label for="appName">
        <g:message code="messageLog.appName.label" default="App Name"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="appName" required="" value="${messageLogInstance?.appName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: messageLogInstance, field: 'userID', 'error')} required">
    <label for="userID">
        <g:message code="messageLog.userID.label" default="User ID"/>
        <span class="required-indicator">*</span>
    </label>
    <g:field type="email" name="userID" required="" value="${messageLogInstance?.userID}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: messageLogInstance, field: 'sent', 'error')} required">
    <label for="sent">
        <g:message code="messageLog.sent.label" default="Sent"/>
        <span class="required-indicator">*</span>
    </label>
    <g:datePicker name="sent" precision="day" value="${messageLogInstance?.sent}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: messageLogInstance, field: 'received', 'error')} ">
    <label for="received">
        <g:message code="messageLog.received.label" default="Received"/>

    </label>
    <g:checkBox name="received" value="${messageLogInstance?.received}"/>
</div>

