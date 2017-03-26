<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set value="/sources" var="sources" />
<spring:url value="${sources}/format" var="sourcesbyFormat" />
<spring:url value="${sources}/version" var="sourcesbyVersion" />
<spring:url value="/tests" var="tests" />

<t:layout>
	<jsp:body>
	
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">
					<spring:message code="layout.title" />
				</h1>
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->
			<div class="row">
			<div class="col-lg-4 col-md-4">                    
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-calendar fa-4x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">3</div>
                                    <div>
									<spring:message code="nav.sources.versions" />
								</div>
                                </div>
                            </div>
                        </div>
					<c:forEach items="${versions}" var="version">
                        <a href="${sourcesbyVersion}/${version}/">
                        
                            <div class="panel-footer">
                                <span class="pull-left">${version}</span>
                                <span class="pull-right"><i
									class="fa fa-arrow-circle-right"></i></span>
                                <div class="clearfix"></div>
                            </div>
                        </a>
					</c:forEach>
                      
                    </div>
                    <div class="panel panel-green">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-file-code-o fa-4x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">7</div>
                                    <div>
									<spring:message code="nav.sources.formats" />
								</div>
                                </div>
                            </div>
                        </div>
					<c:forEach items="${formats}" var="format">
                        <a href="${sourcesbyFormat}/${format}">
                            <div class="panel-footer">
                                <span class="pull-left">${format}</span>
                                <span class="pull-right"><i
									class="fa fa-arrow-circle-right"></i></span>
                                <div class="clearfix"></div>
                            </div>
                        </a>
					</c:forEach>
                    </div>
                </div>
			<div class="col-lg-8 col-md-8">
                    <div class="panel panel-yellow">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-xs-3">
                                    <i class="fa fa-check fa-4x"></i>
                                </div>
                                <div class="col-xs-9 text-right">
                                    <div class="huge">27</div>
                                    <div>
									<spring:message code="nav.tests" />
								</div>
                                </div>
                            </div>
                        </div>
                        <c:forEach items="${testsStructure}"
						var="requirement">
						<c:if test="${requirement.value.folders!=null}">
						<c:forEach items="${requirement.value.folders}" var="folder">
						<c:forEach items="${folder.value.tests}" var="test">
                        <a
										href="${tests}/${requirement.key}/${test.key}/${test.value.index}/">
                            <div class="panel-footer">
                                <span class="pull-left">${test.value.label}</span>
                                <span class="pull-right"><i
												class="fa fa-arrow-circle-right"></i></span>
                                <div class="clearfix"></div>
                            </div>
                        </a>
								</c:forEach>
							</c:forEach>
						</c:if>
						<c:if test="${requirement.value.tests!=null}">
						<c:forEach items="${requirement.value.tests}" var="test">
						<a
									href="${tests}/${requirement.key}/${test.key}/${test.value.index}/">
								<div class="panel-footer">
                                <span class="pull-left">${test.value.label}</span>
                                <span class="pull-right"><i
											class="fa fa-arrow-circle-right"></i></span>
                                <div class="clearfix"></div>
                            </div>
								</a>
						</c:forEach>
						</c:if>
                        </c:forEach>
                    </div>
                </div>
                
                
                
            	
			</div>
			<!-- /.row -->
	
        
    </jsp:body>
</t:layout>



