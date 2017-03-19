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
package fr.scolomfr.recette.model.tests.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("web")
public class WebPersistenceManager implements PersistenceManager {
	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Override
	public boolean errorIsIgnored(String key) {
		if (null == key) {
			return false;
		}
		String status = stringRedisTemplate.opsForValue().get(key);
		return null != status && "IGNORE".equals(status);
	}

	@Override
	public boolean hasKey(String key) {
		return stringRedisTemplate.hasKey(key);
	}

	@Override
	public void setKey(String key, String string) {
		stringRedisTemplate.opsForValue().set(key, "IGNORE");
	}

	@Override
	public void delete(String key) {
		stringRedisTemplate.delete(key);
	}
}
