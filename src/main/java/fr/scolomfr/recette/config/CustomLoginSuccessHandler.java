/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  MENESR (DNE), J.Dornbusch
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package fr.scolomfr.recette.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	private static final String LOGIN_REQUEST_PATH = "/login";
	public static final String URL_PRIOR_LOGIN = "url_prior_login";

	public CustomLoginSuccessHandler(String defaultTargetUrl) {
		setDefaultTargetUrl(defaultTargetUrl);
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session != null) {
			String redirectUrl = (String) session.getAttribute(URL_PRIOR_LOGIN);
			if (redirectUrl != null && !redirectUrl.endsWith(LOGIN_REQUEST_PATH)) {
				session.removeAttribute(URL_PRIOR_LOGIN);
				getRedirectStrategy().sendRedirect(request, response, redirectUrl);
			} else {
				super.onAuthenticationSuccess(request, response, authentication);
			}
		} else {
			super.onAuthenticationSuccess(request, response, authentication);
		}
	}
}