<%@tag description="Page layout" pageEncoding="UTF-8"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<li class="dropdown"><a class="dropdown-toggle"
	data-toggle="dropdown" href="#"> <i class="fa fa-flag fa-fw"></i> <i
		class="fa fa-caret-down"></i>
</a>

	<ul class="dropdown-menu dropdown-language">
		<li data-lang="fr_FR"><a href="#"><i class="fa fa-flag fa-fw"></i>
				<spring:message code="layout.lang.fr"></spring:message></a></li>
		<li data-lang="en_EN"><a href="#"><i class="fa fa-flag fa-fw"></i>
				<spring:message code="layout.lang.en"></spring:message></a></li>
	</ul> <!-- /.dropdown-language --></li>