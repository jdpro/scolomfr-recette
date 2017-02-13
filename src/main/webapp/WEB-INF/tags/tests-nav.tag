<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@tag description="Page layout" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<spring:url value="/tests" var="tests" />
<li><a href="#"><i class="fa fa-check fa-fw"></i> Tests<span
		class="fa arrow"></span></a>
	<ul class="nav nav-second-level">
		<c:forEach items="${testsStructure}" var="requirement">
			<li><a href="#">${requirement.value.index} -
					${requirement.value.label}<span class="fa arrow"></span>
			</a>
				<ul class="nav nav-third-level">
					<c:forEach items="${requirement.value.folders}" var="folder">
						<li><a href="#">${folder.value.index} -
								${folder.value.label}<span class="fa arrow"></span>
						</a>
							<ul class="nav nav-fourth-level">
								<c:forEach items="${folder.value.tests}" var="test">
									<li><a
										href="${tests}/${requirement.key}/${folder.key}/${test.key}/${test.value.index}/">${test.value.index}
											- ${test.value.label}</a></li>
								</c:forEach>
							</ul> <!-- /.nav-fourth-level --></li>
					</c:forEach>
				</ul> <!-- /.nav-third-level --></li>
		</c:forEach>
	</ul> <!-- /.nav-second-level --></li>

