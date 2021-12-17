package com.mt;

import com.mt.access.domain.model.client.Client;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
//@EnableCaching
public class Access {
    public static void main(String[] args) {
        //remove redision create cache provider
        Iterator<CachingProvider> iterator = Caching.getCachingProviders(Caching.getDefaultClassLoader()).iterator();
        while(iterator.hasNext()) {
            CachingProvider provider = iterator.next();
            if(!"org.ehcache.jsr107.EhcacheCachingProvider".equals(provider.getClass().getName())){
                iterator.remove();
            }
        }
        SpringApplication.run(Access.class, args);
    }
}

