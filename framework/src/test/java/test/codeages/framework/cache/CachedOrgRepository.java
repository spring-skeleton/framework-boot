package test.codeages.framework.cache;

import com.codeages.framework.biz.BaseRepository;
import com.codeages.framework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import test.codeages.framework.biz.Org;

import javax.transaction.Transactional;

public interface CachedOrgRepository extends BaseRepository<Org> {
    @Cache
    Org getById(Long id);

    @Cache
    Org getByCodeAndName(String code, String name);

    @Cache
    Org getByNameAndCode(String name, String code);

    @Query(value = "SELECT * FROM Org WHERE name = ?1 order by id",
            countQuery = "SELECT count(*) FROM Org WHERE name = ?1",
            nativeQuery = true)
    Page<Org> findByName(String name, Pageable pageable);

    @Caching(evict = {@CacheEvict(cacheNames = "default", allEntries = true)})
    @Modifying
    @Query("update Org set name=?2 where id=?1")
    @Transactional
    public int updateNameById(Long id, String name);
}
