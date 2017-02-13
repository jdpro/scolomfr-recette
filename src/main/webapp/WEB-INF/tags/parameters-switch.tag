<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@tag description="Page layout" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ attribute name="tagname" required="false" type="String"%>
<c:choose>
	<c:when test="${tagname == 'format'}">
		<t:format parameterName="${tagname}" label="Format testé" />
	</c:when>
	<c:when test="${tagname == 'format2'}">
		<t:format parameterName="${tagname}" label="Second format testé" />
	</c:when>
	<c:when test="${tagname == 'version'}">
		<t:version parameterName="${tagname}" label="Version testée" />
	</c:when>
	<c:when test="${tagname == 'version2'}">
		<t:version parameterName="${tagname}" label="Seconde version testée" />
	</c:when>
	<c:when test="${tagname == 'vocabulary'}">
		<t:vocabulary parameterName="${tagname}" label="Vocabulaire testé" />
	</c:when>
	<c:when test="${tagname == 'vocabulary2'}">
		<t:vocabulary parameterName="${tagname}"
			label="Second vocabulaire testé" />
	</c:when>
</c:choose>