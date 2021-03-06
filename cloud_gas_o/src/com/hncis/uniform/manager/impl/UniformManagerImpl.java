package com.hncis.uniform.manager.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.hncis.common.message.HncisMessageSource;
import com.hncis.common.util.BpmApiUtil;
import com.hncis.common.util.CurrentDateTime;
import com.hncis.common.util.StringUtil;
import com.hncis.common.vo.CommonMessage;
import com.hncis.uniform.dao.UniformDao;
import com.hncis.uniform.manager.UniformManager;
import com.hncis.uniform.vo.BgabGascaf01Dto;
import com.hncis.uniform.vo.BgabGascaf02Dto;
import com.hncis.uniform.vo.BgabGascaf03Dto;

@Service("uniformManagerImpl")
public class UniformManagerImpl  implements UniformManager{
    private transient Log logger = LogFactory.getLog(getClass());
    private static final String pCode = "P-B-002";
	
	@Autowired
	public UniformDao uniformDao;
	
	@Override
	public List<BgabGascaf02Dto> selectTypeofclothesComboList(String corp_cd){
		return uniformDao.selectTypeofclothesComboList(corp_cd);
	}
	@Override
	public List<BgabGascaf02Dto> selectColorComboList(String corp_cd){
		return uniformDao.selectColorComboList(corp_cd);
	}
	@Override
	public List<BgabGascaf02Dto> selectSizeComboList(String corp_cd){
		return uniformDao.selectSizeComboList(corp_cd);
	}
	
	@Override
	public int selectCountToRequest(BgabGascaf01Dto vo){
//		String[] qtyTerm = getUniformCycleQtyTerm(vo);
//		vo.setUnif_cycle_qty(qtyTerm[0]);
//		vo.setUnif_cycle_term(qtyTerm[1]);
		
		return uniformDao.selectCountToRequest(vo);
	}
	@Override
	public List<BgabGascaf01Dto> selectListToRequest(BgabGascaf01Dto vo){
//		String[] qtyTerm = getUniformCycleQtyTerm(vo);
//		vo.setUnif_cycle_qty(qtyTerm[0]);
//		vo.setUnif_cycle_term(qtyTerm[1]);
		
		return uniformDao.selectListToRequest(vo);
	}
	@Override
	public CommonMessage insertListToRequest(List<BgabGascaf01Dto> list){
		
		CommonMessage message = new CommonMessage();
		message.setResult("Y");
		message.setMessage(HncisMessageSource.getMessage("APPLY.0000"));
		
		for(BgabGascaf01Dto dto : list){
			String remainQty = StringUtil.isNullToString(uniformDao.selectUniformClrQty(dto), "0");
			if("N".equals(dto.getAdmin_yn())){
//				if("0".equals(remainQty)){
//					message.setResult("N");
//					message.setMessage(HncisMessageSource.getMessage("UNIFORM.0001") + "\n(" + dto.getRow_number() + " "+HncisMessageSource.getMessage("row")+")");
//					return message;
//				}else if(Integer.parseInt(dto.getUnif_ptt_qty()) > Integer.parseInt(remainQty)){
//					BgabGascaf01Dto nmResult = uniformDao.selectUniformQuantityChkUniformNm(dto);
//					message.setResult("N");
//					message.setMessage(nmResult.getUnif_type_nm() + " > " + nmResult.getUnif_clr_nm() + " / "+HncisMessageSource.getMessage("max_req_qty")+" : " + nmResult.getUnif_cycle_qty() + "\n"+HncisMessageSource.getMessage("MSG.VAL.0046"));
//					return message;
//				}
			}

			// BPM API호출
			String processCode = pCode; 	//프로세스 코드 (근무복 프로세스) - 프로세스 정의서 참조
			String bizKey = dto.getDoc_no() + dto.getUnif_type_cd() + dto.getUnif_clr_cd();	//신청서 번호
			String statusCode = "GASBZ01220010";	//액티비티 코드 (근무복 작성) - 프로세스 정의서 참조
			String loginUserId = dto.getEeno();	//로그인 사용자 아이디
			String comment = null;
			String roleCode = "GASROLE01220030";   //담당자 역할코드
			//역할정보
			List<String> approveList = new ArrayList<String>();
			List<String> managerList = new ArrayList<String>();
			managerList.add("10000001");
			
			BpmApiUtil.sendSaveTask(processCode, bizKey, statusCode, loginUserId, roleCode, approveList, managerList ); // BPM 저장
			BpmApiUtil.sendCompleteTask(processCode, bizKey, statusCode, loginUserId, roleCode, approveList, managerList); // BPM 신청
		}
		
		uniformDao.insertListToRequest(list);
		
		return message;
	}
	
	public int selectCountToList(BgabGascaf01Dto vo){
		return uniformDao.selectCountToList(vo);
	}
	public List<BgabGascaf01Dto> selectListToList(BgabGascaf01Dto vo){
		return uniformDao.selectListToList(vo);
	}
	public int deleteRequestToList(List<BgabGascaf01Dto> list){
		for(BgabGascaf01Dto dto : list){
			// 	BPM API호출
			String processCode = pCode; 		//프로세스 코드 (명함 프로세스) - 프로세스 정의서 참조
			String bizKey = dto.getDoc_no();		//신청서 번호
			String statusCode = "GASBZ01220010";	//액티비티 코드 (명함신청서작성) - 프로세스 정의서 참조
			String loginUserId = dto.getUpdr_eeno();		//로그인 사용자 아이디
			String userId = dto.getEeno();	//로그인 사용자 아이디
			String comment = null;
			String roleCode = "GASROLE01220030";  	//명함 담당자 역할코드
			
			//역할정보
			List<String> approveList = new ArrayList<String>();
			List<String> managerList = new ArrayList<String>();
			managerList.add("10000001");
			
			BpmApiUtil.sendCollectTask(processCode, bizKey, statusCode, loginUserId, roleCode, approveList, managerList );
			BpmApiUtil.sendDeleteAndRejectTask(processCode, bizKey, statusCode, userId);
		}
		return uniformDao.deleteRequestToList(list);
	}
	
	public int selectCountToConfirm(BgabGascaf01Dto vo){
		return uniformDao.selectCountToConfirm(vo);
	}
	public List<BgabGascaf01Dto> selectListToConfirm(BgabGascaf01Dto vo){
		return uniformDao.selectListToConfirm(vo);
	}
	public CommonMessage updateUniformConfirmToConfirm(List<BgabGascaf01Dto> list){
		CommonMessage message = new CommonMessage();
		
		BgabGascaf03Dto stock = new BgabGascaf03Dto();
		BgabGascaf03Dto stock1 = new BgabGascaf03Dto();
		stock1.setUnif_type_cd(list.get(0).getUnif_type_cd());
		stock1.setUnif_clr_cd(list.get(0).getUnif_clr_cd());
		stock1.setUnif_msm_cd(list.get(0).getUnif_msm_cd());
		stock1.setCorp_cd(list.get(0).getCorp_cd());
		
		String stockMaxSeq = uniformDao.selectMaxSeqToStockManagement(stock1);
		
		for(BgabGascaf01Dto vo : list){
			int stockRemain = uniformDao.selectStockCheckToRequest(vo);
			
			if(stockRemain >= Integer.parseInt(vo.getUnif_ptt_qty())){
				uniformDao.updateUniformStatusToConfirm(vo);
				
				stock = new BgabGascaf03Dto();
				stock.setUnif_sex_cd(vo.getUnif_sex_cd());
				stock.setUnif_type_cd(vo.getUnif_type_cd());
				stock.setUnif_clr_cd(vo.getUnif_clr_cd());
				stock.setUnif_msm_cd(vo.getUnif_msm_cd());
				stock.setUnif_seq(stockMaxSeq);
				stock.setUnif_in_count("0");
				stock.setUnif_out_count(vo.getUnif_ptt_qty());
				stock.setUnif_total_count( String.valueOf(uniformDao.selectUniformTotalCountToStockManagement(stock1) - Integer.parseInt(vo.getUnif_ptt_qty())) );
				stock.setUnif_rem(vo.getEeno() + " - CONFIRM( "+CurrentDateTime.dateConversionBr(CurrentDateTime.getDate1())+" )");
				stock.setCorp_cd(vo.getCorp_cd());
				
				uniformDao.insertListToStockManagement(stock);
				
				// BPM API호출
				String processCode = pCode; 	//프로세스 코드 (명함 프로세스) - 프로세스 정의서 참조
				String bizKey = vo.getDoc_no();	//신청서 번호
				String statusCode = "GASBZ01220030";	//액티비티 코드 (명함 담당자확인) - 프로세스 정의서 참조
				String loginUserId = vo.getUpdr_eeno();	//로그인 사용자 아이디
				String comment = null;

				BpmApiUtil.sendFinalCompleteTask(processCode, bizKey, statusCode, loginUserId);
				
				stockMaxSeq = String.valueOf(Integer.parseInt(stockMaxSeq)+1);
			}else{
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				message.setMessage(HncisMessageSource.getMessage("STOCK.0000"));
				message.setResult("E");
				return message;
			}
		}
		
		message.setMessage(HncisMessageSource.getMessage("CONFIRM.0000"));
		message.setResult("Y");
		
		return message;
	}
	
	public CommonMessage updateUniformConfirmCancelToConfirm(List<BgabGascaf01Dto> list){
		CommonMessage message = new CommonMessage();
		
		BgabGascaf03Dto stock = new BgabGascaf03Dto();
		stock.setUnif_type_cd(list.get(0).getUnif_type_cd());
		stock.setUnif_clr_cd(list.get(0).getUnif_clr_cd());
		stock.setUnif_msm_cd(list.get(0).getUnif_msm_cd());
		
		String stockMaxSeq = uniformDao.selectMaxSeqToStockManagement(stock);
		
		for(BgabGascaf01Dto vo : list){
			uniformDao.updateUniformStatusToConfirm(vo);
			
			stock = new BgabGascaf03Dto();
			stock.setUnif_type_cd(vo.getUnif_type_cd());
			stock.setUnif_clr_cd(vo.getUnif_clr_cd());
			stock.setUnif_msm_cd(vo.getUnif_msm_cd());
			stock.setUnif_seq(stockMaxSeq);
			stock.setUnif_in_count(vo.getUnif_ptt_qty());
			stock.setUnif_out_count("0");
			stock.setUnif_total_count( String.valueOf(uniformDao.selectUniformTotalCountToStockManagement(stock) + Integer.parseInt(vo.getUnif_ptt_qty())) );
			stock.setUnif_rem(vo.getEeno() + " - CANCEL( "+CurrentDateTime.dateConversionBr(CurrentDateTime.getDate1())+" )");
			
			uniformDao.insertListToStockManagement(stock);
			
			stockMaxSeq = String.valueOf(Integer.parseInt(stockMaxSeq)+1);
		}
		
		message.setResult("Y");
		
		return message;
	}
	
	@Override
	public List<BgabGascaf02Dto> selectTypeofclothesListToItemInfo(BgabGascaf02Dto vo){
		return uniformDao.selectTypeofclothesListToItemInfo(vo);
	}
	@Override
	public List<BgabGascaf02Dto> selectColorListToItemInfo(BgabGascaf02Dto vo){
		return uniformDao.selectColorListToItemInfo(vo);
	}
	@Override
	public List<BgabGascaf02Dto> selectSizeListToItemInfo(BgabGascaf02Dto vo){
		return uniformDao.selectSizeListToItemInfo(vo);
	}
	@Override
	public int saveListToItemInfo(List<BgabGascaf02Dto> iList, List<BgabGascaf02Dto> uList){
		int iCnt = 0;
		int uCnt = 0;
		
		if(iList.size() > 0){
			int maxSeq = uniformDao.selectMaxSeqToItemInfo(iList.get(0));
			for(BgabGascaf02Dto iData : iList){
				iData.setUnif_cd(String.valueOf(maxSeq++));
			}
			
			iCnt = uniformDao.insertListToItemInfo(iList);
		}
		
		uCnt = uniformDao.updateListToItemInfo(uList);
		
		return iCnt + uCnt;
	}
	@Override
	public int deleteListToItemInfo(List<BgabGascaf02Dto> list){
		
		for(BgabGascaf02Dto dto : list){
			if("CLOTHES".equals(dto.getUnif_type_cd())){
				BgabGascaf02Dto tmpClothes = new BgabGascaf02Dto();
				tmpClothes.setUnif_prn_cd(dto.getUnif_cd());
				uniformDao.deleteUniformItemToDownCodeSize(tmpClothes);

				tmpClothes = new BgabGascaf02Dto();
				tmpClothes.setUnif_type_cd("COLOR");
				tmpClothes.setUnif_prn_cd(dto.getUnif_cd());
				uniformDao.deleteUniformItemToDownCode(tmpClothes);
			}
			
			if("COLOR".equals(dto.getUnif_type_cd())){
				BgabGascaf02Dto tmpColor = new BgabGascaf02Dto();
				tmpColor.setUnif_type_cd("SIZE");
				tmpColor.setUnif_prn_cd(dto.getUnif_cd());
				uniformDao.deleteUniformItemToDownCode(tmpColor);
			}
		}
		
		return uniformDao.deleteListToItemInfo(list);
	}
	@Override
	public int selectCountToStockManagement(BgabGascaf03Dto vo){
		return uniformDao.selectCountToStockManagement(vo);
	}
	@Override
	public List<BgabGascaf03Dto> selectListToStockManagement(BgabGascaf03Dto vo){
		return uniformDao.selectListToStockManagement(vo);
	}
	@Override
	public CommonMessage insertListToStockManagement(BgabGascaf03Dto vo){
		CommonMessage message = new CommonMessage();
		
		vo.setUnif_seq(uniformDao.selectMaxSeqToStockManagement(vo));
		int totalCnt = uniformDao.selectUniformTotalCountToStockManagement(vo);
		
		if(totalCnt < Integer.parseInt(vo.getUnif_out_count())){
			message.setMessage(HncisMessageSource.getMessage("SAVE.0001"));
		}else{
			vo.setUnif_total_count(String.valueOf(totalCnt + Integer.parseInt(vo.getUnif_in_count()) - Integer.parseInt(vo.getUnif_out_count())));
			uniformDao.insertListToStockManagement(vo);
			message.setMessage(HncisMessageSource.getMessage("SAVE.0000"));
		}
		
		return message;
	}
	@Override
	public CommonMessage deleteListToStockManagement(List<BgabGascaf03Dto> list){
		CommonMessage message = new CommonMessage();
		int totalCnt = 0;
		
		for(BgabGascaf03Dto vo : list){
			totalCnt = uniformDao.selectUniformTotalCountToStockManagement(vo);
			if(totalCnt < Integer.parseInt(vo.getUnif_in_count())){
				message.setMessage(HncisMessageSource.getMessage("DELETE.0001"));
			}else{
				uniformDao.deleteListToStockManagement(vo);
			}
		}
		
//		System.out.println("===== message.getMessage() : "+message.getMessage());
		
		if("".equals(StringUtil.isNullToString(message.getMessage()))){
			message.setMessage(HncisMessageSource.getMessage("DELETE.0000"));
		}else{
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		
		return message;
	}
	
	public String[] getUniformCycleQtyTerm(BgabGascaf01Dto vo){
		String[] returnArr = new String[2];
		String cycleCd = uniformDao.selectUniformCycleToRequest(vo);
		String unifQty  = "";
		String unifTerm = "";
		int    termConvertMonth = 1;
		boolean unifFlag = true;
		char numCheck;
		
		for(int i=0; i<cycleCd.length(); i++){
			numCheck = cycleCd.charAt(i);
//			System.out.println("termConvertMonth : "+termConvertMonth);
			if(numCheck < 48 || numCheck > 58){
//				System.out.println("문자 : " + cycleCd.charAt(i) +" : "+numCheck);
				termConvertMonth = ("Y".equals(String.valueOf(numCheck))) ? 12 : 1 ; 
				unifFlag = false;
			}else{
//				System.out.println("숫자 : " + cycleCd.charAt(i) +" : "+numCheck);
				if(unifFlag){
					unifQty = String.valueOf(numCheck);
				}else{
					unifTerm = String.valueOf(numCheck);
				}
			}
//			System.out.println("unifFlag : "+unifFlag);
//			System.out.println("unifQty  : "+unifQty);
//			System.out.println("unifTerm : "+unifTerm);
		}
		
//		System.out.println("Integer.parseInt(unifTerm) * termConvertMonth : "+Integer.parseInt(unifTerm) * termConvertMonth);
		
		returnArr[0] = unifQty;
		returnArr[1] = String.valueOf(Integer.parseInt(unifTerm) * termConvertMonth);
		
		return returnArr;
	}
	
	public CommonMessage updateUniformListToReject(List<BgabGascaf01Dto> list){
		
		CommonMessage message = new CommonMessage();
		
		BgabGascaf03Dto stock = new BgabGascaf03Dto();
		stock.setUnif_type_cd(list.get(0).getUnif_type_cd());
		stock.setUnif_clr_cd(list.get(0).getUnif_clr_cd());
		stock.setUnif_msm_cd(list.get(0).getUnif_msm_cd());
		
		String stockMaxSeq = uniformDao.selectMaxSeqToStockManagement(stock);
		
		for(BgabGascaf01Dto vo : list){
			uniformDao.updateUniformListToReject(vo);
			
			stock = new BgabGascaf03Dto();
			stock.setUnif_sex_cd(vo.getUnif_sex_cd());
			stock.setUnif_type_cd(vo.getUnif_type_cd());
			stock.setUnif_clr_cd(vo.getUnif_clr_cd());
			stock.setUnif_msm_cd(vo.getUnif_msm_cd());
			stock.setUnif_seq(stockMaxSeq);
			stock.setUnif_in_count(vo.getUnif_ptt_qty());
			stock.setUnif_out_count("0");
			stock.setUnif_total_count( String.valueOf(uniformDao.selectUniformTotalCountToStockManagement(stock) + Integer.parseInt(vo.getUnif_ptt_qty())) );
			stock.setUnif_rem(vo.getEeno() + " - REJECT( "+CurrentDateTime.dateConversionBr(CurrentDateTime.getDate1())+" ) " + vo.getSnb_rson_sbc());
			stock.setCorp_cd(vo.getCorp_cd());
			
			uniformDao.insertListToStockManagement(stock);
			
			// BPM API호출
			String processCode = pCode; 	//프로세스 코드 (휴양소 프로세스) - 프로세스 정의서 참조
			String bizKey = vo.getDoc_no();	//신청서 번호
			String statusCode = "GASBZ01220030";	//액티비티 코드 (휴양소 당당자 확인) - 프로세스 정의서 참조
			String loginUserId = vo.getUpdr_eeno();	//로그인 사용자 아이디

			BpmApiUtil.sendDeleteAndRejectTask(processCode, bizKey, statusCode, loginUserId);
			
			stockMaxSeq = String.valueOf(Integer.parseInt(stockMaxSeq)+1);
		}
		
		message.setResult("Y");
		
		return message;
	}
	
	public CommonMessage updateConfirmListToRequest(List<BgabGascaf01Dto> list){
		CommonMessage message = new CommonMessage();
		int remainQty  = 0;
		int stockCount = 0;
		BgabGascaf03Dto stockParam = new BgabGascaf03Dto();
		
		for(BgabGascaf01Dto vo : list){
			remainQty  = 0;
			stockCount = 0;
			
			stockParam.setUnif_type_cd(vo.getUnif_type_cd());
			stockParam.setUnif_clr_cd(vo.getUnif_clr_cd());
			stockParam.setUnif_msm_cd(vo.getUnif_msm_cd());
			
			stockCount = uniformDao.selectUniformTotalCountToStockManagement(stockParam);
			remainQty  = Integer.parseInt(uniformDao.selectUniformRemainQty(vo));
			
			
			if(remainQty < Integer.parseInt(vo.getUnif_ptt_qty())){
				message.setResult("N");
				message.setMessage(HncisMessageSource.getMessage("SAVE.0001"));
				break;
			}else if(stockCount < Integer.parseInt(vo.getUnif_ptt_qty())){
				message.setResult("N");
				message.setMessage(HncisMessageSource.getMessage("STOCK.0000"));
				break;
			}else{
				uniformDao.updateUniformConfirmListToRequest(vo);
				
				if("3".equals(vo.getUaps1_cd())){
					stockParam = new BgabGascaf03Dto();
					stockParam.setUnif_type_cd(vo.getOld_unif_type_cd());
					stockParam.setUnif_clr_cd(vo.getOld_unif_clr_cd());
					stockParam.setUnif_msm_cd(vo.getOld_unif_msm_cd());
					
					String stockMaxSeq = uniformDao.selectMaxSeqToStockManagement(stockParam);
					
					stockParam = new BgabGascaf03Dto();
					stockParam.setUnif_type_cd(vo.getOld_unif_type_cd());
					stockParam.setUnif_clr_cd(vo.getOld_unif_clr_cd());
					stockParam.setUnif_msm_cd(vo.getOld_unif_msm_cd());
					stockParam.setUnif_seq(stockMaxSeq);
					stockParam.setUnif_in_count("0");
					stockParam.setUnif_out_count(vo.getUnif_ptt_qty());
					stockParam.setUnif_total_count( String.valueOf(uniformDao.selectUniformTotalCountToStockManagement(stockParam) + Integer.parseInt(vo.getUnif_ptt_qty())) );
					stockParam.setUnif_rem(vo.getEeno() + " - CONFIRM EDIT( "+CurrentDateTime.dateConversionBr(CurrentDateTime.getDate1())+" )");
					
					uniformDao.insertListToStockManagement(stockParam);
					
					stockParam = new BgabGascaf03Dto();
					stockParam.setUnif_type_cd(vo.getUnif_type_cd());
					stockParam.setUnif_clr_cd(vo.getUnif_clr_cd());
					stockParam.setUnif_msm_cd(vo.getUnif_msm_cd());
					
					stockMaxSeq = uniformDao.selectMaxSeqToStockManagement(stockParam);
					
					stockParam = new BgabGascaf03Dto();
					stockParam.setUnif_type_cd(vo.getUnif_type_cd());
					stockParam.setUnif_clr_cd(vo.getUnif_clr_cd());
					stockParam.setUnif_msm_cd(vo.getUnif_msm_cd());
					stockParam.setUnif_seq(stockMaxSeq);
					stockParam.setUnif_in_count(vo.getUnif_ptt_qty());
					stockParam.setUnif_out_count("0");
					stockParam.setUnif_total_count( String.valueOf(uniformDao.selectUniformTotalCountToStockManagement(stockParam) + Integer.parseInt(vo.getUnif_ptt_qty())) );
					stockParam.setUnif_rem(vo.getEeno() + " - CONFIRM EDIT( "+CurrentDateTime.dateConversionBr(CurrentDateTime.getDate1())+" )");
					
					uniformDao.insertListToStockManagement(stockParam);
				}
				
				message.setResult("Y");
				message.setMessage(HncisMessageSource.getMessage("SAVE.0000"));
			}
		}
		
		if("N".equals(message.getResult())){
			TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();
		}
		
		return message;
	}
	@Override
	public int selectCountToStockManagementList(BgabGascaf03Dto vo){
		return uniformDao.selectCountToStockManagementList(vo);
	}
	@Override
	public List<BgabGascaf03Dto> selectListToStockManagementList(BgabGascaf03Dto vo){
		return uniformDao.selectListToStockManagementList(vo);
	}
	@Override
	public List<BgabGascaf03Dto> selectListToStockDetailIn(BgabGascaf03Dto vo){
		return uniformDao.selectListToStockDetailIn(vo);
	}
	@Override
	public int selectCountToStockDetailOut(BgabGascaf03Dto vo){
		return uniformDao.selectCountToStockDetailOut(vo);
	}
	@Override
	public List<BgabGascaf03Dto> selectListToStockDetailOut(BgabGascaf03Dto vo){
		return uniformDao.selectListToStockDetailOut(vo);
	}
	public BgabGascaf03Dto selectGridStockDetailBasic(BgabGascaf03Dto vo){
		return uniformDao.selectGridStockDetailBasic(vo);
	}
	
	public int insertListToStockManagementList(List<BgabGascaf03Dto> iList, List<BgabGascaf03Dto> uList){
		uniformDao.updateListToStockManagementList(uList);
		return uniformDao.insertListToStockManagementList(iList);
	}
}
