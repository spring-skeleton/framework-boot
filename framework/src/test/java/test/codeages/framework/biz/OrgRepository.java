package test.codeages.framework.biz;

import com.codeages.framework.biz.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgRepository extends BaseRepository<Org> {
    public Org findByName(String name);
}
