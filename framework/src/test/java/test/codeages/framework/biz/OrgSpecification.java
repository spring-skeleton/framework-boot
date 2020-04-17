package test.codeages.framework.biz;

import com.codeages.framework.biz.BaseSpecification;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


public class OrgSpecification extends BaseSpecification<Org> {
    public OrgSpecification(Map<String, Object> conditions) {
        super(conditions);
    }

    @Override
    public Predicate toPredicate(Root<Org> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> list = new ArrayList<Predicate>();
        if (null != conditions) {
            if (conditions.containsKey("name")) {
                list.add(cb.equal(root.get("name").as(String.class), this.conditions.get("name")));
            }

            if (conditions.containsKey("nameLike")) {
                list.add(cb.like(root.get("name").as(String.class), "%" + this.conditions.get("nameLike") + "%"));
            }
        }

        Predicate[] p = new Predicate[list.size()];
        return cb.and(list.toArray(p));
    }

}
