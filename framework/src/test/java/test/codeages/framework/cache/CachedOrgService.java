package test.codeages.framework.cache;

import test.codeages.framework.biz.Org;

public interface CachedOrgService {
    public Org getById(Long id);

    public void deleteById(Long id);

    public Org save(Org org);

    public Org getByCodeAndName(String code, String name);

    public Org getByNameAndCode(String name, String code);

    public int updateNameById(Long id, String name);
}
