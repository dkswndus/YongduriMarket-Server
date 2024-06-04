package Graduation.work.YongduriMarketServer.service;

import Graduation.work.YongduriMarketServer.domain.User;
import Graduation.work.YongduriMarketServer.dto.UserResponseDto;
import Graduation.work.YongduriMarketServer.exception.CustomException;
import Graduation.work.YongduriMarketServer.exception.ErrorCode;
import Graduation.work.YongduriMarketServer.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;


    // 내 정보 조회
    public UserResponseDto getInfoList(Long studentId) throws Exception{

        User user = findByStudentId(studentId);

        try{
            UserResponseDto userResponseDto = UserResponseDto.builder()
                    .studentId(user.getStudentId())
                    .nickname(user.getNickname())
                    .build();

            return userResponseDto;
        }catch (Exception e){
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }



    // 내 정보 수정
    public Boolean infoUpdate(Long studentId, UserResponseDto request)throws Exception {
        //404 studentId 없음
        User user = findByStudentId(studentId);

        //400 데이터 미입력
        if(request.getStudentId() == null){
            throw new CustomException(ErrorCode.INSUFFICIENT_DATA);
        }

        try{
            Optional<User> infoUpdate = userRepository.findById(request.getStudentId());
            infoUpdate.get().setNickname(request.getNickname());
            infoUpdate.get().setFileId(request.getFileId());
            userRepository.save(user);
            return true;
        }catch (Exception e){
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    private User findByStudentId(Long studentId) {
        return userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

    }
}
