/**
 * process init() loading
 */
var fnMerge;
var gridParam;
var gridName1 = "htmlTable";
var datarow = {eeno:"", eenm:"", ops_nm:"",  register:"", line:"", bus_stop:"", pgs_st_nm:"", ops_cd:"", pgs_st_cd:"", doc_no:"", req_type:""};

function fnSetDocumentReady(){
	initMenus();
	
	if(sess_mstu == "M" || sess_auth == 5){
		
	}else{
		$("#key_eeno").val(sess_empno);
		$("#key_eenm").val(sess_name);
		$("#key_ops_cd").val(sess_dept_code);
		$("#key_ops_nm").val(sess_dept_name);
		readOnlyStyle("key_ops_cd", 1);
//		readOnlyStyle("key_eenm", 1);
		$("#deptBtn").attr("disabled", true);
	}
	
	$(".inputOnlyNumber").numeric();
	
	getCommonCode("key_pgs_st_cd:X0001:A;", "N", "setCondition();");  
}

function setCondition(){
	$("#key_pgs_st_cd option:eq(2)").remove();
	if($("#hid_cond").val() != ""){
		var hidCond = $("#hid_cond").val().split("|");
		if(hidCond[0] != "") $("#key_eeno").val(hidCond[0]);
		if(hidCond[1] != "") $("#key_eenm").val(hidCond[1]);
		if(hidCond[2] != "") $("#key_line").val(hidCond[2]);
		if(hidCond[3] != "") $("#key_busStop").val(hidCond[3]);
		if(hidCond[4] != "") $("#key_pgs_st_cd").val(hidCond[4]);
		if(hidCond[5] != "") $("#hid_page").val(hidCond[5]);
	}
	
	init();
}

function init(){
	var cn = ["Status", "ID No.", "Name", "Dept.", "SAP Register", "Line", "Bus Stop", "Type", "dept_code", "Status_code", "doc_no"];
	var cm =
		[
		 {name:"pgs_st_nm",		index:"pgs_st_nm",	sortable:false,		formatter:"string",		width:100,	align:"center",	editable:false,	frozen : false},
		 {name:"eeno",			index:"eeno",		sortable:false,		formatter:"string",		width:70,	align:"center",	editable:false,	frozen : false},
		 {name:"eenm",			index:"eenm",		sortable:false,		formatter:"string",		width:135,	align:"left",	editable:false,	frozen : false},
		 {name:"ops_nm",		index:"ops_nm",		sortable:false,		formatter:"string",		width:125,	align:"left",	editable:false,	frozen : false},
		 {name:"register",		index:"register",	sortable:false,		formatter:"string",		width:90,	align:"center",	editable:false,	frozen : false},
		 {name:"line",			index:"line",		sortable:false,		formatter:"string",		width:80,	align:"center",	editable:false,	frozen : false},
		 {name:"bus_stop",		index:"bus_stop",	sortable:false,		formatter:"string",		width:225,	align:"left",	editable:false,	frozen : false},
		 {name:"req_type",		index:"req_type",	sortable:false,		formatter:"string",		width:140,	align:"center",	editable:false,	frozen : false},
		 {name:"ops_cd",		index:"ops_cd",		sortable:false,		formatter:"string",		width:0,	align:"left",	editable:false,	frozen : false,	hidden: true},
		 {name:"pgs_st_cd",		index:"pgs_st_cd",	sortable:false,		formatter:"string",		width:0,	align:"center",	editable:false,	frozen : false,	hidden: true},
		 {name:"doc_no",		index:"doc_no",		sortable:false,		formatter:"string",		width:0,	align:"center",	editable:false,	frozen : false, hidden: true}
	];
	
	var params = {
		key_eeno		: $("#key_eeno").val(),
		key_eenm		: $("#key_eenm").val(),
		key_ops_cd		: $("#key_ops_cd").val(),
		key_line  		: $("#key_line").val(),
		key_busStop  	: $("#key_busStop").val(),
		key_pgs_st_cd	: $("#key_pgs_st_cd").val(),
		locale			: sess_locale,
		corp_cd			: sess_corp_cd
	};
	
	gridParam = {
		viewEdit : [{
			gridName     : gridName1,
			url          : "doRequestShuttleBusList.do",
			colNames     : cn,
			colModel     : cm,
			height       : "100%",
			sortname     : "applyDate",
			sortorder    : "desc",
			rownumbers   : true,
			multiselect  : false,
			cellEdit     : false,
			fnMerge      : false,
			paramJson    : params,
			pager		 : "htmlPager",
			page		 : $("#hid_page").val(),
			completeFc   : "addGridRow();initAfterMenus();"
		}]
	};
	
	//core jqGrid call...
	commonJqGridInit(gridParam);

	jQuery("#"+gridName1).jqGrid("setGridParam",{
		ondblClickRow : function(rowid, iRow, iCol, e){
			if(getColValue("doc_no",rowid, gridName1) != ""){
	            var form = $("<form/>");
	            form.attr("method" , "post");
	            form.attr("id"     , "hideForm");
	            form.attr("action" , "xsb02.gas");
	            var inp1 = $("<input type='hidden' id='hid_eeno' name='hid_eeno'/>").val(getColValue("eeno", rowid, gridName1));
	            var inp2 = $("<input type='hidden' id='hid_doc_no' name='hid_doc_no'/>").val(getColValue("doc_no", rowid, gridName1));
	            var cond = "";
	            cond += $("#key_eeno").val();
	            cond += "|" + $("#key_eenm").val();
				cond += "|" + $("#key_line").val();
				cond += "|" + $("#key_busStop").val();
				cond += "|" + $("#key_pgs_st_cd").val();
				cond += "|" + $("#page_htmlPager").val();
	            
	            var inp3 = $("<input type='hidden' id='hid_cond' name='hid_cond'/>").val(cond);
	            var inp4 = $("<input type='hidden' id='hid_view_nm' name='hid_view_nm'/>").val($("#menu_id").val());
	            var token = $("<input type='hidden' id='hid_csrfToken' name='hid_csrfToken'/>").val($("#csrfToken").val());
	            form.append(inp1, inp2, inp3, inp4, token);
	            $("body").append(form);
	            form.submit();
	            form.remove();
			}
		}
	}).trigger('reloadGrid');
	
	jQuery("#"+gridName1).jqGrid("setFrozenColumns");
	jQuery("#"+gridName1).jqGrid("navGrid","#htmlPager",{edit:false,add:false,del:false,search:false,refresh:false});

}

function setInsaInfo(){
	if($("#key_eeno").val() != ""){
		var keyData = { xusr_empno : $("#key_eeno").val() };
		paramData = {
			paramJson : util.jsonToString(keyData)
		};
		doCommonAjax("/doSearchToUserInfo.do", paramData, "insaCallBack(jsonData.sendResult)");
	}
}

function insaCallBack(result){
	setBottomMsg(result.message, false);
	$("#key_eenm").val(result.xusr_name);
}

function clearInsa(){
	if($("#key_eeno").val() == ""){
		$("#key_eenm").val("");
	}
}

function retrieve(gubn){
	switch(gubn){
		case "search" :
			doSearch("N");
			break;
		case "save" :
			doSearchShuttleBusRequestCheck();
			break;
	}
}

function doSearch(msgFlag){
	var params = {
		key_eeno		: $("#key_eeno").val(),
		key_eenm		: $("#key_eenm").val(),
		key_ops_cd		: $("#key_ops_cd").val(),
		key_line  		: $("#key_line").val(),
		key_busStop  	: $("#key_busStop").val(),
		key_pgs_st_cd	: $("#key_pgs_st_cd").val()
	};
	
	doCommonSearch("doRequestShuttleBusList.do", util.jsonToString(params), "loadCallBack();", gridName1, msgFlag);
}

/**
 * callback
 */
function loadCallBack(){
	addGridRow();
}

function deptFind(){
	var param = "?dcd=key_ops_cd&dcdNm=key_ops_nm&csrfToken="+$("#csrfToken").val();
	newPopWin(ctxPath+"/hncis/popup/deptPopup.gas"+param, "440", "510", "pop_dept");
}

function deptSearch(){
	if($("#key_ops_cd").val() != ""){
		if($("#key_ops_cd").val().length > 8) return;
		if($("#key_ops_cd").val().length == 8){
			
			var keyData = { xorg_orga_c : $("#key_ops_cd").val() };
			paramData = {
				paramJson : util.jsonToString(keyData)
			};
			doCommonAjax("/doSearchToDeptInfo.do", paramData, "deptCallBack(jsonData.sendResult)");
		}
	}else{
		$("#key_ops_nm").val("");
	}
}

function deptCallBack(result){
	$("#key_ops_nm").val(result.xorg_orga_e);
}

function doSearchShuttleBusRequestCheck(){
	var keyData = {
		eeno		: sess_empno,
		final_yn	: "S"
	};
	
	paramData = {
		paramJson : util.jsonToString(keyData)
	};
	
	doCommonAjax("doSearchShuttleBusRequestCheck.do", paramData, "doMovePage(jsonData.sendResult)");
}

function doMovePage(result){
	sess_auth = $("#work_auth").val();
	if(sess_auth == 5 || sess_mstu == "M"){
		var form = $("<form/>");
        form.attr("method" , "post");
        form.attr("id"     , "hideForm");
        form.attr("action" , "xsb03.gas");
        var token = $("<input type='hidden' id='hid_csrfToken' name='hid_csrfToken'/>").val($("#csrfToken").val());
        form.append(token);
        $("body").append(form);
        form.submit();
        form.remove();
	}else{
		if(result.register == "NO"){
			alertUI("Can not Request.");
			return;
		}
		
		if(result.doc_no == null || result.doc_no == ""){
			var form = $("<form/>");
	        form.attr("method" , "post");
	        form.attr("id"     , "hideForm");
	        form.attr("action" , "xsb03.gas");
	        var token = $("<input type='hidden' id='hid_csrfToken' name='hid_csrfToken'/>").val($("#csrfToken").val());
	        form.append(token);
	        $("body").append(form);
	        form.submit();
	        form.remove();
		}else{
			var form = $("<form/>");
	        form.attr("method" , "post");
	        form.attr("id"     , "hideForm");
	        form.attr("action" , "xsb02.gas");
	        var inp1 = $("<input type='hidden' id='hid_eeno' name='hid_eeno'/>").val(result.eeno);
	        var inp2 = $("<input type='hidden' id='hid_doc_no' name='hid_doc_no'/>").val(result.doc_no);
	        var cond = "";
	        cond += $("#key_eeno").val();
	        cond += "|" + $("#key_eenm").val();
			cond += "|" + $("#key_line").val();
			cond += "|" + $("#key_busStop").val();
	        
	        var inp3 = $("<input type='hidden' id='hid_cond' name='hid_cond'/>").val(cond);
	        var inp4 = $("<input type='hidden' id='hid_view_nm' name='hid_view_nm'/>").val($("#menu_id").val());
	        var token = $("<input type='hidden' id='hid_csrfToken' name='hid_csrfToken'/>").val($("#csrfToken").val());
	        form.append(inp1, inp2, inp3, inp4, token);
	        $("body").append(form);
	        form.submit();
	        form.remove();
		}
	}
}
