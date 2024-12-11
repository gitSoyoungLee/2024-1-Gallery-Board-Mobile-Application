package com.example.Proj2_spr_2021202039;

import com.example.Proj2_spr_2021202039.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class MainController {

    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardRepository boardRepository;

    //보드 생성
    @PostMapping("/board")
    public void createBoard(@RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("image") MultipartFile image, @RequestParam(value = "id", required = false) Long id){
        if(id==null){
            try {
                System.out.println("title: "+title);
                System.out.println("content: "+content);
                // 이미지 파일을 Base64 문자열로 변환
                byte[] imageBytes = image.getBytes();
                String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

                // Article 엔티티 생성 및 저장
                Board board = new Board();
                board.setTitle(title);
                board.setContent(content);
                board.setImage(encodedImage);

                boardService.createBoard(board);

                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        else{
            return;
        }

    }

    
    // 데이터베이스에 저장된 보드 리스트를 안드로이드로 보냄
    @GetMapping("/boards")
    public List<BoardResponse> getBoardList(){
        System.out.println("boards request!");
        return boardRepository.findAll().stream()
                .map(board -> new BoardResponse(board.getId(),board.getTitle(),board.getContent(), board.getImage()))
                .collect(Collectors.toList());
    }

    public static class BoardResponse{
        private long id;
        private String title;
        private String content;
        private String image;

        public BoardResponse(long id, String title, String content, String image){
            this.id=id;
            this.title=title;
            this.content=content;
            this.image=image;
        }

        public long getId(){return id;}

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getImage() {
            return image;
        }
    }

    //삭제하기
    @PostMapping("/remove/{id}")
    public ResponseEntity<String> removeBoard(@PathVariable long id){
        System.out.println("remove request: "+ id);
        Optional<Board> boardOptional = boardRepository.findById(id);
        if (boardOptional.isPresent()) {
            boardService.deleteBoard(id);
            return ResponseEntity.ok("게시물이 삭제되었습니다.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //수정하기
    @PutMapping("/board/{id}")
    public void updateBoard(@RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("image") MultipartFile image, @RequestParam(value = "id") String id) throws IOException {
        System.out.println("update request: "+id);
        long boardId=Long.parseLong(id);
        Optional<Board> optionalBoard=boardRepository.findById(boardId);
        Board board=optionalBoard.get();
        board.setTitle(title);
        board.setContent(content);
        // 이미지 파일을 Base64 문자열로 변환
        byte[] imageBytes = image.getBytes();
        String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

        board.setImage(encodedImage);
        boardService.createBoard(board);
    }
    
}
