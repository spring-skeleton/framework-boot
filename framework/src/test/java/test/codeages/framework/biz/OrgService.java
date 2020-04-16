package test.codeages.framework.biz;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface OrgService {
    Org save(Org role);

    public List<Org> findAll();

    Org saveByDto(OrgDto orgDto);

    Page<Org> findByConditions(Map<String, Object> conditions, Pageable pageable);
}
