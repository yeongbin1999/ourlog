package com.back.ourlog.domain.genre.repository;

import com.back.ourlog.domain.genre.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Integer> {

}
