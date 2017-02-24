<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:layout>
	<jsp:body>

        <div class="row">
            <div class="col-md-4 col-md-offset-4">
                <div class="login-panel panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">
							<spring:message code="login.signin" />
						</h3>
                    </div>
                    <div class="panel-body">
                        <form name='loginForm'
							action="<spring:url value='/login' />" method='POST'>
                            <fieldset>
                                <div class="form-group">
                                    <input class="form-control"
										placeholder='<spring:message code="login.username" />'
										name="username" autofocus="" type="text">
                                </div>
                                <div class="form-group">
                                    <input class="form-control"
										placeholder="<spring:message code="login.password" />"
										name="password" value="" type="password">
                                </div>
                                <input type="hidden"
									name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <!-- Change this to a button or input when using this as a form -->
                                <input type="submit"
									value="<spring:message code="login.connect" />" />
                            </fieldset>
                        </form>
                    </div>
                </div>
            </div>
        </div>
   </jsp:body>
</t:layout>