package com.codeages.framework.biz;

import java.util.List;

public interface BaseEntityMapper<D, E> {
    D toDTO(E e);

    List<D> toDTO(List<E> es);

    E toEntity(D d);

    List<E> toEntity(List<D> ds);
}
