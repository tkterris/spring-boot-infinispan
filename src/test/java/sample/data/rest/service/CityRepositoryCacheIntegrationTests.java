/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.data.rest.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import sample.data.rest.domain.City;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link CityRepository}.
 *
 * @author Oliver Gierke
 * @author Andy Wilkinson
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CityRepositoryCacheIntegrationTests {
	
	private static final String CITY_SEARCH_CACHE = "citySearch";
	
	private static final String SINGLE_CITY_CACHE = "singleCity";

	@Autowired
	CityRepository repository;
	
	@Autowired
	private CacheManager cacheManager;

	@Test
	public void searchCacheUsed() {
		Cache citySearchCache = cacheManager.getCache(CITY_SEARCH_CACHE);
		citySearchCache.clear();
		Page<City> cities = this.repository
				.findByNameContainingAndCountryContainingAllIgnoringCase("", "UK",
						new PageRequest(0, 10));
		assertThat(cities.getTotalElements()).isEqualTo(3L);
		assertThat(((org.infinispan.Cache<?, ?>)citySearchCache.getNativeCache()).size()).isGreaterThan(0);
	}

	@Test
	public void singleCityCacheEviction() {
		Cache singleCityCache = cacheManager.getCache(SINGLE_CITY_CACHE);
		singleCityCache.clear();
		this.repository.findByNameAndCountryAllIgnoringCase("Brisbane", "Australia");
		this.repository.findByNameAndCountryAllIgnoringCase("Melbourne", "Australia");
		this.repository.findByNameAndCountryAllIgnoringCase("Sydney", "Australia");
		this.repository.findByNameAndCountryAllIgnoringCase("Montreal", "Canada");
		this.repository.findByNameAndCountryAllIgnoringCase("Tel Aviv", "Israel");
		this.repository.findByNameAndCountryAllIgnoringCase("Tokyo", "Japan");
		assertThat(((org.infinispan.Cache<?, ?>)singleCityCache.getNativeCache()).size()).isGreaterThan(0);
		assertThat(((org.infinispan.Cache<?, ?>)singleCityCache.getNativeCache()).size()).isLessThanOrEqualTo(5);
	}
}
