package com.twm.mgmt.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.twm.mgmt.Exception.ModelAndViewException;
import com.twm.mgmt.persistence.entity.SerialCampaignBlockDetail;
import com.twm.mgmt.service.SerialBlackReportService;
import com.twm.mgmt.utils.DateUtilsEx;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/serialBlockReport")
@Controller
public class SerialBlockReportController extends BaseController {
	
	private String INDEX_PAGE = "serialBlockReport/index";
	private int offset = 0;
	private static final int MAX_PAGE_ITEMS = 10;
	private static final String ACTIVE_ERROR_MSG = "執行失敗，請於每月1~7日時間內執行操作!\\r";
	
	@Autowired
	private SerialBlackReportService serialBlackReportService;
	
	/**
	 * 序號發幣黑名單資料維護頁
	 * 
	 * @return
	 */
	@GetMapping("index")
	public String enterPage(HttpServletRequest request) throws Exception {
		log.debug("call SerialBlockReportController.enterPage()");
		return index(request);
	}
	
	/**
	 * 序號發幣黑名單資料維護頁
	 * 
	 * @return
	 */
	@PostMapping("index")
	public String index(HttpServletRequest request) throws Exception {
		log.debug("call SerialBlockReportController.search()");
		int totalRows = 0;
		int startRow = 0;
		
		try {
			disableCheck(request);
			String subId = request.getParameter("subId");
			
			if ((request.getParameter("offset") != null) &&
	                (request.getParameter("offset").trim().length() != 0)) {
	            offset = Integer.parseInt(request.getParameter("offset"));
	        }
			
			if(offset > 0)
				startRow = (offset - 1) * MAX_PAGE_ITEMS;
			
			List<SerialCampaignBlockDetail> blacklist = new ArrayList<SerialCampaignBlockDetail>();
			var blockId = serialBlackReportService.getSerialAvailableBlockIdByKindId(SerialBlackReportService.reportSerialCampaignKind);
			if(null != blockId) {
				totalRows = (StringUtils.isBlank(subId)) ? serialBlackReportService.countSerialBlockDetail(blockId) : 0;
				blacklist = (StringUtils.isBlank(subId)) ? serialBlackReportService.getSerialBlockDetail(blockId, startRow, MAX_PAGE_ITEMS) : serialBlackReportService.getSerialBlockDetailBySubId(blockId, subId);
			}
				
			int totalPages = totalRows / MAX_PAGE_ITEMS;
				
			request.setAttribute("menudata", "2");
			request.setAttribute("menuop", INDEX_PAGE);
			request.setAttribute("blacklist", blacklist);
			request.setAttribute("total", totalRows);
			request.setAttribute("offset", offset);
			request.setAttribute("pageSize", MAX_PAGE_ITEMS);
			request.setAttribute("allPages", totalRows % MAX_PAGE_ITEMS > 0 ? totalPages + 1 : totalPages);
			return INDEX_PAGE;
		}catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ModelAndViewException();
		}
	}
	
	/**
	 * 序號發幣黑名單檔案上傳
	 * 
	 * @return
	 */
	@RequestMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
		log.debug("call SerialBlockReportController.upload()");
		
		try {
			String errorMsg = validate(file);
			if (StringUtils.isNotBlank(errorMsg)) {
				request.setAttribute("errorMsg", errorMsg);
				return index(request);
			}
			
			StringBuffer csvErrorMsg = new StringBuffer();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF8"));
			var serialCampaignBlockDetail = new ArrayList<SerialCampaignBlockDetail>();
			
			var blockId = serialBlackReportService.getSerialAvailableBlockIdByKindId(SerialBlackReportService.reportSerialCampaignKind);
			if(null == blockId) {
				blockId = serialBlackReportService.insertSerialCampaignBlockMain(SerialBlackReportService.reportSerialCampaignKind);
			}
			
			String dataString = null;
			bufferedReader.readLine();
			while ((dataString = bufferedReader.readLine()) != null) {
				log.debug("Column Name: " + dataString);
				String[] data = dataString.split(",", -1);
				SerialCampaignBlockDetail vo = new SerialCampaignBlockDetail();
				
				IntStream.range(0, 4).forEach(s -> {
					if (data[s].trim().isEmpty()) {
						csvErrorMsg.append("您上傳之檔案中有資料未填!\\r");
					}
				});
				if(!isNumber(data[0].trim())){
					csvErrorMsg.append("SubID有非數字資料，請更新資料後再重新進行操作!\\r");
				}
				if(!DateUtilsEx.isDateFormat(data[3].trim())) {
					csvErrorMsg.append("專案申請日資料格式錯誤SubID : " + data[0] + "，請更新資料後再重新進行操作!\\r");
				}
				if (null != csvErrorMsg && csvErrorMsg.length() > 0) {
					bufferedReader.close();
					request.setAttribute("errorMsg", csvErrorMsg);
					return index(request);
				}
				
				vo.setBlockId(blockId);
				vo.setSubId(data[0].trim());
				vo.setProjectCode(data[1].trim());
				vo.setProjectName(data[2].trim());
				vo.setApplyDate(new SimpleDateFormat("yyyy/MM/dd").parse(data[3].trim()));
				vo.setMemo(data.length == 5 ? data[4] : "");
				vo.setCreateDate(new Date());
				
				serialCampaignBlockDetail.add(vo);
			}
			
			serialBlackReportService.insertSerialCampaignBlockDetail(serialCampaignBlockDetail);
			
			bufferedReader.close();
			return index(request);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ModelAndViewException();
		}
	}
	
	/**
	 * 序號發幣黑名單資料刪除
	 * 
	 * @return
	 */
	@PostMapping("delete")
	public String delete(String[] deletedId, HttpServletRequest request) throws Exception {
		log.debug("call SerialBlockReportController.delete()");
		
		try {
			Calendar cal = Calendar.getInstance();
			
			if(isNotModifyDate(cal)) {
				request.setAttribute("errorMsg", ACTIVE_ERROR_MSG);
				return index(request);
			}
			
			if (null == deletedId){
				return index(request);
			}
			List<String> deleteIds = Arrays.asList(deletedId);
			log.debug("Del Ids:" + deleteIds);
			
			for (String rowId : deleteIds) {
				SerialCampaignBlockDetail serialCampaignBlockDetail = serialBlackReportService.getSerialBlockDetailByRowId(rowId);
				String nowDate = DateUtilsEx.parseDateToString(cal.getTime(), DateUtilsEx.DATE_PATTERN_YEAR_MONTH);
				String dataDate = DateUtilsEx.parseDateToString(serialCampaignBlockDetail.getCreateDate(), DateUtilsEx.DATE_PATTERN_YEAR_MONTH);
				if(!dataDate.equals(nowDate)) {
					request.setAttribute("errorMsg", "刪除失敗，非本月黑名單不可刪除!");
					return index(request);
				}
			}
			
			serialBlackReportService.delete(deleteIds);
			log.info(" --- SerialBlockReportController.delete() end -- ");
			
			return index(request);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ModelAndViewException();
		}
	}
	
	/**
	 * 序號發幣黑名單設定的下載上傳範例檔
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("downloadExample")
	public ResponseEntity<Resource> downloadExample() throws IOException {
		log.debug("call SerialBlockReportController.downloadExample()");
		try {
	        Path filePath = Paths.get(File.separator + "home" + File.separator + "tpsacct" + File.separator + "序號簡訊黑名單新增檔案上傳範例檔.csv");	     
	        HttpHeaders header = new HttpHeaders();
			header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=example.csv");
			header.add("Cache-Control", "no-cache, no-store, must-revalidate");
			header.add("Pragma", "no-cache");
			header.add("Expires", "0");

			ByteArrayResource resource = new ByteArrayResource(Files.newInputStream(filePath, LinkOption.NOFOLLOW_LINKS).readAllBytes());
	        
	        return ResponseEntity.ok().headers(header).contentLength(Files.size(filePath))
					.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);

	    } catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ModelAndViewException();
		}
	}

	private void disableCheck(HttpServletRequest request) {
		var disable = true;
		Calendar cal = Calendar.getInstance();
		disable = isNotModifyDate(cal);
		
		request.setAttribute("disable", disable);
		request.setAttribute("dateS", DateUtilsEx.parseDateToString(cal.getTime(), DateUtilsEx.DATE_PATTERN_YEAR_MONTH));
	}

	private boolean isNotModifyDate(Calendar cal) {
		var isNotModifyDate = true;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		
		if(dayOfMonthNow > 0 && dayOfMonthNow < 8) {
			isNotModifyDate = false;
		}
		return isNotModifyDate;
	}
	
	private boolean isNumber(String checkStr){
		try {
			int i = Integer.parseInt(checkStr);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private String validate(MultipartFile file) throws IOException {
		String errorMsg = "";
		
		if(isNotModifyDate(Calendar.getInstance())) {
			errorMsg += ACTIVE_ERROR_MSG;
		}
		// 检查文件是否上傳
		if (file == null || file.isEmpty()) {
			errorMsg += "請選擇欲上傳之檔案!\\r";
		}
		
		// 检查文件格式
		if (file != null && !file.getOriginalFilename().endsWith(".csv")) {
			errorMsg += "您上傳之檔案格式不為 csv 檔，還請確認檔案格式後再進行上傳作業！";
		}
		return errorMsg;
	}

}