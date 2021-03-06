var params;
var fnMerge;
var comboVal;
var comboVal1;
var comboVal2;
var comboEmp = '<option role="option" value=""></option>';
var gridName1 = "htmlTable";
var datarow = {stap_ymd:"",sta_hhmm:"",stap_cd:"",stap_adr:"",arvp_cd:"",arvp_adr:"",rem_sbc:"",flht_no:"",svca_amt:"",doc_no:"",seq:"",add_flag:"", po_no:""};
var tmpAgencyData;

function fnSetDocumentReady(){
	$(".sub_title").hide();
	
	initMenus(); 
	$('#PS').slideDown('fast');
	
	
	getCommonCode("cd_stap_cd:XPS03;cd_arvp_cd:XPS03;", "Y", "");  
	getCommonCode("car_type_cd:XPS01:S;", "N", "getKeyCombo('firm_cd')");
//	getKeyCombo('firm_cd2');
	
	chk_auth();
}

function getKeyCombo(codeStr){
	
	var codknd = "";
	switch (codeStr) {
	case "firm_cd": codknd = "firm_cd:S";
		break;
	case "firm_cd2": codknd = "firm_cd2:S";
		break;
	}
	getKeyMultiCombo(codknd);
};

function getKeyMultiCombo(codeStr){

	var keyData = {
			codknd : codeStr
	};
 
	doCommonAjax("doSearchPsToCombo.do", keyData, "comboResult(jsonData,'"+codeStr+"');");
 
};
function comboResult(jsonData,codknd){
	$.each(eval(jsonData.sendResult),function(index,optionData){
		$("#"+index).loadSelect(eval(optionData));
	});
	
	$("#eeno").val(sess_empno);
	$("#eeno_nm").val(sess_name);
	$("#pos_nm").val(sess_step_name);
	$("#dept_nm").val(sess_dept_name);
	$("#tel_no").val(sess_tel_no);
	$("#ptt_ymd").val(getCurrentToDateAddDayEn("/",0));
	
	init();
}

function init(){ 
	
	$("#htmlTable").GridUnload();
	
	cn = ['일정','출발시간','장소','상세정보','장소','상세정보','이름','항공번호','금액','Doc No','Seq']; 
	cm = [
      		{name:"stap_ymd", index:"stap_ymd", sortable:false, formatter:"string",	width:65, readonly:"true", align:"center", editable:false,	frozen : false,
				editoptions: { 
					readonly : true,
		            dataInit: function(element) {
		     		    $(element).datepicker({
		     		    	dateFormat: 'dd/mm/yy',
		     		    	onSelect: function(dataText, inst){
		     		    	}
				    	});
		            }
				}
			},
			{name:"sta_hhmm", index:"sta_hhmm", sortable:false,	formatter:"string",	width:65, align:"center", editable:false, frozen : false,
				editoptions: {maxlength:"4", 
		            dataInit: function(element) {
		     		    $(element).keyup(function(){

		     		    	if(!isNumeric(element.value)){
		     		    		element.value = selectNum(element.value);
		     		    	}

		     		    	if(trimChar(element.value, ":").length == 4){
		     		    		if(element.value.length > 4){
		     		    			element.value = "";
		     		    		}else{
		     		    			element.value = element.value.substring(0, 2)+":"+element.value.substring(2, 4);
		     		    		}
		     				}else{
		     					element.value = trimChar(element.value, ":");
		     				}
		     		    })
				    	
		            }
		        }
			},
			{name:'stap_cd',index:'stap_cd', formatter: "string",width:100,align:'center',editable:false,sortable:false},
			{name:'stap_adr',index:'stap_adr', formatter: "string",width:150,align:'left',editable:false,sortable:false},
			{name:'arvp_cd',index:'arvp_cd', formatter: "string",width:100,align:'center',editable:false,sortable:false},
			{name:'arvp_adr',index:'arvp_adr', formatter: "string",width:150,align:'left',editable:false,sortable:false},   
			{name:'rem_sbc',index:'rem_sbc', formatter: "string",width:222,align:'left',editable:false,sortable:false},
			{name:'flht_no',index:'flht_no', formatter: "string",width:205,align:'left',editable:false,sortable:false},
			{name:'svca_amt',index:'svca_amt', formatter: "string",width:100,align:'left',editable:false,sortable:false,hidden:true},
			{name:'doc_no',index:'doc_no', formatter: "string",width:60,align:'left',editable:false,sortable:false,hidden:true},
			{name:'seq',index:'seq', formatter: "string",width:60,align:'left',editable:false,sortable:false,hidden:true}
	        ];    
	
	url = $("#M_DOC_NO").val() == "" ? "/doSearchToEmpty.do" : "doSearchListPsToRequest.do";
	
	var params = {
			corp_cd		: sess_corp_cd
//			doc_no		: $('#M_DOC_NO').val()
	};

	gridParam = {
		viewEdit : [{
			gridName     : "htmlTable",
			url          : url, 
			colNames     : cn,
			colModel     : cm,
			width        : "1122",
			height       : "100%",
			sortname     : "stap_ymd",
			sortorder    : "desc",
			rownumbers   : true,
			multiselect  : false, 
			cellEdit     : true,
			fnMerge      : false,
			paramJson    : params,
			scroll: true,
			selectCellFc : "setTsGridValue(rowid, iCol, cellcontent);",
			completeFc   : "fnInitGridComplete('htmlTable');"
		}]
	};
	
	url = $("#M_DOC_NO").val() == "" ? "/doSearchToEmpty.do" : "doSearchDriverListPsToRequest.do";
	
	var editFlag = false;
	var cellFc = "";
	if($("#work_auth").val() < 5 && sess_mstu != "M"){
		editFlag = false;
		cellFc = "";
		$("#con_t_btn").attr("style", "display:none");
	}else{
		editFlag = true;
		cellFc = "setChangeImg();";
	}
	
	commonJqGridInit(gridParam, "N");
	
	jQuery("#htmlTable").jqGrid('setGroupHeaders', {
		useColSpanStyle: true,
		groupHeaders:[
	          {startColumnName: 'stap_cd', numberOfColumns: 2, titleText: '출발'},
	          {startColumnName: 'arvp_cd', numberOfColumns: 2, titleText: '도착'}
		]
	});
//	addGridRow(5);
//	initAfterMenus();
//	
	doSearch(); 
}

function chk_auth(){ 
	var f = document.frm;
	
	with(f){
		
		readOnlyStyle("eeno", 1);
		readOnlyStyle("drvr_nm", 1);
		readOnlyStyle("ccpc", 1);
		readOnlyStyle("firm_cd", 1);
		$("#firm_cd").attr("disabled",true); 
		readOnlyStyle("drvr_nm2", 1);
		readOnlyStyle("ccpc2", 1);
		readOnlyStyle("firm_cd2", 1);
		$("#firm_cd2").attr("disabled",true); 
		readOnlyStyle("snb_rson_sbc", 1);
		readOnlyStyle("grss_amt", 1);
		readOnlyStyle("grss_amt2", 1);
	}
}

function retrieve(btnFlag){
	var f = document.frm;
	switch(btnFlag){
	   case "search" :
		    doSearch();
			break;
	}
}

function doSearch(msgFlag){
	
	setFormClear();
	
	var keyData = {
			if_id		: $('#M_DOC_ID').val(),
			corp_cd		: sess_corp_cd
	};
	paramData = {
			paramJson      	: util.jsonToString(keyData)
	};
	doCommonAjax("doSearchInfoPsToRequestForApprove.do", paramData, "loadCallBack(jsonData.sendResult,'"+msgFlag+"');");
}
/**
 * callback
 */
function loadCallBack(result,msgFlag){
	
	loadJsonSet(result);
	if(msgFlag != 'N'){ 
		setBottomMsg(result.message, false);
	}
	
//	if($("#pgs_st_cd").val() != "0" && $("#if_id").val() != ""){
		displaySubmit(document ,result.code, 2);		
//	}else{
//		displaySubmitClear(document);
//	}
	
	
	doSerchList(msgFlag);
	
}

function doSerchList(msgFlag){
	params = {
			doc_no		: $('#doc_no').val(),
			corp_cd		: sess_corp_cd
	};
	doCommonSearch("doSearchListPsToRequestForApprove.do",util.jsonToString(params), "loadCallBackList();", "htmlTable", msgFlag);
}
function loadCallBackList(){
	if(fnMerge !== ""){
		eval(fnMerge);
	}
	addGridRow(5);
	
}

function doInsert(){
	
	if($("#work_auth").val() < 5 && sess_mstu != "M"){
		if($("#doc_no").val() != ''){
			if(sess_empno != $("#ipe_eeno").val()){
				alertUI("You can not save.");
				return;
			};
			if($("#pgs_st_cd").val() != '0'){
				alertUI("There is data that can not be saved.");
				return;
			}
		} 
	}
	 
	var psParams = [];
	var ids = jQuery("#htmlTable").getDataIDs();
	
	for(var i = 0; i < ids.length; i++){
		rowId = ids[i];
		if(rowId)	{
			if(getColValue("stap_cd",rowId)!=''&getColValue("arvp_cd",rowId)!=''){
				
				data =
				{
						doc_no 			: getColValue("doc_no",rowId),
						seq 			: getColValue("seq",rowId),
						stap_ymd 		: dateConversionKr(selectNum(getColValue("stap_ymd",rowId))),
						sta_hhmm		: selectNum(getColValue("sta_hhmm",rowId)),
						stap_cd			: getColValue("stap_cd",rowId),
						stap_adr		: getColValue("stap_adr",rowId),
						arvp_cd			: getColValue("arvp_cd",rowId),
						arvp_adr		: getColValue("arvp_adr",rowId),
						rem_sbc			: getColValue("rem_sbc",rowId),
						flht_no			: getColValue("flht_no",rowId),
						rem_sbc			: getColValue("rem_sbc",rowId),
						svca_amt		: getColValue("svca_amt",rowId),
						ipe_eeno		: sess_empno,
						updr_eeno		: sess_empno,
						corp_cd			: sess_corp_cd
				};
				psParams.push(data);

			}
		} else { alertUI("데이터를 선택하세요.");}
	}
	
	var keyData = {
			doc_no      		: $("#doc_no").val(),
			eeno      			: $("#eeno").val(),
			ptt_ymd      		: dateConversionKr(selectNum($("#ptt_ymd").val())),
			car_type_cd      	: $("#car_type_cd").val(),
			purp_sbc      		: $("#purp_sbc").val(),
			drvr_nm   			: $("#drvr_nm").val(),
			ccpc      			: $("#ccpc").val(),
			firm_cd     		: $("#firm_cd").val(),
			grss_amt     		: $("#grss_amt").val()==''?'0':$("#grss_amt").val(),
			pgs_st_cd			: '0',
			dept_cd				: sess_dept_code,
			ipe_eeno			: sess_empno,
			updr_eeno			: sess_empno,
			corp_cd				: sess_corp_cd
			
	};
	
	confirmUI("저장 하시겠습니까?");
	$("#pop_yes").click(function(){
		$.unblockUI({
			onUnblock: function(){
				var paramData =  {
						paramJsonList 			: util.jsonToList(psParams),
						paramJsonStr 			: util.jsonToString(keyData) 
					};
					doCommonAjax("doInsertPsToRequest.do", paramData, "setBottomMsg(jsonData.sendResult.message, true);insertCallBack(jsonData.sendResult);");
			}
		});
	});
}
function insertCallBack(result){
	
	if(result.code1 == "Y"){
		$("#M_DOC_NO").val(result.code);
		doSearch('N');
	}
	
}
function doDelete(){
	if($("#work_auth").val() < 5 && sess_mstu != "M"){
		if(sess_empno != $("#ipe_eeno").val()){
			alertUI("삭제할 수 없습니다.");
			return;
		};
	}
	if($("#pgs_st_cd").val() != '0'){
		alertUI("삭제할 수 없는 데이터 입니다.");
		return;
	}
	
	var keyData = {
			doc_no				: $("#doc_no").val(),
			corp_cd				: sess_corp_cd
	};
	
	confirmUI("삭제 하시겠습니까?");
	$("#pop_yes").click(function(){
		$.unblockUI({
			onUnblock: function(){
				var paramData = {
						paramJson : util.jsonToString(keyData)
				};
				doCommonAjax("doDeletePsToRequest.do", paramData, "setBottomMsg(jsonData.sendResult.message, true);setLoadUserInfo();");
			}
		});
	});
}
function doApprove(){
	if($("#ipe_eeno").val() != sess_empno){
		alertUI("You can not request.");
		return;
	}
	if($("#pgs_st_cd").val() != '0'){
		alertUI("There is data that can not be requested.");
		return;
	}
	
	var keyData = {
			doc_no			 : $("#doc_no").val(),
			pgs_st_cd		 : 'A',
			updr_eeno 		: sess_empno,
			corp_cd			: sess_corp_cd
	};
	
	confirmUI("신청 하시겠습니까?");
	$("#pop_yes").click(function(){
		$.unblockUI({
			onUnblock: function(){
				var paramData = {
						paramJson : util.jsonToString(keyData)
				};
				doCommonAjax("doApprovePsToRequest.do", paramData, "setBottomMsg(jsonData.sendResult.message, true);doSearch('N');");
			}
		});
	});
}
function doApproveCancel(){
	
	//작성 중인 사람이 아닌 경우에는 cancel할 수 없음.
	if($("#ipe_eeno").val() != sess_empno){
		alertUI("You can not request cancel.");
		return;
	}
	if($("#pgs_st_cd").val() != 'A'){
		alertUI("You can not request cancel.");
		return;
	}
	var keyData = {
			pgs_st_cd		 : '0',
			updr_eeno		 : sess_empno,
			if_id			 : $("#if_id").val(),
			corp_cd			 : sess_corp_cd
	}; 
	
	confirmUI("신청취소 하시겠습니까?");
	$("#pop_yes").click(function(){
		$.unblockUI({
			onUnblock: function(){
				var paramData = {
						paramJson : util.jsonToString(keyData)
				};
				doCommonAjax("doApproveCancelPsToRequest.do", paramData, "setBottomMsg(jsonData.sendResult.message, true);doSearch('N');");
			}
		});
	});
}



function doConfirm(){
	if($("#pgs_st_cd").val() != 'Z'){
		alertUI("You can not confirm.");
		return;
	}
	
	var keyData = {
			doc_no			 	: $("#doc_no").val(),
			pgs_st_cd		 	: '3',
			acpc_eeno		 	: sess_empno,
			budg_no      		: $("#budg_no").val(),
			stap_adr      		: $("#stap_adr").val(),
			stap_ymd   			: dateConversionKr(selectNum($("#stap_ymd").val())),
			sta_hhmm      		: selectNum($("#sta_hhmm").val()),
			arvp_adr     		: $("#arvp_adr").val(),
			arv_ymd     		: dateConversionKr(selectNum($("#arv_ymd").val())),
			arv_hhmm     		: selectNum($("#arv_hhmm").val()),  
			flht_no     		: $("#flht_no").val(),
			cpsn     			: $("#cpsn").val(),
			car_type_cd     	: $("#car_type_cd").val(),
			purp_sbc     		: $("#purp_sbc").val(),
			rem_sbc     		: $("#rem_sbc").val(),
			drvr_nm     		: $("#drvr_nm").val(),
			ccpc     			: $("#ccpc").val(),
			firm_cd     		: $("#firm_cd").val(),
			corp_cd				: sess_corp_cd
	};
	
	confirmUI("확정 하시겠습니까?");
	$("#pop_yes").click(function(){
		$.unblockUI({
			onUnblock: function(){
				var paramData = {
						paramJson : util.jsonToString(keyData)
				};
				doCommonAjax("doConfirmPsToRequest.do", paramData, "setBottomMsg(jsonData.sendResult.message, true);doSearch('N');");
			}
		});
	});
}

function doConfirmCancel(){
	if($("#pgs_st_cd").val() != 'Z'){
		alertUI("You can not confirm cancel.");
		return;
	}
	
	if($("#snb_rson_sbc").val() == ""){
		alertUI("Please enter the reason for confirm cancel");
		$("#snb_rson_sbc").focus();
		return;
	}
	
	var keyData = {
			doc_no			 : $("#doc_no").val(),
			pgs_st_cd		 : '0',
			snb_rson_sbc  	 : $("#snb_rson_sbc").val(),
			updr_eeno		 : sess_empno,
			corp_cd			 : sess_corp_cd
	};
	
	confirmUI("확정취소 하시겠습니까?");
	$("#pop_yes").click(function(){
		$.unblockUI({
			onUnblock: function(){
				var paramData = {
						paramJson : util.jsonToString(keyData)
				};
				doCommonAjax("doConfirmCancelPsToRequest.do", paramData, "setBottomMsg(jsonData.sendResult.message, true);doSearch('N');");
			}
		});
	});
}

function setLoadUserInfo(){
	
	setFormClear();  
	
	$("#eeno").val(sess_empno);
	$("#eeno_nm").val(sess_name);
	$("#pos_nm").val(sess_step_name);
	$("#dept_nm").val(sess_dept_name);
	$("#tel_no").val(sess_tel_no);
	$("#ptt_ymd").val(getCurrentToDateAddDayEn("/",0)); 
}

 function setFormClear(){  

	$('#eeno').val("");
	$('#eeno_nm').val("");
	$('#pos_nm').val("");
	$('#dept_nm').val("");
	$('#ptt_ymd').val("");
	$('#doc_no').val("");
	$('#pgs_st_cd_d').val("");
	$('#tel_no').val("");
	$('#snb_rson_sbc').val("");
	
	
	$('#car_type_cd').val("");
	$('#purp_sbc').val("");
	$('#rem_sbc').val("");
	$('#drvr_nm').val("");
	$('#ccpc').val("");
	$('#firm_cd').val("");
	$('#grss_amt').val("");

}
 
 function cearInsa(){
	if($("#eeno").val() == ""){
		$("#eeno").val("");
		$("#eeno_nm").val("");
		$("#pos_nm").val("");
		$("#dept_nm").val("");
		$("#tel_no").val("");
	}
}

function setInsaInfo(){
	if($("#eeno").val() != ""){
		var keyData = { 
				xusr_empno : $("#eeno").val(),
				corp_cd		: sess_corp_cd
		};
		paramData = {
			paramJson : util.jsonToString(keyData)
		};
		doCommonAjax("/doSearchToUserInfo.do", paramData, "insaCallBack(jsonData.sendResult)");
	}
}
/**
 * callback
 */
function insaCallBack(result){
	$("#eeno").val(result.xusr_empno);
	$("#eeno_nm").val(result.xusr_name);
	$("#pos_nm").val(result.xusr_step_name);
	$("#dept_nm").val(result.xusr_dept_name);
	$("#tel_no").val(result.xusr_tel_no); 
		
}

function gridRowAdd(){
	
	var gridRowId = jQuery("#htmlTable").getDataIDs().length;
	jQuery("#htmlTable").jqGrid("addRowData", gridRowId+1, datarow); 
	initAfterMenus(); 
	
}

function gridRowDelete(){
	

	var rowId = jQuery("#htmlTable").jqGrid("getGridParam", "selrow");
		
	if(rowId == "" || rowId == null){
		alertUI("Please select delete schedule.");
		return;
	}else if(getColValue("doc_no", rowId) == "" ){
		alertUI("Invalid ID.");
		return;
	}
	
	var scheduleInfo = {
			doc_no          : getColValue("doc_no", rowId),
			seq          	: getColValue("seq", rowId) ,
			corp_cd			: sess_corp_cd
	};
	
	confirmUI("Do you want to Delete schedule?");
	$("#pop_yes").click(function(){
		$.unblockUI({
			onUnblock: function(){
				var paramData = {
						paramJson : util.jsonToString(scheduleInfo)
				};
				doCommonAjax("doDeleteScheduleToRequest.do", paramData, "setBottomMsg(jsonData.sendResult.message, true);doSearch();");
			}
		});
	});
}
var win;
function doFileAttach(seq){
	
	if($("#doc_no").val() == ''){
		alertUI("After saving, you can attach files.");
		return;
	} 
	
	if(win != null){ win.close(); }
	var url = "xps01_file.gas", width = "460", height = "453";
	
	$("#hid_doc_no").val($("#doc_no").val());
	$("#hid_eeno").val("00000000");
	$("#hid_seq").val('1');
	$("#hid_use_yn").val("N");
	
	win = newPopWin("about:blank", width, height, "win_file"); 
	document.fileForm.action = url;
	document.fileForm.hid_csrfToken.value = $("#csrfToken").val();
	document.fileForm.target = "win_file"; 
	document.fileForm.method = "post"; 
	document.fileForm.submit();
	
}

function getAgencyComboValue(comboName){
	var returnVal = "";
	var i = 0;

	$.each(eval(tmpAgencyData),function(targetNm,optionData){
		if(targetNm == comboName){
			$.each(eval(optionData),function(index,optionData){
				if(returnVal == ""){
					returnVal = returnVal + htmlDecode(optionData.value) + ":" + htmlDecode(optionData.name);
				}else{
					returnVal = returnVal + ";" + htmlDecode(optionData.value) + ":" + htmlDecode(optionData.name);
				}
				i++;
			});
		}
    });

	return returnVal;
}

function setTsGridValue(rowId, iCol, cellcontent){
	var colNm = jQuery('#htmlTable').jqGrid('getGridParam', 'colModel')[iCol].index;

	var rowId = jQuery("#"+gridName1).jqGrid("getGridParam", "selrow");
	if($('#car_type_cd').val() == '' && rowId != null){
		$("select#" + rowId +"_stap_cd").html(comboEmp);
		$("select#" + rowId +"_arvp_cd").html(comboEmp);
	}else{
		setMiltiComboList(rowId,'stap_cd');
		setMiltiComboList(rowId,'arvp_cd');
	}

//	if(colNm == 'stap_cd' || colNm == 'arvp_cd'){
	if($('#car_type_cd').val() == ''){
		alertUI('차 종류를 먼저 선택하세요.');
		$("#car_type_cd").focus();
		return;
	}
//		setMiltiComboList(rowId,colNm);
//	calcuAmount();
//	}
}

function fnInitGridComplete(initGridNm){
	addGridRow(5, 'htmlTable', 'datarow');
	initAfterMenus();
}
