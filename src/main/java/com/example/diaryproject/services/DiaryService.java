package com.example.diaryproject.services;

import com.example.diaryproject.Data.models.Diary;
import com.example.diaryproject.dtos.requests.CreateDiaryRequest;
import com.example.diaryproject.dtos.requests.LoginDiaryRequest;
import com.example.diaryproject.dtos.responses.CreateDiaryResponse;
import com.example.diaryproject.dtos.responses.LoginDiaryResponse;
import com.example.diaryproject.exceptions.DiaryDoesNotExistException;
import com.example.diaryproject.exceptions.WrongPasswordException;

import java.util.Collection;
import java.util.List;

public interface DiaryService {

    CreateDiaryResponse createDiary(CreateDiaryRequest createDiaryRequest);

    LoginDiaryResponse loginDiary(LoginDiaryRequest loginDiaryRequest) throws WrongPasswordException, DiaryDoesNotExistException;

    List<Diary> findAllDairy();

    long count();
}
