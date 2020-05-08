package com.codeages.framework.biz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Date;
import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long>, JpaSpecificationExecutor {
    @Override
    @Deprecated
    T getOne(Long aLong);

    public T getById(Long id);

    List<BaseEntity> findByUpdatedTimeBetween(Date startDate, Date endDate);
}
