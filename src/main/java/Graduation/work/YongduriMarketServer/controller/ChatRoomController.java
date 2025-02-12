package Graduation.work.YongduriMarketServer.controller;

import Graduation.work.YongduriMarketServer.config.CustomUserDetails;
import Graduation.work.YongduriMarketServer.dto.ChatRoomRequestDto;
import Graduation.work.YongduriMarketServer.dto.ChatRoomResponseDto;
import Graduation.work.YongduriMarketServer.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/chatRoom")
public class ChatRoomController {

    public final ChatRoomService chatRoomService;

    //전체조회
    @GetMapping
    public ResponseEntity<List> getAllChatRoom() throws Exception{
        return new ResponseEntity<List>(chatRoomService.getAllChatRoom(), HttpStatus.OK);
    }

    //상세조회
    @GetMapping("/detail")
    public ResponseEntity<ChatRoomResponseDto> getChatRoomDetails(ChatRoomRequestDto.DetailDto request) throws Exception{
        return new ResponseEntity<ChatRoomResponseDto>(chatRoomService.getChatRoomDetails(request), HttpStatus.OK);
    }
    //방 생성
    @PostMapping
    public ResponseEntity<Boolean> createChatRoom(@AuthenticationPrincipal CustomUserDetails user, ChatRoomRequestDto.CreateDto request) throws Exception{
        return new ResponseEntity<Boolean>(chatRoomService.createChatRoom(user.getStudentId(),request), HttpStatus.OK);
    }
    // 방 삭제
    @DeleteMapping
    public ResponseEntity<Boolean> deleteChatRoom(@AuthenticationPrincipal CustomUserDetails user,ChatRoomRequestDto.DeleteDto request) throws Exception{
        return new ResponseEntity<Boolean>(chatRoomService.deleteChatRoom(user.getStudentId(),request), HttpStatus.OK);
    }





}
