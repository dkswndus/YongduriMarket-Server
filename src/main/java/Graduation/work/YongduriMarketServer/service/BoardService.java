package Graduation.work.YongduriMarketServer.service;

import Graduation.work.YongduriMarketServer.domain.Board;
import Graduation.work.YongduriMarketServer.domain.BoardLike;
import Graduation.work.YongduriMarketServer.domain.User;
import Graduation.work.YongduriMarketServer.domain.state.TradeStatus;
import Graduation.work.YongduriMarketServer.dto.BoardRequestDto;
import Graduation.work.YongduriMarketServer.dto.BoardResponseDto;
//import Graduation.work.YongduriMarketServer.dto.BoardResponseSavedIdDto;
import Graduation.work.YongduriMarketServer.exception.CustomException;
import Graduation.work.YongduriMarketServer.exception.ErrorCode;
import Graduation.work.YongduriMarketServer.repository.BoardRepository;
import Graduation.work.YongduriMarketServer.repository.LikeRepository;
import Graduation.work.YongduriMarketServer.repository.UserRepository;
//import com.google.api.gax.rpc.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;


    //게시글 전체 조회
    public List<BoardResponseDto> getAllBoards() throws Exception{
        List<Board> board = boardRepository.findByOrderByCreatedAtDesc();
        List<BoardResponseDto> getListDto = new ArrayList<>();
        board.forEach(s-> getListDto.add(BoardResponseDto.getBoardDto(s)));
        return getListDto;
    }

    // 게시글  상세 조회
    public BoardResponseDto getBoardDetail(BoardRequestDto.DetailDto request)throws Exception {
        Board board= findByBoardId(request.getBoardId());
        try{
            return BoardResponseDto.getBoardDto(board);
        }catch (Exception e){
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    // 게시글 작성
    public Boolean createBoard(Long studentId, BoardRequestDto.CreateDto request) throws Exception{
        User user = findByStudentId(studentId);
        if(request.getBoardTitle().isEmpty() || request.getBoardContent().isEmpty() ||
        request.getPrice() == null || request.getSales() == null || request.getPlace() == null
                || request.getMethod() == null){
            throw new CustomException(ErrorCode.INSUFFICIENT_DATA);
        }
        try{
            Board board = Board.builder()
                    .user(user)
                    .place(request.getPlace())
                    .method(request.getMethod())
                    .status(TradeStatus.판매중)
                    .sales(request.getSales())
                    .boardTitle(request.getBoardTitle())
                    .boardContent(request.getBoardContent())
                    .price(request.getPrice())
                    .build();
            boardRepository.save(board);
            return true;
        }catch (Exception e){
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    //게시글 수정
    public Boolean updateBoard(Long studentId, BoardRequestDto.UpdateDto request) throws Exception{
        User user = findByStudentId(studentId);
        Board board= findByBoardId(request.getBoardId());
        if(request.getBoardId() == null){
            throw new CustomException(ErrorCode.INSUFFICIENT_DATA);
        }
        if(request.getBoardTitle().isEmpty() || request.getBoardContent().isEmpty() ||
                request.getPrice() == null || request.getSales() == null || request.getPlace() == null
                || request.getMethod() == null){
            throw new CustomException(ErrorCode.INSUFFICIENT_DATA);
        }

        //자기가 쓴 글이 아닐 때
        if(!board.getUser().getStudentId().equals(studentId)){
            throw new CustomException(ErrorCode.NO_AUTH);

        }
        try{
            board.setBoardTitle(request.getBoardTitle());
            board.setBoardContent(request.getBoardContent());
            board.setPrice(request.getPrice());
            board.setSales(request.getSales());
            board.setPlace(request.getPlace());
            board.setMethod(request.getMethod());
            boardRepository.save(board);
            return true;
        }catch (Exception e){
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //게시글 삭제
    public Boolean deleteBoard(Long studentId, BoardRequestDto.DeleteDto request)throws Exception{
        User user = findByStudentId(studentId);
        Board board= findByBoardId(request.getBoardId());
        if(request.getBoardId() == null){
            throw new CustomException(ErrorCode.INSUFFICIENT_DATA);
        }
        if(!board.getUser().getStudentId().equals(studentId)){
            throw new CustomException(ErrorCode.NO_AUTH);
        }
        try{
            boardRepository.deleteById(request.getBoardId());
            return true;
        }catch (Exception e){
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    // 게시글 좋아요
    public Boolean likeBoard(Long studentId, BoardRequestDto.LikeDto request) throws Exception{
        User user = findByStudentId(studentId);
        Board board = findByBoardId(request.getBoardId());
        //Board existingLike = findByBoardAndUser(board,user);
        Optional<BoardLike> existingLike = likeRepository.findByBoardAndUser(board, user);
        //400 데이터 미입력
        if(request.getBoardId() == null){
            throw new CustomException(ErrorCode.INSUFFICIENT_DATA);
        }
        if (existingLike.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE);
        }
        if(!board.getUser().getStudentId().equals(studentId)){
            throw new CustomException(ErrorCode.NO_AUTH);
        }
        try{
            BoardLike like = BoardLike.builder()
                    .user(user)
                    .board(board)
                    .build();
            likeRepository.save(like);
            // 게시글의 좋아요 수 증가
            board.setLikeCount(board.getLikeCount() + 1);
            boardRepository.save(board);

            return true;
        }catch (Exception e){

            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    // 게시글 좋아요 해제
    public Boolean unlikeBoard(Long studentId, BoardRequestDto.UnLikeDto request) throws Exception{
        User user = findByStudentId(studentId);
        Board board= findByBoardId(request.getBoardId());
        Optional<BoardLike> existingLike = likeRepository.findByBoardAndUser(board, user);
        //400 데이터 미입력
        if(request.getBoardId() == null){
            throw new CustomException(ErrorCode.INSUFFICIENT_DATA);
        }
        if(!board.getUser().getStudentId().equals(studentId)){
            throw new CustomException(ErrorCode.NO_AUTH);
        }

        BoardLike boardLike = likeRepository.findByBoardAndUser(board, user)
                .orElseThrow(() -> new CustomException(ErrorCode.INSUFFICIENT_DATA));
        try{
            likeRepository.delete(boardLike);
            // 게시글의 좋아요 수 감소
            board.setLikeCount(board.getLikeCount() - 1);
            boardRepository.save(board);


            return true;
        }catch (Exception e){
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }





    //거래 완료
    public Boolean endTrade(Long studentId, BoardRequestDto.EndTradeDto request) {
        User user = findByStudentId(studentId);
        Board board= findByBoardId(request.getBoardId());
        //400 데이터 미입력
        if(request.getBoardId() == null){
            throw new CustomException(ErrorCode.INSUFFICIENT_DATA);
        }
        //자기가 쓴 글이 아닐 때
        if(!board.getUser().getStudentId().equals(studentId)){
            throw new CustomException(ErrorCode.NO_AUTH);
        }
        try{
            board.setStatus(TradeStatus.거래완료);
            boardRepository.save(board);

            return true;
        }catch (Exception e){
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //거래 예약
    public Boolean reserveTrade(Long studentId,BoardRequestDto.ReserveTradeDto request) {
        User user = findByStudentId(studentId);
        Board board= findByBoardId(request.getBoardId());
        //400 데이터미입력
        if(request.getBoardId() == null){
            throw new CustomException(ErrorCode.INSUFFICIENT_DATA);
        }
        //자기가 쓴 글이 아닐 때
        if(!board.getUser().getStudentId().equals(studentId)){
            throw new CustomException(ErrorCode.NO_AUTH);
        }
        try{
            board.setStatus(TradeStatus.거래예약);

            boardRepository.save(board);

            return true;
        }catch (Exception e){
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }



    public User findByStudentId(Long studentId) {
        return userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
    }
    public Board findByBoardId(Long boardId) {
        return boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
    }
    public Board findByBoardAndUser(Board board, User user) {
        return boardRepository.findByBoardAndUser(board, user)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_AUTH));
    }



}