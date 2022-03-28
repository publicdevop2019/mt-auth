package com.mt;

import java.util.Iterator;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * mt-auth access module.
 */

@SpringBootApplication
//@EnableCaching
@EnableEurekaServer
public class Access {
    /**
     * application entry.
     *
     * @param args initial arguments
     */
    public static void main(String[] args) {
        //remove redision create cache provider
        Iterator<CachingProvider> iterator = Caching
            .getCachingProviders(Caching.getDefaultClassLoader()).iterator();
        while (iterator.hasNext()) {
            CachingProvider provider = iterator.next();
            if (!"org.ehcache.jsr107.EhcacheCachingProvider"
                .equals(provider.getClass().getName())) {
                iterator.remove();
            }
        }
        SpringApplication.run(Access.class, args);
    }
}

