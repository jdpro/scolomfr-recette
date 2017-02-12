<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<t:layout>
	<jsp:body>
	
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">Sources</h1>
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->
			<div class="row">
			<table id="sources-table" width="100%"
				class="table table-striped table-bordered table-hover hidden">
			<thead>
            <tr>
                <c:forEach var="columnTitle" items="${headers}">
						<th>${columnTitle}</th>
						
				</c:forEach>
            </tr> 
        </thead>
        <tbody>
			<c:forEach var="line" items="${lines}">
					<tr>
						<td>${line.first}</td>
						<td>${line.second.first}</td>
						<td>${line.second.second}</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
				
			</div>
			<!-- /.row -->
	
        
    </jsp:body>
</t:layout>



