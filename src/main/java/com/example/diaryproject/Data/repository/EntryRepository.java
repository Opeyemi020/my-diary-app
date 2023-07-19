package com.example.diaryproject.Data.repository;


import com.example.diaryproject.Data.models.Entry;

public interface EntryRepository {
    void create(Entry entry);

    int count();

    Entry findById(int id);
    Entry findByTitle(String title);

    void deleteByTitle(int id);
    Entry deleteByTitle(String title);

    int update(Entry entry);

    void deleteA();
}
