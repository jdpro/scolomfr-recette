/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  Direction du Numérique pour l'éducation - Ministère de l'éducation nationale, de l'enseignement supérieur et de la Recherche
 * Copyright (C) 2017 Joachim Dornbusch
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
package fr.scolomfr.recette.utils.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * Utility class to get logger by dependency injection Inspired by
 * http://memorynotfound.com/spring-inject-logger-annotation-example/
 */
@Component
public class LoggerPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(final Object bean, String beanName) {
		ReflectionUtils.doWithFields(bean.getClass(), field -> {
			ReflectionUtils.makeAccessible(field);
			// Check if the field is annotated with @Log
			if (field.getAnnotation(Log.class) != null) {
				Logger logger = LoggerFactory.getLogger(bean.getClass());
				field.set(bean, logger);
			}
		});

		return bean;
	}
}
