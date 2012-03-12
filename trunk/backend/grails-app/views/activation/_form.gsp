<%@ page import="com.algos.droidactivator.backend.Activation" %>



<div class="fieldcontain ${hasErrors(bean: activationInstance, field: 'applicationName', 'error')} ">
    <label for="applicationName">
        <g:message code="activation.applicationName.label" default="Application Name"/>

    </label>
    <g:textField name="applicationName" value="${activationInstance?.applicationName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activationInstance, field: 'paid', 'error')} ">
    <label for="paid">
        <g:message code="activation.paid.label" default="Paid"/>

    </label>
    <g:checkBox name="paid" value="${activationInstance?.paid}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activationInstance, field: 'amount', 'error')} required">
    <label for="amount">
        <g:message code="activation.amount.label" default="Amount"/>
        <span class="required-indicator">*</span>
    </label>
    <g:field type="number" name="amount" required="" value="${fieldValue(bean: activationInstance, field: 'amount')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activationInstance, field: 'active', 'error')} ">
    <label for="active">
        <g:message code="activation.active.label" default="Active"/>

    </label>
    <g:checkBox name="active" value="${activationInstance?.active}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activationInstance, field: 'level', 'error')} required">
    <label for="level">
        <g:message code="activation.level.label" default="Level"/>
        <span class="required-indicator">*</span>
    </label>
    <g:field type="number" name="level" required="" value="${fieldValue(bean: activationInstance, field: 'level')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activationInstance, field: 'userGoogleMail', 'error')} required">
    <label for="userGoogleMail">
        <g:message code="activation.userGoogleMail.label" default="User Google Mail"/>
        <span class="required-indicator">*</span>
    </label>
    <g:field type="email" name="userGoogleMail" required="" value="${activationInstance?.userGoogleMail}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activationInstance, field: 'expiration', 'error')} ">
    <label for="expiration">
        <g:message code="activation.expiration.label" default="Expiration"/>

    </label>
    <g:datePicker name="expiration" precision="day" value="${activationInstance?.expiration}" default="none" noSelection="['': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activationInstance, field: 'installationID', 'error')} ">
    <label for="installationID">
        <g:message code="activation.installationID.label" default="Installation ID"/>

    </label>
    <g:textField name="installationID" value="${activationInstance?.installationID}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activationInstance, field: 'userID', 'error')} required">
    <label for="userID">
        <g:message code="activation.userID.label" default="User ID"/>
        <span class="required-indicator">*</span>
    </label>
    <g:field type="number" name="userID" required="" value="${fieldValue(bean: activationInstance, field: 'userID')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activationInstance, field: 'userName', 'error')} ">
    <label for="userName">
        <g:message code="activation.userName.label" default="User Name"/>

    </label>
    <g:textField name="userName" value="${activationInstance?.userName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activationInstance, field: 'userCategory', 'error')} ">
    <label for="userCategory">
        <g:message code="activation.userCategory.label" default="User Category"/>

    </label>
    <g:textField name="userCategory" value="${activationInstance?.userCategory}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activationInstance, field: 'userCurrentMail', 'error')} ">
    <label for="userCurrentMail">
        <g:message code="activation.userCurrentMail.label" default="User Current Mail"/>

    </label>
    <g:textField name="userCurrentMail" value="${activationInstance?.userCurrentMail}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: activationInstance, field: 'userAddress', 'error')} ">
    <label for="userAddress">
        <g:message code="activation.userAddress.label" default="User Address"/>

    </label>
    <g:textField name="userAddress" value="${activationInstance?.userAddress}"/>
</div>

