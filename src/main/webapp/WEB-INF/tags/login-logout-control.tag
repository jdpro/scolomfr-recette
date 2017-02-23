<%@tag description="Page layout" pageEncoding="UTF-8"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<spring:url value="/login" var="login" />
<spring:url value="/logout" var="logout" />
<li class="dropdown"><a class="dropdown-toggle"
	data-toggle="dropdown" href="#"> <i class="fa fa-user fa-fw"></i> <i
		class="fa fa-caret-down"></i>
</a>
	<ul class="dropdown-menu dropdown-user">
		<sec:authorize access="isAnonymous()">
			<li><a href="${login}"><i class="fa fa-sign-in fa-fw"></i> <spring:message
						code="user.login"></spring:message></a></li>
		</sec:authorize>
		<sec:authorize access="isAuthenticated()">
			<li><a href="${logout}"><i class="fa fa-sign-out fa-fw"></i>
					<spring:message code="user.logout"></spring:message> </a></li>
		</sec:authorize>


	</ul> <!-- /.dropdown-user --></li>