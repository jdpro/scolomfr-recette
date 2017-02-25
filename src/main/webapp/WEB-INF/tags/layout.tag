<%@tag description="Page layout" pageEncoding="UTF-8"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<c:set value="/resources" var="baseResourcesPath" />
<spring:url value="" var="current" />
<spring:url value="/" var="home" />
<spring:url
	value="${baseResourcesPath}/vendor/bootstrap/css/bootstrap.min.css"
	var="bootstrapCss" />
<spring:url
	value="${baseResourcesPath}/vendor/metisMenu/metisMenu.min.css"
	var="metisMenuCss" />
<spring:url value="${baseResourcesPath}/dist/css/sb-admin-2.min.css"
	var="sbAdmin2Css" />
<spring:url value="${baseResourcesPath}/dist/css/recette.min.css"
	var="recetteCss" />
<spring:url
	value="${baseResourcesPath}/vendor/font-awesome/css/font-awesome.min.css"
	var="fontAwesomeCss" />
<spring:url
	value="${baseResourcesPath}/vendor/datatables-plugins/dataTables.bootstrap.css"
	var="datatablesBootstrapCss" />
<spring:url
	value="${baseResourcesPath}/vendor/datatables-responsive/dataTables.responsive.css"
	var="datatablesResponsiveCss" />
<spring:url value="${baseResourcesPath}/vendor/jquery/jquery.min.js"
	var="jQueryJs" />
<spring:url value="${baseResourcesPath}/vendor/js-cookie/js.cookie.js"
	var="jsCookieJs" />
<spring:url
	value="${baseResourcesPath}/vendor/bootstrap/js/bootstrap.min.js"
	var="bootstrapJs" />
<spring:url value="${baseResourcesPath}/vendor/metisMenu/metisMenu.js"
	var="metisMenuJs" />
<spring:url
	value="${baseResourcesPath}/vendor/datatables/js/jquery.dataTables.min.js"
	var="jqueryDatatablesJs" />
<spring:url
	value="${baseResourcesPath}/vendor/datatables-plugins/dataTables.bootstrap.min.js"
	var="bootstrapDatatablesJs" />
<spring:url
	value="${baseResourcesPath}/vendor/datatables-responsive/dataTables.responsive.js"
	var="responsiveDatatablesJs" />

<spring:url value="${baseResourcesPath}/dist/js/sb-admin-2.js"
	var="sbAdmin2Js" />
<spring:url value="${baseResourcesPath}/dist/js/recette.js"
	var="recetteJs" />

<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<sec:csrfMetaTags />

<title><spring:message code="layout.title" /></title>

<!-- Bootstrap Core CSS -->
<link href="${bootstrapCss}" rel="stylesheet">

<!-- MetisMenu CSS -->
<link href="${metisMenuCss}" rel="stylesheet">



<!-- Morris Charts CSS -->
<link href="${morrisCss}" rel="stylesheet">

<!-- Custom Fonts -->
<link href="${fontAwesomeCss}" rel="stylesheet" type="text/css">

<link href="${datatablesBootstrapCss}" rel="stylesheet">

<!-- DataTables Responsive CSS -->
<link href="${datatablesResponsiveCss}" rel="stylesheet">

<!-- Custom CSS -->
<link href="${sbAdmin2Css}" rel="stylesheet">
<link href="${recetteCss}" rel="stylesheet">
<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body>
	<input type="hidden" value="${pageContext.request.contextPath}"
		id="context-path" />
	<div id="wrapper">

		<!-- Navigation -->
		<nav class="navbar navbar-default navbar-static-top" role="navigation"
			style="margin-bottom: 0">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="${home}"><spring:message
						code="layout.title" />${current}</a>
			</div>
			<!-- /.navbar-header -->

			<ul class="nav navbar-top-links navbar-right">
				<t:lang-control></t:lang-control>
				<c:if test="${logControl }">
					<t:login-logout-control></t:login-logout-control>
				</c:if>
				<!-- /.dropdown -->
			</ul>
			<!-- /.navbar-top-links -->
			<c:if test="${displayContainer}">
				<div class="navbar-default sidebar" role="navigation">
					<div class="sidebar-nav navbar-collapse">
						<ul class="nav hidden" id="side-menu">
							<t:search-nav></t:search-nav>
							<li><a href="${home}"><i class="fa fa-home fa-fw"></i> <spring:message
										code="nav.home"></spring:message></a></li>
							<t:sources-nav></t:sources-nav>
							<t:tests-nav></t:tests-nav>
						</ul>
					</div>
					<!-- /.sidebar-collapse -->
				</div>
				<!-- /.navbar-static-side -->
			</c:if>
		</nav>
		<c:if test="${displayContainer}">
			<div id="page-wrapper"><jsp:doBody /></div>
		</c:if>
		<c:if test="${!displayContainer}">
			<jsp:doBody />
		</c:if>

	</div>
	<!-- /#wrapper -->

	<!-- jQuery -->
	<script src="${jQueryJs}"></script>

	<!-- js-cookie -->
	<script src="${jsCookieJs}"></script>

	<!-- Bootstrap Core JavaScript -->
	<script src="${bootstrapJs}"></script>

	<!-- Metis Menu Plugin JavaScript -->
	<script src="${metisMenuJs}"></script>

	<!-- Datatable -->
	<script src="${jqueryDatatablesJs}"></script>
	<script src="${bootstrapDatatablesJs}"></script>
	<script src=""></script>
	<!-- Custom Theme JavaScript -->
	<script src="${sbAdmin2Js}"></script>
	<script src="${recetteJs}"></script>

</body>

</html>


