package com.back.ourlog.domain.ott.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Ott {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String logoUrl;

    @OneToMany(mappedBy = "ott", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryOtt> diaryOtts = new ArrayList<>();

    public Ott(String name) {
        this.name = name;
    }

    public Ott(String name, String logoUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ott)) return false;
        Ott ott = (Ott) o;
        return id != null && id.equals(ott.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
