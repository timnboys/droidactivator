<%@ page import="com.algos.droidactivator.backend.MessageType" %>



<div class="fieldcontain ${hasErrors(bean: messageTypeInstance, field: 'language', 'error')} required">
    <label for="language">
        <g:message code="messageType.language.label" default="Language"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="language" required="" value="${messageTypeInstance?.language}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: messageTypeInstance, field: 'summary', 'error')} required">
    <label for="summary">
        <g:message code="messageType.summary.label" default="Summary"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="summary" required="" value="${messageTypeInstance?.summary}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: messageTypeInstance, field: 'body', 'error')} ">
    <label for="body">
        <g:message code="messageType.body.label" default="Body"/>

    </label>
    <g:textArea name="body" cols="40" rows="5" value="${messageTypeInstance?.body}"/>
</div>

