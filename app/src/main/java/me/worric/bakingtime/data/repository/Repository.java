package me.worric.bakingtime.data.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

public interface Repository<T> {

    LiveData<List<T>> findAll();

    LiveData<T> findOneById(Long id);

    T findOneByIdNonReactive(Long id);

}
