package com.mt.common.domain.model.cache;

import org.hibernate.Cache;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Service
public class HibernateCacheService {
    @Autowired
    private EntityManager entityManager;
    //clear hibernate second level cache when manually update database, to resolve entity not found issue
    public void clearCache() {
        Session session = entityManager.unwrap(Session.class);
        SessionFactory sessionFactory = session.getSessionFactory();
        Cache cache = sessionFactory.getCache();
        cache.evictAll();
        cache.evictAllRegions();
        cache.evictCollectionData();
    }
}
