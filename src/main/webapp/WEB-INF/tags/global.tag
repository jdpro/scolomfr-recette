<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@tag description="Page layout" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ attribute name="parameterName" required="true" type="String"%>
<%@ attribute name="label" required="true" type="String"%>
<%@ attribute name="label1" required="true" type="String"%>
<%@ attribute name="label2" required="true" type="String"%>
<div class="form-group col-lg-4">
	<label>${label}</label>
	<div class="radio">
		<label> <input id="global-radio" type="radio" checked="checked"
			value="global" name="${parameterName}"> ${label1}
		</label> <label> <input id="special-radio" type="radio" 
			value="special" name="${parameterName}"> ${label2}
		</label>
	</div>

</div>