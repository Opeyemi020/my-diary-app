package com.example.diaryproject.services.diaryService;

import com.example.diaryproject.data.models.Diary;
import com.example.diaryproject.data.models.Entry;
import com.example.diaryproject.data.repository.DiaryRepository;
import com.example.diaryproject.dtos.requests.*;
import com.example.diaryproject.dtos.responses.CreateDiaryResponse;
import com.example.diaryproject.dtos.responses.CreateEntryResponse;
import com.example.diaryproject.dtos.responses.DeleteDiaryResponse;
import com.example.diaryproject.dtos.responses.LoginDiaryResponse;
import com.example.diaryproject.exceptions.DiaryDoesNotExistException;
import com.example.diaryproject.exceptions.DiaryUsernameAlreadyExistExceptions;
import com.example.diaryproject.exceptions.WrongPasswordException;
import com.example.diaryproject.services.EntryService;
import com.example.diaryproject.services.mailService.MailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.diaryproject.utils.AppUtils.ERROR_MESSAGE;
import static com.example.diaryproject.utils.Mapper.map;
import static com.example.diaryproject.utils.Mapper.mapResponse;


@Service
@AllArgsConstructor
public class DiaryServiceImplementation implements DiaryService {

    private final DiaryRepository diaryRepository;
    private EntryService entryService;
    private MailService mailService;


    @Override
    public CreateDiaryResponse createDiary(CreateDiaryRequest createDiaryRequest) {//throws InvalidEmailAddressException {
        CreateDiaryResponse createDiaryResponse;
        try {
            createDiaryResponse = new CreateDiaryResponse();
            SendMailRequest sendMailRequest = SendMailRequest.builder()
                    .from("laycon122@gmail.com")
                    .subject("Testing mail")
                    .text("testing testing")
                    .to("codingwithsultan@gmail.com")
                    .build();
            mailService.sendMail(sendMailRequest);
            boolean validatedEmailAddress = validateEmailAddress(createDiaryRequest);
            validateDuplicateUsername(createDiaryRequest);
            if (validatedEmailAddress) {
                Diary diary = map(createDiaryRequest);
                diary.setId("");

                diaryRepository.save(diary);
                return mapResponse(diary);
            }
        } catch (DiaryUsernameAlreadyExistExceptions er) {
            createDiaryResponse = new CreateDiaryResponse();
            createDiaryResponse.setMessage(ERROR_MESSAGE);
            return createDiaryResponse;
        }
        return createDiaryResponse;
    }

    @Override
    public LoginDiaryResponse loginDiary(LoginDiaryRequest loginDiaryRequest) throws WrongPasswordException, DiaryDoesNotExistException {
        Diary foundDiary = diaryRepository.findByUsername(loginDiaryRequest.getUsername());
        if (foundDiary == null)
            throw new DiaryDoesNotExistException("Username Does Not Exist, Kindly Enter a Valid Username ->");
        boolean isLoggedIn;
        if (!Objects.equals(foundDiary.getPassword(), loginDiaryRequest.getPassword())) {
            throw new WrongPasswordException("Wrong password, please Enter correct password to Login to Diary");
        } else if (foundDiary.getPassword().equals(loginDiaryRequest.getPassword()))
            isLoggedIn = true;
        LoginDiaryResponse loginDiaryResponse = new LoginDiaryResponse();
        loginDiaryResponse.setId(foundDiary.getId());
        LocalDateTime loginTime = LocalDateTime.now();
        String loginMessage = getLoginMessage(loginTime);
        loginDiaryResponse.setMessage(loginMessage);
        return loginDiaryResponse;
    }

    private static String getLoginMessage(LocalDateTime loginTime) {
        DateTimeFormatter myDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = loginTime.format(myDateFormat);
        return "Diary Successfully Logged In at " +
                formattedDateTime;
    }

    @Override
    public List<Diary> findAllDairy() {
        return diaryRepository.findAll();
    }

    @Override
    public long count() {
        return diaryRepository.count();
    }
    public CreateEntryResponse createEntry(CreateEntryRequest createEntryRequest){
        for (Diary diary: findAllDairy()) {
            diary.getEntries().add(entryService.createEntry(createEntryRequest));
            new Entry();
            return null;
        }

        return null;
    }

    @Override
    public void deleteAll() {
        diaryRepository.deleteAll();
    }


    @Override
    public DeleteDiaryResponse deleteDiary(DeleteDiaryRequest deleteDiaryRequest) throws DiaryDoesNotExistException {
        if (deleteDiaryRequest.getDiaryId()!=null) {
            findAllDairy().removeIf(diary -> Objects.equals(diary.getId(), deleteDiaryRequest.getDiaryId()));
            findAllDairy().removeIf(diary -> Objects.equals(diary.getUsername(), deleteDiaryRequest.getUsername()));
        }
        throw new DiaryDoesNotExistException("Diary does not exist ");
    }
    private void validateDuplicateUsername(CreateDiaryRequest createDiaryRequest) {
        boolean usernameExist = confirmUsername(createDiaryRequest);
        if (usernameExist) throw new DiaryUsernameAlreadyExistExceptions("username already Exist, kindly enter a valid username :");

    }

    private boolean confirmUsername(CreateDiaryRequest createDiaryRequest) {
        Diary diary = diaryRepository.findByUsername(createDiaryRequest.getUsername());
        return diary != null;
    }
    private static boolean validateEmailAddress(CreateDiaryRequest createDiaryRequest){
        String regex = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.com|semicolon\\.africa|inbox\\.com|iCloud\\.com|Mail\\.com|outlook\\.com)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(createDiaryRequest.getEmailAddress());
        return matcher.matches();
    }

}
