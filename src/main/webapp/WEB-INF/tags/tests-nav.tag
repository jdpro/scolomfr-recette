<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@tag description="Page layout" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<spring:url value="/tests" var="tests" />
<li><a href="#"><i class="fa fa-check fa-fw"></i> Tests<span
		class="fa arrow"></span></a>
	<ul class="nav nav-second-level">
		<li><a href="#">1. Cohérence interne des vocabulaires <span
				class="fa arrow"></span></a>
			<ul class="nav nav-third-level">
				<li><a href="#">1.2 - Absence de doublons de concepts<span
						class="fa arrow"></span></a>
					<ul class="nav nav-fourth-level">
						<li><a
							href="${tests}/absence_de_doublons_de_concepts/skos/1.2.1">1.2.1
								- Skos</a></li>
					</ul>
					<!-- /.nav-fourth-level --></li>
			</ul> <!-- /.nav-third-level --></li>
	</ul> <!-- /.nav-second-level --></li>

