<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@tag description="Page layout" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ attribute name="parameterName" required="true" type="String"%>
<%@ attribute name="label" required="true" type="String"%>
<div class="form-group col-lg-4">
	<label>${label}</label>
	<div class="radio">
		<label> <input id="skosxl-radio" type="radio" checked="checked"
			value="skosxl" name="${parameterName}"> SKOS-XL
		</label> <label> <input id="skos-radio" type="radio"
			value="skos" name="${parameterName}"> SKOS
		</label>
	</div>

</div>