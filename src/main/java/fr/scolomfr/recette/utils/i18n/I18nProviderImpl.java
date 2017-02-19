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
package fr.scolomfr.recette.utils.i18n;

import java.util.Locale;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import fr.scolomfr.recette.utils.log.Log;

@Component
@Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
public class I18nProviderImpl implements I18nProvider {
	@Autowired
	MessageSource ms;

	@Log
	Logger logger;

	private Locale locale;

	@Override
	public String tr(final String code) {
		return tr(code, null);
	}

	@Override
	public String tr(final String code, Object[] args) {
		if (locale == null) {
			locale = LocaleContextHolder.getLocale();
		}
		return ms.getMessage(code, args, locale);
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
