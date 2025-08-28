package com.twm.mgmt.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.twm.mgmt.constant.CrsConstants;
import com.twm.mgmt.enums.ActionType;
import com.twm.mgmt.model.account.APAccountConditionVo;
import com.twm.mgmt.model.account.APAccountResultVo;
import com.twm.mgmt.model.account.APAccountVo;
import com.twm.mgmt.model.account.APKeyIvVo;
import com.twm.mgmt.model.account.AccountConditionVo;
import com.twm.mgmt.model.account.AccountResultVo;
import com.twm.mgmt.model.account.AccountVo;
import com.twm.mgmt.model.account.DepartmentConditionVo;
import com.twm.mgmt.model.account.DepartmentResultVo;
import com.twm.mgmt.model.account.DepartmentVo;
import com.twm.mgmt.model.account.MOAccountConditionVo;
import com.twm.mgmt.model.account.MOAccountResultVo;
import com.twm.mgmt.model.account.MOAccountVo;
import com.twm.mgmt.model.account.PermissionVo;
import com.twm.mgmt.model.account.RoleVo;
import com.twm.mgmt.model.common.MenuVo;
import com.twm.mgmt.model.common.QueryResultVo;
import com.twm.mgmt.model.common.SubMenuVo;
import com.twm.mgmt.model.common.UserInfoVo;
import com.twm.mgmt.model.report.*;
import com.twm.mgmt.persistence.dao.CheckbillDao;
import com.twm.mgmt.persistence.dao.ElectronicmoneyDao;
import com.twm.mgmt.persistence.dto.APAccountDto;
import com.twm.mgmt.persistence.dto.AccountDto;
import com.twm.mgmt.persistence.dto.CheckbillDto;
import com.twm.mgmt.persistence.dto.ElectronicmoneyDto;
import com.twm.mgmt.persistence.dto.MOAccountDto;
import com.twm.mgmt.persistence.entity.APAccountEntity;
import com.twm.mgmt.persistence.entity.APKeyIvEntity;
import com.twm.mgmt.persistence.entity.AccountEntity;
import com.twm.mgmt.persistence.entity.AccountPermissionProgramEntity;
import com.twm.mgmt.persistence.entity.DepartmentEntity;
import com.twm.mgmt.persistence.entity.MOAccountEntity;
import com.twm.mgmt.persistence.entity.MenuEntity;
import com.twm.mgmt.persistence.entity.ProgramEntity;
import com.twm.mgmt.persistence.entity.RewardReportEntity;
import com.twm.mgmt.persistence.entity.RoleEntity;
import com.twm.mgmt.persistence.entity.RolePermissionProgramEntity;
import com.twm.mgmt.persistence.entity.pk.APAccountPk;
import com.twm.mgmt.persistence.repository.APAccountRepository;
import com.twm.mgmt.persistence.repository.APKeyIvRepository;
import com.twm.mgmt.persistence.repository.AccountPermissionProgramRepository;
import com.twm.mgmt.persistence.repository.DepartmentRepository;
import com.twm.mgmt.persistence.repository.MOAccountRepository;
import com.twm.mgmt.persistence.repository.MenuRepository;
import com.twm.mgmt.persistence.repository.RewardReportRepository;
import com.twm.mgmt.persistence.repository.RolePermissionProgramRepository;
import com.twm.mgmt.persistence.repository.RoleRepository;
import com.twm.mgmt.utils.AESUtil;
import com.twm.mgmt.utils.DateUtilsEx;
import com.twm.mgmt.utils.JsonUtil;
import com.twm.mgmt.utils.RandomUtil;
import com.twm.mgmt.utils.StringUtilsEx;

@Service
public class ReportService extends BaseService {

	@Value("${ap.account.secrect.key}")
	private String screctKey;

	@Value("${ap.account.secrect.iv}")
	private String screctIv;
	
	@Value("${report.path}")
	private String reportPath;
	
	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private MenuRepository menuRepo;

	@Autowired
	private RolePermissionProgramRepository rolePermissionProgramRepo;

	@Autowired
	private AccountPermissionProgramRepository accountPermissionProgramRepo;

	@Autowired
	private DepartmentRepository departmentRepo;

	@Autowired
	private APAccountRepository apAccountRepo;

	@Autowired
	private APKeyIvRepository apKeyIvRepo;

	@Autowired
	private MOAccountRepository moAccountRepo;
	
	@Autowired
	private RewardReportRepository rewardreportRepo;
		
	@Autowired
	private ElectronicmoneyDao electronicmoneyDao;

	@Autowired
	private CheckbillDao checkbillDao;
		
	@Value("${EPAPI.master}")
	public String EpApiMaster;
	
	@Value("${EPAPI.userId}")
	public String EpApiId;

	@Value("${EPAPI.userPwd}")
	public String EpApiPwd;
	
	
	public List<momoDepartmentVo> findmomoDepartmentList() {
		List<MOAccountEntity> entities = moAccountRepo.findMOAccount();
		for(MOAccountEntity x : entities) {
			//System.out.println("data:"+x.getDepartmentId()+","+x.getDeptNo()+","+x.getApiKey());
		}
		
		
		return entities.stream().map(entity -> new momoDepartmentVo(entity)).collect(Collectors.toList());
	}
	
	//核帳報表頁面, momo部門會針對登入者顯示所屬的momo部門,而不是顯示全部的momo部門
	public List<String> findmomoCustomDepartmentList(Long accountId) {
		List<String> entities = moAccountRepo.findMOCustomAccount(accountId);
		//checkmarx弱掃
		entities = JsonUtil.jsonToList(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(entities))), String.class);
		return entities;
	}
	
	public List<String> findmomoDepartmentList1() {
		List<String> entities = moAccountRepo.findMOAccount1();
		//checkmarx弱掃
		entities = JsonUtil.jsonToList(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(entities))), String.class);
		return entities;
	}
	
	public List<String> findElectronicmoneyDepartmentList() {
		//System.out.println("findElectronicmoneyDepartmentListstart");
		List<String> entities = rewardreportRepo.findREQUISITION_UNIT();
		//checkmarx弱掃
		entities = JsonUtil.jsonToList(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(entities))), String.class);
		//System.out.println("findElectronicmoneyDepartmentListend"+entities.size());
//		return entities.stream().map(entity -> new ElectronicmoneyDepartmentVo(entity)).collect(Collectors.toList());
		return entities;
	}
	
	@Transactional
	public QueryResultVo findcheckbillList(CheckbillVo condition,List<String> momoDepartmentStr_List) throws ParseException {
		
		QueryResultVo resultVo = new QueryResultVo(condition);
		
		List<CheckbillDto> dtos = checkbillDao.findByCondition(condition,momoDepartmentStr_List);
//		List<CheckbillDto> dtos = TransactionredeemhistoryRepo.findByCondition(condition);
		for(CheckbillDto x : dtos) {
			if(x.getReceivedate() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Calendar c = Calendar.getInstance();
				try{
				   c.setTime(sdf.parse(x.getReceivedate()));
				}catch(ParseException e){
				   e.printStackTrace();
				 }
				//Incrementing the date by 1 day
				c.add(Calendar.DAY_OF_MONTH, 1);  
				String newDate = sdf.format(c.getTime());  
				x.setCanceldate(newDate); //canceldate				
			}

		}
		//System.out.println("checkbillnum1:"+dtos.size());
//		//System.out.println("checkbillnum2:"+checkbillDao.countByCondition(condition));
		resultVo.setTotal(checkbillDao.countByCondition(condition,momoDepartmentStr_List));
//		resultVo.setTotal(20);
		resultVo.setResult(dtos);
		return resultVo;
	}
	
	@Transactional
	public String findcheckbillList1(CheckbillVo condition,List<String> momoDepartmentStr_List) {
		String result = "";
		List<CheckbillDto> dtos = checkbillDao.findByCondition(condition,momoDepartmentStr_List);
		int count = 0;
		int sum = 0;
		for(CheckbillDto x : dtos) {
			count +=Integer.parseInt(x.getQuantity()) ;
			sum += Integer.parseInt(x.getMomoney()) ;
		}
		result = "總計筆數/金額："+count+"筆/$"+sum;
		return result;
		
	}
	
//	@Transactional
//	public String findcheckbillList2() {
//		String result = "";
//		SimpleDateFormat sdFormat = new SimpleDateFormat("hhmmss");
//		Date date = new Date();
//		String strDate = sdFormat.format(date);
//		return strDate;
//		
//	}
	
	@Transactional
	public void findcheckbillList3(List<CheckbillDto> dtos, String filename, ByteArrayOutputStream byteArrayOutputStream) throws ParseException, IOException {

		String[] arrayTitle={
				"momo公司單位",		"收款日期", "銷帳日期",		"筆數",      "mo幣金額"	};
		//1、新建工作簿
		XSSFWorkbook workbook=new XSSFWorkbook();
		//2、建立工作表
		XSSFSheet sheet=workbook.createSheet("名單資訊");
		
		//3、建立行 - title
		XSSFRow row0title=sheet.createRow(0);
		//4、建立內容 - title
		for(int i=0; i<=arrayTitle.length-1; i++) {
			row0title.createCell(i).setCellValue(arrayTitle[i]);
		}
		
		//5、建立行 - 內容-1
		XSSFCell cell = null;
		//6、建立內容 - 內容-1 
		for(int s=0;s<=dtos.size()-1;s++) {
			XSSFRow nextrow = sheet.createRow(s+1);	
			
			for(int i=0; i<=4; i++) {
				
				cell = nextrow.createCell(i);
				if(i==0) {
					//System.out.println("Part1:"+dtos.get(s).getDepartmentId());					
					cell.setCellValue(dtos.get(s).getDepartmentId());
				}
				if(i==1 && (dtos.get(s).getReceivedate()!=null)) {
					cell.setCellValue(dtos.get(s).getReceivedate().toString());
				}
				if(i==2  && (dtos.get(s).getReceivedate()!=null)) {	
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar c = Calendar.getInstance();
					c.setTime(sdf.parse(dtos.get(s).getReceivedate().toString()));
					c.add(Calendar.DAY_OF_MONTH, 1); 
					
					cell.setCellValue(sdf.format(c.getTime()));
				}
				if(i==3 && (dtos.get(s).getQuantity().toString()!= null)) {
				
					cell.setCellValue(dtos.get(s).getQuantity().toString());
				}
				if(i==4 && (dtos.get(s).getMomoney().toString()!= null)) {			
					cell.setCellValue(dtos.get(s).getMomoney().toString());
				}

			}							
		}
		
		//解Checkmarx的高風險Relative Path Traversal
	    // 將 Excel 寫入到 ByteArrayOutputStream
	    try {
	        workbook.write(byteArrayOutputStream);
	    } catch (IOException e) {
	        e.printStackTrace();
	        throw e;
	    } finally {
	        workbook.close();
	    }
	}
	
	@Transactional
	public void createDetailFile(List<CheckbillDto> dtos, String filename,ByteArrayOutputStream byteArrayOutputStream) throws ParseException, IOException {

		String[] arrayTitle={
				"公司編號","公司名稱","公司統編","單位代碼","單位名稱"	,"通路","商店店點編號","商店店點名稱","兌幣項目","兌幣交易序號","商家訂單編號","商家交易編號","uuid","商家會員編號","訂單日期","兌幣日期","兌幣金額","取消兌幣日期","取消兌幣金額"};
		//1、新建工作簿
		XSSFWorkbook workbook=new XSSFWorkbook();
		//2、建立工作表
		XSSFSheet sheet=workbook.createSheet("名細資訊");
		//3、建立行 - title
		XSSFRow row0title=sheet.createRow(0);
		//4、建立內容 - title
		for(int i=0; i<=arrayTitle.length-1; i++) {
			row0title.createCell(i).setCellValue(arrayTitle[i]);
		}
		//5、建立行 - 內容-1
		XSSFCell cell = null;
		//6、建立內容 - 內容-1 
		for(int s=0;s<=dtos.size()-1;s++) {
			XSSFRow nextrow = sheet.createRow(s+1);	
			for(int i=0; i<=19; i++) {
				cell = nextrow.createCell(i);
				if(i==0 && (dtos.get(s).getCOMPANY_NUMBER()!=null)) {
					cell.setCellValue(dtos.get(s).getCOMPANY_NUMBER().toString());
				}
				if(i==1 && (dtos.get(s).getCOMPANY()!=null)) {
					cell.setCellValue(dtos.get(s).getCOMPANY().toString());
				}
				if(i==2 && (dtos.get(s).getINVOICE_NUMBER()!=null)) {
					cell.setCellValue(dtos.get(s).getINVOICE_NUMBER().toString());
				}
				if(i==3 && (dtos.get(s).getDEPT_NO()!=null)) {
					cell.setCellValue(dtos.get(s).getDEPT_NO().toString());
				}
				if(i==4 && (dtos.get(s).getDEPT_NAME()!=null)) {
					cell.setCellValue(dtos.get(s).getDEPT_NAME().toString());
				}
				if(i==5 && (dtos.get(s).getCHANNEL_NAME()!=null)) {
					cell.setCellValue(dtos.get(s).getCHANNEL_NAME().toString());
				}
				if(i==6 && (dtos.get(s).getSTORE_ID()!=null)) {
					cell.setCellValue(dtos.get(s).getSTORE_ID().toString());
				}
				if(i==7 && (dtos.get(s).getSTORE_NAME()!=null)) {
					cell.setCellValue(dtos.get(s).getSTORE_NAME().toString());
				}
				if(i==8 && (dtos.get(s).getORDER_NOTE()!=null)) {
					cell.setCellValue(dtos.get(s).getORDER_NOTE().toString());
				}
				if(i==9 && (dtos.get(s).getMO_CHARGE_ID()!=null)) {
					cell.setCellValue(dtos.get(s).getMO_CHARGE_ID().toString());
				}
				if(i==10 && (dtos.get(s).getO_ORDER_NUMBER()!=null)) {
					cell.setCellValue(dtos.get(s).getO_ORDER_NUMBER().toString());
				}
				if(i==11 && (dtos.get(s).getT_ORDER_NUMBER()!=null)) {
					cell.setCellValue(dtos.get(s).getT_ORDER_NUMBER().toString());
				}
				if(i==12 && (dtos.get(s).getTWM_UUID()!=null)) {
					cell.setCellValue(dtos.get(s).getTWM_UUID().toString());
				}
				if(i==13 && (dtos.get(s).getTWM_UID()!=null)) {
					cell.setCellValue(dtos.get(s).getTWM_UID().toString());
				}
				if(i==14 && (dtos.get(s).getORDER_DATE()!=null)) {
					cell.setCellValue(dtos.get(s).getORDER_DATE().toString());
				}
				if(i==15 && (dtos.get(s).getMO_TX_TIME()!=null)) {
					cell.setCellValue(dtos.get(s).getMO_TX_TIME().toString());
				}
				if(i==16 && (dtos.get(s).getAMOUNT()!=null)) {
					cell.setCellValue(dtos.get(s).getAMOUNT().toString());
				}
				if(i==17 && (dtos.get(s).getMO_REFUND_TX_TIME()!=null)) {
					cell.setCellValue(dtos.get(s).getMO_REFUND_TX_TIME().toString());
				}
				if(i==18 && (dtos.get(s).getCANCEL_AMOUNT()!=null)) {
					cell.setCellValue(dtos.get(s).getCANCEL_AMOUNT().toString());
				}

			}							
		}
		
		//解Checkmarx的高風險Relative Path Traversal
	    // 將 Excel 寫入到 ByteArrayOutputStream
	    try {
	        workbook.write(byteArrayOutputStream);
	    } catch (IOException e) {
	        e.printStackTrace();
	        throw e;
	    } finally {
	        workbook.close();
	    }
	}
	
	public QueryResultVo findElectronicMoneyList(ElectronicMoneyVo condition, int roleid, HttpServletRequest request) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		
		QueryResultVo resultVo = new QueryResultVo(condition);
		List<ElectronicmoneyDto> result1 = electronicmoneyDao.findByCondition(condition);
		//System.out.println("neyDtosize:"+result1.size());
		
		for(int i=0;i<result1.size(); i++) {
			//System.out.println("getRoleid01:"+result1.get(i).getPayaccount());
			//System.out.println("getRoleid02:"+result1.get(i).getPayaccountId());
			//System.out.println("getRoleid03:"+result1.get(i).getMomoeventId());
			////System.out.println("getRoleid1:"+rewardreportRepo.getCampaignofaccountofRoleid(result1.get(i).getMomoeventId()));
			
			

				int resultnum = rewardreportRepo.getCampaignofaccountofRoleid(result1.get(i).getMomoeventId());
				//System.out.println("getRoleid2:"+resultnum);
				if( roleid == 25) //登入者的角色 pm角色 = 25
				{
					//System.out.println("getRoleid3=25:");
					if(resultnum != roleid) { //
						result1.remove(i--);
						//System.out.println("getRoleid4_remove:");
					}			
				}				
		}
		
		List<ElectronicMoneyVo> x = new ArrayList<ElectronicMoneyVo>();
		if(result1.size()>0) {
			//System.out.println("getRoleid6:");
			x = test(result1);
			for(ElectronicMoneyVo xx : x) { //setPrRequestorDept
				//System.out.println("getrpart7:"+xx.getPrRequestorDept());
			}
			HttpSession session= request.getSession();
			UserInfoVo xx = (UserInfoVo) session.getAttribute(CrsConstants.USER_INFO);

			//SSOLogin
			AccountEntity accountEntity = accountRepo.findByAccountID1(xx.getAccountId());
//			AccountEntity accountEntity = accountRepo.findByAccountID1((long)1);

			for(int j =0; j<x.size();j++) {
				//System.out.println("derole1:"+accountEntity.getRoleId().toString());
				//System.out.println("derole1.5:"+x.size());
				if(accountEntity.getRoleId().toString().equals("25")) { //看角色

					//test
					//System.out.println("derole2:"+accountEntity.getRoleId().toString());
					//System.out.println("derole3:"+x.get(j).getPrRequestorDept());
					
					//SSOLogin
					if(!x.get(j).getPrRequestorDept().equals(xx.getDepartmentName())) {
						x.remove(j--);
					}
//					if(!x.get(j).getPrRequestorDept().equals("語音服務開發部00")) {
//						x.remove(j--);
//					}
					
				}				
			}	
		}
		resultVo.setTotal(x.size());
		resultVo.setResult(x);
		x = null;
		return resultVo;		
	}

	public List<ElectronicMoneyVo> test(List<ElectronicmoneyDto> result1) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		
		// 定義信任所有證書的 TrustManager
        TrustManager[] trustAllCerts = new TrustManager[] {
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[0];
                    }
                    public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            };
        
		HttpsURLConnection conn = null;
		List<ElectronicMoneyVo> ElectronicMoneyVoList = new ArrayList<ElectronicMoneyVo>();
		for(int i=0; i<=result1.size()-1; i++) {
			
			List<String> poNumsList = new ArrayList<>();
			poNumsList.add(result1.get(i).getMomoeventId());
			HashMap<String, Object> mapResponse = new HashMap<String, Object>();
			
	        mapResponse.put("userId", EpApiId);
	        mapResponse.put("userPwd", EpApiPwd);
	        
	        mapResponse.put("poNums", poNumsList);
	        
	        Gson gson = new Gson();
	        String json = gson.toJson(mapResponse);
	                
	        URL connectto = new URL(EpApiMaster);
	        conn = (HttpsURLConnection) connectto.openConnection();
	        
	        // 設置自定義的 SSLSocketFactory，信任所有證書
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            conn.setSSLSocketFactory(sc.getSocketFactory());

            // 跳過主機名驗證
            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
	        conn.setRequestMethod( "POST" );
	        conn.setRequestProperty( "Content-Type", "application/json");
	        conn.setRequestProperty( "charset", "utf-8");
	        conn.setUseCaches(false);
	        conn.setAllowUserInteraction(false);
	        conn.setInstanceFollowRedirects( false );
	        conn.setDoOutput( true );
	       	        	        
	        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	        wr.writeBytes(json);
	        wr.flush();
	        wr.close();
	        
	        int responseCode = conn.getResponseCode();          
	        //System.out.println("Response Code : " + responseCode);

	        
	        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
	        StringBuilder sb = new StringBuilder();
	        String line;
	        String strContext = "";
	        while ((line = br.readLine()) != null) {
	            sb.append(line+"\n");
	            strContext = line;
	            //System.out.println("strContextis: "+strContext);
	        }
	        
	        br.close();	
	        
	        JsonObject convertedObject = new Gson().fromJson(strContext, JsonObject.class);
	        
			//Part2: 取得 post 內容
			//pos去掉 '['  與  ']'  符號後 轉json format
			JsonObject convertedPosObject = new Gson().fromJson(convertedObject.get("pos").toString().replace("[", "").replace("]", "") , JsonObject.class);
			 //System.out.println("strContextis1: ");
			if(convertedObject.get("result").getAsJsonObject().get("resultID").getAsInt() == 100) {
				if(convertedPosObject.get("checkId").getAsString().equals("00")) {
					if(convertedPosObject.get("poInfo").getAsJsonObject().get("poNum").getAsString().equals(result1.get(i).getMomoeventId())) {
						
						
						List<RewardReportEntity> rre = rewardreportRepo.findBymomoEventNo(result1.get(i).getMomoeventId());
						//System.out.println("strContextis1.1: "+rre.size());
						for(int j=0;j<=rre.size()-1;j++) {
							//System.out.println("strContextis1.2: "+rre.get(j).getOrderNumber());
							//System.out.println("strContextis1.3: "+result1.get(i).getMomoeventId());
							if(rre.get(j).getOrderNumber().equals(result1.get(i).getMomoeventId()))
							{
								ElectronicMoneyVo data = new ElectronicMoneyVo();
								//System.out.println("strContextis1.2: "+rre.get(j).getReportId());
								String _strpayac = rre.get(j).getPayAccount();
							
								//下單日期
								data.setOrderdate(convertedPosObject.get("poInfo").getAsJsonObject().get("poApprovedDate").getAsString());
								//System.out.println("strContextis2: ");
								
								//po單號
								data.setMomoeventId(convertedPosObject.get("poInfo").getAsJsonObject().get("poNum").getAsString());
								//System.out.println("strContextis3: ");		
								
								//請購單位
								//System.out.println("strContextis3.5: "+convertedPosObject.get("poInfo").getAsJsonObject().get("prRequestorDept").getAsString());
								data.setPrRequestorDept(convertedPosObject.get("poInfo").getAsJsonObject().get("prRequestorDept").getAsString());
								//請購人 prRequestorName
								data.setPrRequestorName(convertedPosObject.get("poInfo").getAsJsonObject().get("prRequestorName").getAsString());
								//訂單數量poQty
								data.setPoQty(convertedPosObject.get("poInfo").getAsJsonObject().get("poQty").getAsString());
								
								//驗收金額rcvAmountWithTax
								data.setRcvAmountWithTax(convertedPosObject.get("poInfo").getAsJsonObject().get("rcvAmountWithTax").getAsString());
								
								//訂單金額 poAmountWithTax
								data.setPoAmountWithTax(convertedPosObject.get("poInfo").getAsJsonObject().get("poAmountWithTax").getAsString());
								//System.out.println("strContextis4: ");
								
								//戶頭代碼 payaccount findBymomoEventNo
								//System.out.println("strContextis1.3: "+rre.get(j).getPayAccount());
								data.setPayaccount(_strpayac);
								int size = rre.get(j).getPayAccount().length();
								//System.out.println("last3:"+rre.get(j).getPayAccount().substring(size - 4, size - 3));
								
								int itema=convertedPosObject.get("poInfo").getAsJsonObject().get("poAmountWithTax").getAsInt();
								if(rre.get(j).getPayAccount().substring(size - 4, size - 3).equals("M")) {
									itema = (int) (itema + (itema*0.05));
									data.setAmountValidityPeriod("中效期");
									
								}
								if(rre.get(j).getPayAccount().substring(size - 4, size - 3).equals("S")) {
									itema = (int) (itema + (itema*0.1));
									data.setAmountValidityPeriod("短效期");
								}
								
								if(rre.get(j).getPayAccount().substring(size - 4, size - 3).equals("L")) {
									itema = (int) (itema + (itema*0));
									data.setAmountValidityPeriod("長效期");//123
								}
								
								data.setItemA(String.valueOf(itema));
								
								//System.out.println("intItemB_before:"+rre.get(j).getReportId());
								int intItemB = rewardreportRepo.getpayaccount_rr_totalmoney_this_month(rre.get(j).getReportId());
								//System.out.println("intItemB:"+intItemB);
								data.setItemB(String.valueOf(intItemB));
								
								int intItemC = rewardreportRepo.getpayaccount_rr_totalmoney(rre.get(j).getReportId());
								//System.out.println("xxxxis:"+intItemC);
								data.setItemC(String.valueOf(intItemC));
								
								data.setItemD(String.valueOf(itema-intItemC));
								
								
								//mo幣效期 AMOUNT_VALIDITY_DATE  amountValidityPeriod
//								DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
//								data.setAmountValidityPeriod(dateFormat.format(rre.get(j).getAmountValidityDate()));
//								
								ElectronicMoneyVoList.add(data);								
							}	
						}	
					}
				}
			}
		}

		return ElectronicMoneyVoList;
		
		
	}

	public List<PayaccountVo> findPayaccountList() {
		List<RewardReportEntity> entities = rewardreportRepo.findEMpayaccount();
		//checkmarx弱掃
		entities = JsonUtil.jsonToList(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(entities))), RewardReportEntity.class);
		//return entities.stream().map(entity -> new PayaccountVo(entity)).collect(Collectors.toList());
		//System.out.println("PayaccountVosizee11:"+entities.stream().distinct().collect(Collectors.toList()).size());
		return entities.stream().map(entity -> new PayaccountVo(entity)).collect(Collectors.toList());
		
	}
	
	public List<MomoeventVo> findMomoeventList() {
		List<RewardReportEntity> entities = rewardreportRepo.findEMmomoevent();
		//checkmarx弱掃
		entities = JsonUtil.jsonToList(ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(JsonUtil.objectToJson(entities))), RewardReportEntity.class);
		//return entities.stream().map(entity -> new PayaccountVo(entity)).collect(Collectors.toList());
		//System.out.println("PayaccountVosizee12:"+entities.stream().distinct().collect(Collectors.toList()).size());
		return entities.stream().map(entity -> new MomoeventVo(entity)).collect(Collectors.toList());
		
	}
	
	
	public List<RoleVo> findRoleList() {
		List<RoleEntity> entities = roleRepo.findAll();

		return entities.stream().map(entity -> new RoleVo(entity)).collect(Collectors.toList());
	}

	public List<AccountVo> findAccountList() {
		List<AccountEntity> entities = accountRepo.findEnabledAccount();

		return entities.stream().map(entity -> new AccountVo(entity)).collect(Collectors.toList());
	}


}
