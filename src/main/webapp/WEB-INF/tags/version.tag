<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@tag description="Page layout" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ attribute name="parameterName" required="true" type="String"%>
<%@ attribute name="label" required="true" type="String"%>
<div class="form-group  col-lg-4">
	<label>${label}</label> <select class="form-control"
		name="${parameterName}">
		<c:forEach items="${versions}" var="version">
			<option
				<c:if test="${version==defaultVersion}">selected="selected"</c:if>>${version}</option>
		</c:forEach>
	</select>
</div>