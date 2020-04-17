package test.codeages.framework.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrgServiceImpl implements OrgService {

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private OrgMapper orgMapper;

    @Override
    public Org save(Org org) {
        return orgRepository.save(org);
    }

    @Override
    public List<Org> findAll() {
        return orgRepository.findAll();
    }

    public Org saveByDto(OrgDto orgDto){
        return orgRepository.save(orgMapper.toEntity(orgDto));
    }

    @Override
    public Page<Org> findByConditions(Map<String, Object> conditions, Pageable pageable) {
        return orgRepository.findAll(new OrgSpecification(conditions), pageable);
    }
}
