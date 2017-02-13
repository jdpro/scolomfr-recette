<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@tag description="Page layout" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set value="/sources" var="sources" />
<spring:url value="${sources}/format" var="sourcesbyFormat" />
<spring:url value="${sources}/version" var="sourcesbyVersion" />

<li><a href="sources"><i class="fa fa-file-code-o fa-fw"></i>
		Sources<span class="fa arrow"></span></a>
	<ul class="nav nav-second-level">
		<li><a href="sources/version">Versions <span class="fa arrow"></span></a>
			<ul class="nav nav-third-level">
				<c:forEach items="${versions}" var="version">
					<%
						//Mandatory trailing slash for parameters with dots
					%>
					<li><a href="${sourcesbyVersion}/${version}/">${version}</a></li>
				</c:forEach>

			</ul> <!-- /.nav-third-level --></li>
		<li><a href="sources/formats">Formats <span class="fa arrow"></span></a>
			<ul class="nav nav-third-level">
				<c:forEach items="${formats}" var="format">

					<li><a href="${sourcesbyFormat}/${format}">${format}</a></li>

				</c:forEach>
			</ul>
	</ul> <!-- /.nav-second-level --></li>

