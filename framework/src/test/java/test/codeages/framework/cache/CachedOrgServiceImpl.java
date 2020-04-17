package test.codeages.framework.cache;

import com.codeages.framework.biz.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.codeages.framework.biz.Org;

@Service
public class CachedOrgServiceImpl extends BaseService implements CachedOrgService{
    @Autowired
    private CachedOrgRepository orgRepository;

    @Override
    public Org getById(Long id) {
        return orgRepository.getById(id);
    }

    public Org save(Org org){
        return orgRepository.save(org);
    }

    @Override
    public Org getByCodeAndName(String code, String name){
        return orgRepository.getByCodeAndName(code, name);
    }

    @Override
    public Org getByNameAndCode(String name, String code){
        return orgRepository.getByNameAndCode(name, code);
    }

    @Override
    public int updateNameById(Long id, String name) {
        return orgRepository.updateNameById(id,name);
    }
}
