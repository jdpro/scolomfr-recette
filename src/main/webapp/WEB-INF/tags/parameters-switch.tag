<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@tag description="Page layout" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ attribute name="tagname" required="false" type="String"%>
<c:choose>
	<c:when test="${tagname == 'format'}">
		<t:format parameterName="${tagname}">
			<jsp:attribute name="label">
				<spring:message code="test.tested_format"></spring:message> </jsp:attribute>
		</t:format>
	</c:when>
	<c:when test="${tagname == 'format2'}">
		<t:format parameterName="${tagname}"><jsp:attribute
				name="label">
				<spring:message code="test.tested_format2"></spring:message> </jsp:attribute></t:format>
	</c:when>
	<c:when test="${tagname == 'version'}">
		<t:version parameterName="${tagname}"><jsp:attribute
				name="label">
				<spring:message code="test.tested_version"></spring:message> </jsp:attribute></t:version>
	</c:when>
	<c:when test="${tagname == 'version2'}">
		<t:version parameterName="${tagname}"><jsp:attribute
				name="label">
				<spring:message code="test.tested_version2"></spring:message> </jsp:attribute></t:version>
	</c:when>
	<c:when test="${tagname == 'vocabulary'}">
		<t:vocabulary parameterName="${tagname}"><jsp:attribute
				name="label">
				<spring:message code="test.tested_vocabulary"></spring:message> </jsp:attribute></t:vocabulary>
	</c:when>
	<c:when test="${tagname == 'vocabulary2'}">
		<t:vocabulary parameterName="${tagname}"><jsp:attribute
				name="label">
				<spring:message code="test.tested_vocabulary2"></spring:message> </jsp:attribute></t:vocabulary>
	</c:when>
	<c:when test="${tagname == 'skostype'}">
		<t:skostype parameterName="${tagname}"><jsp:attribute
				name="label">
				<spring:message code="test.tested_skostype"></spring:message> </jsp:attribute></t:skostype>
	</c:when>
</c:choose>