package com.mt.access.port.adapter.persistence.image;

import com.mt.access.domain.model.image.Image;
import com.mt.access.domain.model.image.ImageQuery;
import com.mt.access.domain.model.image.ImageRepository;
import com.mt.access.domain.model.image.Image_;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaImageRepository extends JpaRepository<Image, Long>, ImageRepository {

    default void add(Image address) {
        save(address);
    }

    default SumPagedRep<Image> query(ImageQuery query) {
        QueryUtility.QueryContext<Image> context = QueryUtility.prepareContext(Image.class, query);
        Optional.ofNullable(query.getIds()).ifPresent(
            e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId)
                .collect(Collectors.toSet()), Image_.IMAGE_ID, context));
        Order order = null;
        if (Checker.isTrue(query.getSort().getById())) {
            order =
                QueryUtility.getDomainIdOrder(Image_.IMAGE_ID, context, query.getSort().getIsAsc());
        }
        context.setOrder(order);
        return QueryUtility.nativePagedQuery(query, context);
    }

}
