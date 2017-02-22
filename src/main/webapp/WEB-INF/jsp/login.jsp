<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:layout>
	<jsp:body>

        <div class="row">
            <div class="col-md-4 col-md-offset-4">
                <div class="login-panel panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">Please Sign In</h3>
                    </div>
                    <div class="panel-body">
                        <form name='loginForm'
							action="<spring:url value='/login' />" method='POST'>
                            <fieldset>
                                <div class="form-group">
                                    <input class="form-control"
										placeholder="Username" name="username" autofocus=""
										type="text">
                                </div>
                                <div class="form-group">
                                    <input class="form-control"
										placeholder="Password" name="password" value=""
										type="password">
                                </div>
                                <input type="hidden"
									name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <!-- Change this to a button or input when using this as a form -->
                                <input type="submit" value="Connect" />
                            </fieldset>
                        </form>
                    </div>
                </div>
            </div>
        </div>
   </jsp:body></t:layout>