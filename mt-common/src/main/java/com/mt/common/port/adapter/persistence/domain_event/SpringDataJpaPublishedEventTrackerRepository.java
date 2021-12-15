//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.mt.common.port.adapter.persistence.domain_event;

import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.notification.PublishedEventTracker;
import com.mt.common.domain.model.notification.PublishedEventTrackerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * improved event tracker, rarely it will not send events properly
 */
@Repository
public interface SpringDataJpaPublishedEventTrackerRepository extends PublishedEventTrackerRepository, JpaRepository<PublishedEventTracker, Long> {
    default PublishedEventTracker publishedNotificationTracker() {
        Iterable<PublishedEventTracker> all = findAll();
        List<PublishedEventTracker> objects = new ArrayList<>(1);
        all.forEach(objects::add);
        return objects.isEmpty() ? new PublishedEventTracker() : objects.get(0);
    }

    /**
     * publish event since id 1
     * when 2,3,5 event got published and 4 got missed
     * skip will only happen once due to id is not consistent when rollback
     */
    default void trackMostRecentPublishedNotification(PublishedEventTracker tracker, List<StoredEvent> events) {
        int lastIndex = events.size() - 1;
        if (lastIndex >= 0) {
            long mostRecentId = events.get(lastIndex).getId(); //5
            //only update tracker when event count pass check
            //5-1 compare to 3
            if ((mostRecentId - tracker.getLastPublishedId()) == events.size()|| tracker.isSkipped()) {
                tracker.setLastPublishedId(mostRecentId);
                tracker.setSkipped(false);
                save(tracker);
            }else{
                // find last publish id which should be 3
                tracker.setSkipped(true);
                for(long i=tracker.getLastPublishedId();i<=mostRecentId;i++){
                    long nextIndex=i;
                    nextIndex++;
                    long finalNextIndex = nextIndex;
                    if(events.stream().noneMatch(e->e.getId()== finalNextIndex)){
                        tracker.setLastPublishedId(i);
                        save(tracker);
                        break;
                    }
                }
            }

        }
    }

}
