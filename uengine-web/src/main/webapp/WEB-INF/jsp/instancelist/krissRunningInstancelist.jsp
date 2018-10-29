<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/WEB-INF/include/include-header_resource.jspf"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<style>
.form-control {font-size:12px;height:28px; padding:0px	}
/*
.ui-jqgrid-sortable {font-size: 12px;text-align: left}
.ui-jqgrid-labels .ui-th-column{border-right-width: 0px;  }
.ui-jqgrid tr.ui-row-ltr td {border-right-width: 0px;}
.ui-widget-content {background:#FFFFFF}
.ui-state-default, .ui-widget-content .ui-state-default, .ui-widget-header .ui-state-default {background:#FFFFFF}
*/
</style>
<script>

	var totalCount = 0;
	var pageSize = 15;
	var pagerSize = 10;
	var pageNo = ${pageNo};
	
	var dateFormat = "yy-mm-dd";
	
	$(document).ready(function() {
		$("#folderName").selectmenu({
			width: "auto"
		});
		$("#searchOption").selectmenu({
			width: "auto"
		});
		$("#status").selectmenu({
			width: "auto"
		});
		var from = $("#fromDate").datepicker({
			defaultDate: "+1w",
			changeMonth: true,
			numberOfMonths: 3,
			dateFormat: dateFormat,
			resional: "ko-KR"
		}).on("change", function(){
			to.datepicker("option","minDate", $(this).val());
		});
		var to = $("#toDate").datepicker({
			defaultDate: "+1w",
			changeMonth: true,
			numberOfMonths: 3,
			dateFormat: dateFormat
		}).on("change", function(){
			from.datepicker("option","maxDate", $(this).val());
		});
		
		
		fn_getRunningInstaceList("${command.pageNo}");
		
	});
	
	var fn_callPaging= function(){
		$("#totalCount").val(totalCount);
		$("#pageSize").val(pageSize);
		$("#pagerSize").val(pagerSize);
		$("#pageNo").val(pageNo);
		if ( totalCount == 0 ) {
			pagerFrame.attr("src","");
		} else {
			$("#pagerForm").attr("action","<c:url value='/common/paging.do' />");
			$("#pagerForm").submit();
		}
	}
	
	var fn_getMyRunningInstanceListCount = function() {
		$.ajax({
			type : "GET",
			url : "<c:url value='/instancelist/count/kriss/running/instance' />",
			cache : false,
			dataType : "JSON",
			success : function(data) {
				$("#totalCountSpan").html(data);	
				totalCount = data;
				fn_callPaging();
			},
			error : function(data, msg) {
				if(window.console){
					console.log(data);
					console.log(msg);
				}
			}
		});
	}
	
	var fn_getRunningInstaceList = function(page) {
		$("#pagerFrame").attr("src","");
		$.ajax({
			type : "GET",
			url : "<c:url value='/instancelist/list/kriss/running/instance/' />"+page+"/"+pageSize,
			cache : false,
			dataType : "JSON",
			beforeSend: function(){
				$("#loadingDiv").show();
				$("#contentTbody").html("");
			},
			complete: function(){
				$("#loadingDiv").hide();
			},
			success : function(data) {
				pageNo = page;
				fn_getMyRunningInstanceListCount();
			    var contentTbody = $("#contentTbody");
			    for ( var i = 0; i < data.length; i++ ){
			    	var tr = $("<tr></tr>")
			    	//folderName
			    	var folderNameChild = $("<th></th>").attr("scope","row").attr("style","vertical-align: middle; padding:2px;");
			    	var folderNameFont = $("<font></font>").attr("size","2").html(data[i].folderName);
			    	folderNameFont.addClass("list_font");
			    	folderNameChild.append(folderNameFont);
			    	tr.append(folderNameChild);
			    	//defName
			    	var defNameChild = $("<td></td>").attr("style","vertical-align: middle; padding:2px;");
			    	var defNameFont = $("<font></font>").attr("size","2").html(data[i].defName&&data[i].defName.length>8?data[i].defName.substring(0,8)+"...":data[i].defName);
			    	defNameFont.addClass("list_font");
			    	defNameChild.append(defNameFont);
			    	tr.append(defNameChild);
			    	//info
			    	var infoChild = $("<td></td>").attr("style","vertical-align: middle; padding:2px;");
			    	var infoFont = $("<font></font>").attr("size","2").html(data[i].info&&data[i].info.length>15?data[i].info.substring(0,15)+"...":data[i].info);
			    	if ( data[i].isEmergency == 1 ){
			    		infoFont.attr("color","#bd3b3b");
			    		var iconImg = $("<img></img>").attr("src","<c:url value='/resources/images/kriss_old/emergent.gif' />");
			    		infoFont.append(iconImg);
			    	}
			    	infoFont.addClass("list_font");
			    	infoChild.append(infoFont);
			    	tr.append(infoChild);
			    	//currStatusNames
			    	var currStatusNamesChild = $("<td></td>").attr("style","vertical-align: middle; padding:2px;");
			    	var currStatusNamesFont = $("<font></font>").attr("size","2").html(data[i].currStatusNames&&data[i].currStatusNames.length>8?data[i].currStatusNames.substring(0,8)+"...":data[i].currStatusNames);
			    	currStatusNamesFont.addClass("list_font");
			    	currStatusNamesChild.append(currStatusNamesFont);
			    	tr.append(currStatusNamesChild);
			    	//currRsNm
			    	var currRsNmChild = $("<td></td>").attr("style","vertical-align: middle; padding:2px;");
			    	var currRsNmFont = $("<font></font>").attr("size","2").html(data[i].currRsNm&&data[i].currRsNm.length>4?data[i].currRsNm.substring(0,4)+"...":data[i].currRsNm);
			    	currRsNmFont.addClass("list_font");
			    	currRsNmChild.append(currRsNmFont);
			    	tr.append(currRsNmChild);
			    	//collectActivityName
			    	var collectActivityNameChild = $("<td></td>").attr("style","vertical-align: middle; padding:2px;");
			    	var collectActivityNameFont = $("<font></font>").attr("size","2").html(data[i].collectActivityName&&data[i].collectActivityName.length>8?data[i].collectActivityName.substring(0,8)+"...":data[i].collectActivityName);
			    	collectActivityNameFont.addClass("list_font");
			    	collectActivityNameChild.append(collectActivityNameFont);
			    	tr.append(collectActivityNameChild);
			    	//strStartedDate
			    	var strStartedDateChild = $("<td></td>").attr("style","vertical-align: middle; padding:2px;");
			    	var strStartedDateFont = $("<font></font>").attr("size","2").html(data[i].strStartedDate);
			    	strStartedDateFont.addClass("list_font");
			    	strStartedDateChild.append(strStartedDateFont);
			    	tr.append(strStartedDateChild);
			    	
			    	//process
			    	var iconChild = $("<td></td>").attr("style","vertical-align: middle; padding:2px;");
			    	var iconImg = $("<img></img>").attr("src","<c:url value='/resources/images/icons/btn_process.gif' />");
			    	iconChild.append(iconImg);
			    	tr.append(iconChild);
			    	//config
			    	var configChild = $("<td></td>").attr("style","vertical-align: middle; padding:2px;");
			    	var configFont = $("<font></font>").attr("size","2").html("설정");
			    	configFont.addClass("list_font");
			    	configChild.append(configFont);
			    	tr.append(configChild);
			    	
			    	contentTbody.append(tr);
			    }
				
			},
			error : function(data, msg) {
				alert("system error!");
			}
		});
	}
	
	
</script>
<title>BPM</title>
</head>
<body style="font-size: 12px;">
	<form:form name="workListForm" id="workListForm" method="post" target="_blank">
		<form:input path="comCode" type="hidden" id="comCode" name="comCode" value="${sessionScope.loggedUser.comCode}" /> 
		<form:input path="userId" type="hidden" id="userId" name="userId" value="${sessionScope.loggedUser.userId}" /> 
		<form:input path="instanceId" type="hidden" id="instanceId" name="instanceId" value="" /> 
		<form:input path="tracingTag" type="hidden" id="tracingTag" name="tracingTag" value="" /> 
		<form:input path="taskId" type="hidden" id="taskId" name="taskId" value="" /> 
	</form:form>
	<!-- Page Content -->
	<div class="container-fluid">
		<table width=100%>
			<tr height="20px">
				<td>
					<div class="well well-sm" style="padding:0px 0px 0px 10px;font-family: 돋움, 굴림, Gulim, Verdana, Arial; font-size: 12px;">
						<img src="<c:url value='/resources/images/kriss_old/lo_folder.gif' />" align="absmiddle" width="18" height="20" border="0">
                        <font color="#999999">	<spring:message code="old.menu.home.label" 	/>		&gt; 
                        						<spring:message code="old.menu.bpm.manager.label" 	/>			&gt; 
                        						<spring:message code="old.menu.worklist.label" 		/>			&gt;	</font> 
                        <font color="#6E98CF">	<spring:message code="old.menu.my.work.label" 		/>
                        </font>
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<table class="table">
						<tr style="border-bottom: 1px solid #ddd;">
							<td class="active" style="vertical-align: middle"><spring:message code="old.worklist.work.type.label" 	/></td>
							<td style="vertical-align: middle" >
								<select id="folderName" name="folderName" style="width:auto;">
								<!-- <select id="status" name="status" class="form-control" style="width:auto;"> -->
									<option value="all"><spring:message code="menu.all.label" 	/></option>
						      		<c:forEach var="paramvalues" items="${folderList }">
						      			<option value="${paramvalues.defId }">${paramvalues.defName }</option>
						      		</c:forEach>
						      	</select>
							</fieldset>
							</td>
							<td class="active" style="vertical-align: middle">
								<select id="searchOption" name="searchOption" style="width:100px;">
									<option value="defName"><spring:message code="process.name.label" 						/></option>
									<option value="info"><spring:message code="menu.subject.label" 							/></option>
									<option value="status"><spring:message code="old.instancelist.instance.status.label" 	/></option>		
									<option value="manager"><spring:message code="old.instancelist.instance.manager.label" 	/></option>
						      	</select>
							</td>
							<td  style="vertical-align: middle">
								<div class="controlgroup">
									<input type="text" id="searchKeyword" name="searchKeyword" style="width:200px"/>
								</div>
							</td>
							<td class="active" rowspan="2" style="vertical-align: middle; text-align:center;">
								<button type="button" class="btn btn-primary">검색</button>
							</td>
						</tr>
						<tr style="border-bottom: 1px solid #ddd;">
							<td class="active" style="vertical-align: middle"><spring:message code="old.instancelist.instance.stdt.label" 	/></td>
							<td style="vertical-align: middle" colspan="3">
								<input type="text" id="fromDate" name="fromDate" style="width:80px" />
								~
								<input type="text" id="toDate" name="toDate" style="width:80px" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="vertical-align: top;">
					<table class="table table-striped table-inverse">
					  <thead>
					    <tr>
					      <th style="background-color: #454545; height: 20px"><font size="2" color="#ffffff"><spring:message code="old.worklist.work.type.label" 	/>				</font></th>
					      <th style="background-color: #454545; height: 20px"><font size="2" color="#ffffff"><spring:message code="process.name.label" 	/>				</font></th>
					      <th style="background-color: #454545; height: 20px"><font size="2" color="#ffffff"><spring:message code="menu.subject.label" 	/>				</font></th>
					      <th style="background-color: #454545; height: 20px"><font size="2" color="#ffffff"><spring:message code="old.instancelist.instance.status.label" 	/>				</font></th>
					      <th style="background-color: #454545; height: 20px"><font size="2" color="#ffffff"><spring:message code="old.instancelist.instance.manager.label" 	/>				</font></th>
					      <th style="background-color: #454545; height: 20px"><font size="2" color="#ffffff"><spring:message code="old.instancelist.instance.collect.label" 	/>				</font></th>
					      <th style="background-color: #454545; height: 20px"><font size="2" color="#ffffff"><spring:message code="old.instancelist.instance.stdt.label" 	/>				</font></th>
					      <th style="background-color: #454545; height: 20px"><font size="2" color="#ffffff"><spring:message code="old.worklist.work.proc.label" 	/>				</font></th>
					      <th style="background-color: #454545; height: 20px"><font size="2" color="#ffffff"><spring:message code="old.instancelist.instance.config.label" 	/>				</font></th>
					    </tr>
					  </thead>
					  <tbody id="contentTbody">
						<div id="loadingDiv" style="text-align: center; width:100%; display: none;">
							<img id="loadingImage" src="${pageContext.request.contextPath}/resources/images/flowchart/images/loading_animation.gif" />
						</div>
					  </tbody>
					</table>
				</td>
			</tr>
			<tr><td style="text-align: right; padding: 10px;">
				<span class="label label-default">totalCount : <span id="totalCountSpan"></span>
			</td></tr>
			<tr><td>
				<form:form name="pagerForm" id="pagerForm" command="command" target="pagerFrame" action="" method="POST">
					<form:input type="hidden" path="pageNo" name="pageNo" id="pageNo" />
					<form:input type="hidden" path="totalCount" name="totalCount" id="totalCount" />
					<form:input type="hidden" path="pagerSize" name="pagerSize" id="pagerSize" />
					<form:input type="hidden" path="pageSize" name="pageSize" id="pageSize" />
					<form:input type="hidden" path="callbackFunc" name="callbackFunc" id="callbackFunc" value="fn_getRunningInstaceList" />
				</form:form>
				<iframe name="pagerFrame" id="pagerFrame" frameborder="0" scrolling="no" style="height: 32px; width: 100%"></iframe>
<!-- 				<iframe name="pagerFrame" id="pagerFrame" frameborder="0" scrolling="no" style="height: 28px; width: 100%"></iframe> -->
			</td></tr>
		</table>
	</div>
	<!-- /.container -->
</body>
</html>