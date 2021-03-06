package com.hncis.training.manager.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.hncis.common.application.ApprovalGasc;
import com.hncis.common.dao.CommonJobDao;
import com.hncis.common.message.HncisMessageSource;
import com.hncis.common.util.BpmApiUtil;
import com.hncis.common.util.StringUtil;
import com.hncis.common.vo.BgabGascz002Dto;
import com.hncis.common.vo.CommonApproval;
import com.hncis.common.vo.CommonMessage;
import com.hncis.training.dao.TrainingDao;
import com.hncis.training.manager.TrainingManager;
import com.hncis.training.vo.BgabGasctr01;

@Service("trainingManagerImpl")
public class TrainingManagerImpl implements TrainingManager{
    private static final String pCode = "P-B-005";
    private static final String sCode = "GASBZ01250010";

	@Autowired
	public TrainingDao trainingDao;
	
	@Autowired
	public CommonJobDao commonJobDao;
	
	/**
	 * request apply
	 * @return 
	 */
	public Object insertInfoTRToRequest(BgabGasctr01 dto){
		return trainingDao.insertInfoTRToRequest(dto);
	}

	public Object updateInfoTRToRequest(BgabGasctr01 dto) {
		return trainingDao.updateInfoTRToRequest(dto);
	}

	public Object deleteInfoTRToRequest(BgabGasctr01 dto) {
		
		// BPM API호출
		String processCode = pCode; 	//프로세스 코드 (교육신청 프로세스) - 프로세스 정의서 참조
		String bizKey = dto.getDoc_no();	//신청서 번호
		String statusCode = sCode;	//액티비티 코드 (교육신청신청서작성) - 프로세스 정의서 참조
		String loginUserId = dto.getUpdr_eeno();	//로그인 사용자 아이디
		
		BpmApiUtil.sendDeleteAndRejectTask(processCode, bizKey, statusCode, loginUserId);
				
		return trainingDao.deleteInfoTRToRequest(dto);
	}
	
	public BgabGasctr01 getSelectInfoTRToRequest(BgabGasctr01 dto) {
		return trainingDao.getSelectInfoTRToRequest(dto);
	}
	
	public BgabGasctr01 getSelectInfoTRToRequestByIfId(BgabGasctr01 dto) {
		return trainingDao.getSelectInfoTRToRequestByIfId(dto);
	}

	public List<BgabGasctr01> getSelectListTRToRequest(BgabGasctr01 dto) {
		return trainingDao.getSelectListTRToRequest(dto);
	}
	
	public int getSelectCountTRToAccept(BgabGasctr01 dto){
		return Integer.parseInt(trainingDao.getSelectCountTRToAccept(dto));
	}
	
	public List<BgabGasctr01> getSelectListTRToAccept(BgabGasctr01 dto) {
		return trainingDao.getSelectListTRToAccept(dto);
	}

	public CommonMessage setApproveTRToRequest(BgabGasctr01 dto, CommonApproval appInfo, CommonMessage message, HttpServletRequest req) {
		
		BgabGascz002Dto userParam = new BgabGascz002Dto();
		userParam.setCorp_cd(dto.getCorp_cd());
		userParam.setXusr_empno(dto.getEeno());
		BgabGascz002Dto userInfo = commonJobDao.getXusrInfo(userParam);
		
		appInfo.setDoc_no(dto.getDoc_no());					// 문서번호
		appInfo.setSystem_code("TR");								// 시스템코드
		appInfo.setTable_name("bgab_gasctr01");						// 업무 테이블이름
		appInfo.setRpts_eeno(userInfo.getXusr_empno());		// 상신자 사번
		appInfo.setRpts_dept(userInfo.getXusr_dept_code());	// 상신자 부서코드
		appInfo.setStep_code(userInfo.getXusr_step_code());	// 상신자 직위코드
		appInfo.setRpts_eeno_nm(userInfo.getXusr_name());		// 상신자 성명
		appInfo.setStep_nm(userInfo.getXusr_step_name());		// 직위 이름
		appInfo.setTitle_nm(HncisMessageSource.getMessage("edu_req"));								// 업무 이름
		appInfo.setAppType("TR");									// 전결권자 업무
		appInfo.setMax_level(5);									// 해외 결재 LEVEL
		appInfo.setCorp_cd(userInfo.getCorp_cd());

		CommonApproval commonApproval = ApprovalGasc.setApprovalRequestUseYn(appInfo);

		dto.setIf_id(commonApproval.getIf_id());
		dto.setRpts_eeno(userInfo.getXusr_empno());
//		updateInfoTXToApprove(keyParamDto);

		if(commonApproval.getResult().equals("Z")){
			message.setMessage(HncisMessageSource.getMessage("REQUEST.0000"));

			// BPM API호출
			String processCode = pCode; 	//프로세스 코드 (교육신청 프로세스) - 프로세스 정의서 참조
			String bizKey = dto.getDoc_no();	//신청서 번호
			String statusCode = sCode;	//액티비티 코드 (교육신청신청서작성) - 프로세스 정의서 참조
			String loginUserId = dto.getEeno();	//로그인 사용자 아이디
			String comment = null;
			String roleCode = "GASROLE01250030";   //교육신청 담당자 역할코드
			
			//역할정보
			List<String> approveList = commonApproval.getApproveList();
			List<String> managerList = new ArrayList<String>();
			managerList.add("10000001");

			BpmApiUtil.sendCompleteTask(processCode, bizKey, statusCode, loginUserId, roleCode, approveList, managerList);
			
		}else{
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			message.setMessage(HncisMessageSource.getMessage("REQUEST.0001"));
			message.setErrorCode("E");
			message.setCode("");
			message.setCode1("");
		}

		return message;
		
//		return trainingDao.setApproveTRToRequest(dto);
	}

	public CommonMessage setApproveCancelTRToRequest(BgabGasctr01 dto, CommonApproval appInfo, CommonMessage message, HttpServletRequest req) {
		if("".equals(StringUtil.isNullToString(dto.getIf_id()))){
			int cnt = trainingDao.setApproveCancelTRToRequest(dto);
			
			if(cnt > 0){
				
				// BPM API호출
				String processCode = pCode; 	//프로세스 코드 (교육신청 프로세스) - 프로세스 정의서 참조
				String bizKey = dto.getDoc_no();	//신청서 번호
				String statusCode = sCode;	//액티비티 코드 (교육신청신청서작성) - 프로세스 정의서 참조
				String loginUserId = dto.getUpdr_eeno();	//로그인 사용자 아이디
				String roleCode = "GASROLE01210030";  	//교육신청 담당자 역할코드
				
				//역할정보
				List<String> approveList = new ArrayList<String>();
				List<String> managerList = new ArrayList<String>();
				managerList.add("10000001");
				
				BpmApiUtil.sendCollectTask(processCode, bizKey, statusCode, loginUserId, roleCode, approveList, managerList );

				message.setMessage(HncisMessageSource.getMessage("APPROVE.0002"));
			}else{
				message.setMessage(HncisMessageSource.getMessage("APPROVE.0003"));
			}
		}else{
			appInfo.setIf_id(dto.getIf_id());
			appInfo.setTable_name("bgab_gasctr01");
			appInfo.setCorp_cd(dto.getCorp_cd());
			
			CommonApproval commonApproval = ApprovalGasc.setApprovalCancelProcess(appInfo);

			if(commonApproval.getResult().equals("Z")){
				
				// BPM API호출
				String processCode = pCode; 	//프로세스 코드 (교육신청 프로세스) - 프로세스 정의서 참조
				String bizKey = dto.getDoc_no();	//신청서 번호
				String statusCode = sCode;	//액티비티 코드 (교육신청신청서작성) - 프로세스 정의서 참조
				String loginUserId = dto.getUpdr_eeno();	//로그인 사용자 아이디
				String roleCode = "GASROLE01250030";  	//교육신청 담당자 역할코드
				
				//역할정보
				List<String> approveList = new ArrayList<String>();
				List<String> managerList = new ArrayList<String>();
				managerList.add("10000001");
				
				BpmApiUtil.sendCollectTask(processCode, bizKey, statusCode, loginUserId, roleCode, approveList, managerList );

				message.setMessage(HncisMessageSource.getMessage("APPROVE.0002"));
			}else{
				message.setMessage(commonApproval.getMessage());
			}
		}
		
		return message;
//		return trainingDao.setApproveCancelTRToRequest(dto);
	}
	
	public Object setConfirmTRToRequest(BgabGasctr01 dto) {
		return trainingDao.setConfirmTRToRequest(dto);
		
	}

	
	public CommonMessage updateConfirmTRToRequest(BgabGasctr01 dto) {
		
		CommonMessage message = new CommonMessage();
		try{
			int cnt = trainingDao.updateInfoTrToReject(dto);
			message.setMessage(HncisMessageSource.getMessage("CONFIRM.0000"));
			message.setCode1("Y");
			
			// BPM API호출
			String processCode = pCode; 	//프로세스 코드 (교육신청 프로세스) - 프로세스 정의서 참조
			String bizKey = dto.getDoc_no();	//신청서 번호
			String statusCode = "GASBZ01250030";	//액티비티 코드 (교육신청신청서작성) - 프로세스 정의서 참조
			String loginUserId = dto.getUpdr_eeno();	//로그인 사용자 아이디
			String comment = null;
			
			BpmApiUtil.sendFinalCompleteTask(processCode, bizKey, statusCode, loginUserId);
		
		}catch (Exception e) {
			message.setMessage(HncisMessageSource.getMessage("CONFIRM.0001"));
			message.setCode1("N");
		}
		return message;
		
	}
	
	public Object setConfirmCancelTRToRequest(BgabGasctr01 dto) {
		return trainingDao.setConfirmCancelTRToRequest(dto);
	}
	
	public CommonMessage updateInfoTrToReject(BgabGasctr01 dto){
		CommonMessage message = new CommonMessage();
		try{
			int cnt = trainingDao.updateInfoTrToReject(dto);
			message.setMessage(HncisMessageSource.getMessage("REJECT.0000"));
			message.setCode1("Y");
			
			// BPM API호출
			String processCode = pCode; 	//프로세스 코드 (교육신청 프로세스) - 프로세스 정의서 참조
			String bizKey = dto.getDoc_no();	//신청서 번호
			String statusCode = "GASBZ01250030";	//액티비티 코드 (교육신청 담당자확인) - 프로세스 정의서 참조
			String loginUserId = dto.getUpdr_eeno();	//로그인 사용자 아이디
			
			BpmApiUtil.sendDeleteAndRejectTask(processCode, bizKey, statusCode, loginUserId);
							
		}catch (Exception e) {
			message.setMessage(HncisMessageSource.getMessage("REJECT.0001"));
			message.setCode1("N");
		}
		return message;
	}

}
