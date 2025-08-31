package com.twm.mgmt.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.twm.mgmt.constant.CrsConstants;
import com.twm.mgmt.model.common.UserInfoVo;
import com.twm.mgmt.service.SsoService;
import com.twm.mgmt.utils.StringUtilsEx;
import com.twm.mgmt.ws.nt.GetTokenValueRs;

@RequestMapping(SsoController.SSO_URI)
@Controller
public class SsoController extends BaseController {

	public static final String SSO_URI = "/sso";

	public static final String LOGIN_URI = "/login";

	public static final String GET_TOKEN_URI = "/getToken";

	private static final String DUMMY_URI = "/dummy";

	@Value("${nt.sso.sid}")
	private String sid;

	@Value("${nt.sso.auth.type}")
	private String authType;

	@Autowired
	private SsoService service;

	/**
	 * 認證
	 * 
	 * @return
	 */
	@GetMapping(LOGIN_URI)
	public ModelAndView login(HttpServletRequest request) {
		if (isLocal(request)) {

			return new ModelAndView(String.format("redirect:%s%s", SSO_URI, DUMMY_URI));
		}

		ModelAndView mv = new ModelAndView("sso/login");

		mv.addObject("url", service.proxy.ntSsoUrl);

		mv.addObject("sid", sid);

		mv.addObject("authType", authType);

		return mv;
	}

	/**
	 * 取得Token ID並要求Token Value
	 * 
	 * @param tokenId
	 * @return
	 */
	@PostMapping(GET_TOKEN_URI)
	public RedirectView getToken(@RequestParam("TokenID") String tokenId, HttpServletRequest request, RedirectAttributes attrs) {
		log.debug("TokenID: {}", tokenId);

		if (StringUtilsEx.isBlank(tokenId)) {

			return new RedirectView(fullUrl(String.format("%s%s", SSO_URI, LOGIN_URI), request));
		}

		try {
			GetTokenValueRs rs = service.getTokenValue(tokenId);

			UserInfoVo vo = service.getUserInfo(rs);

			if (vo == null) {
				log.debug("UserInfoVo not Found...");

				attrs.addFlashAttribute("errorVo", getAccountNotFoundResponse().getBody());

				return new RedirectView(fullUrl(CRS_ERROR_URI, request));
			} else {
				
//				HttpSession session= request.getSession();
//				session.setAttribute("logindata", vo);
				request.getSession().setAttribute(CrsConstants.USER_INFO, vo);
			}
		} catch (Exception e) {
			log.error("SsoController getToken Error: {}", e.getMessage(), e);

			attrs.addFlashAttribute("errorVo", getErrorResponse().getBody());

			return new RedirectView(fullUrl(CRS_ERROR_URI, request));
		}

		return new RedirectView(fullUrl(LOADING_URI, request));
	}

	/**
	 * 假登入
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping(DUMMY_URI)
	public RedirectView dummy(HttpServletRequest request) {
		UserInfoVo vo = UserInfoVo.builder().accountId(1L).userName("YvetteYang").roleId((long) 1).roleName("ROLE_ADMIN").departmentId(1L).departmentName("整合行銷傳播暨會員平台處").buTag("CBG").build();

		request.getSession().setAttribute(CrsConstants.USER_INFO, vo);

		return new RedirectView(fullUrl(LOADING_URI, request));
	}

}
