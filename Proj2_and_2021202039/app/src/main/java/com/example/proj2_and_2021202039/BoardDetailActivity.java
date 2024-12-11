package com.example.proj2_and_2021202039;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class BoardDetailActivity extends AppCompatActivity {
    private long boardId;
    private String title;
    private String content;
    private Bitmap image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        // Intent에서 Board 객체 받기
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("board")) {
            Board board = intent.getParcelableExtra("board");
            if (board != null) {
                boardId = board.getId();
                title = board.getTitle();
                content = board.getContent();
                image = board.getImage();
                System.out.println("board ID: "+boardId);
                System.out.println("board title: "+title);
                System.out.println("board content: "+content);

                // board 객체를 사용하여 UI 업데이트 등 필요한 작업 수행
                // 예를 들어, 이미지뷰에 이미지 설정하기
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(board.getImage());

                // 제목과 내용 등을 필요에 따라 TextView에 설정하기
                TextView titleTextView = findViewById(R.id.titleTextView);
                titleTextView.setText(board.getTitle());

                TextView contentTextView = findViewById(R.id.contentTextView);
                contentTextView.setText(board.getContent());
            }

            //갤러리로 이동하기
            Button backButton = (Button) findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BoardDetailActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });


            //삭제하기
            Button deleteButton=(Button)findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DeleteBoardTask().execute(boardId);
                }
            });

            //수정하기, 일단 수정 페이지로 이동
            Button modifyButton=(Button) findViewById(R.id.modifyButton);
            modifyButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(BoardDetailActivity.this, ModifyActivity.class);
                    intent.putExtra("board", board);
                    startActivity(intent);
                }
            });
        }



    }
    // 게시물 삭제 AsyncTask
    private class DeleteBoardTask extends AsyncTask<Long, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Long... params) {
            long boardId = params[0];
            String urlString = "http://10.0.2.2:8080/remove/" + boardId;

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                int responseCode = connection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(BoardDetailActivity.this, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                // MainActivity로 돌아가기
                Intent intent = new Intent(BoardDetailActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            } else {
                Toast.makeText(BoardDetailActivity.this, "게시물 삭제 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

