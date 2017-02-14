<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<t:layout>
	<jsp:body>
	
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">Test ${testCaseIndex} | ${folderLabel} / ${testCaseLabel}  </h1>
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->
			<div class="row">
				<c:choose>
				<c:when test="${implemented}">
					<div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            Test implémenté : ${implementation}
                        </div>
                        <div class="panel-body">
                            <div class="row">
                                    <form role="form" method="POST"
										action=".">
                                      
                                        <c:forEach items="${parameters}"
											var="parameter">
						<t:parameters-switch tagname="${parameter}" />
						</c:forEach>
                                        <div class="col-lg-12">
											<button class="btn btn-default" type="submit">Exécuter</button>
										</div>
                                        
                                    </form>
                                
                                
                            </div>
                            <!-- /.row (nested) -->
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                </div>
				
				</c:when>
				<c:otherwise>
					<span class="label label-default">Test non implémenté : </span> </c:otherwise>
				</c:choose>
				
			</div>
			<!-- /.row -->
	
        
    </jsp:body>
</t:layout>






<



