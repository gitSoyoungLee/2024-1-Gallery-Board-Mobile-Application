package com.example.proj2_and_2021202039;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Board> boardList=new ArrayList<>();
    MyGridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 정의한 MyGridAdapter 변수 적용
        setTitle("그리드뷰");
        // MyGridAdapter 초기화
        gridAdapter = new MyGridAdapter(this);
        gridAdapter = new MyGridAdapter(this, boardList);
        GridView gv=(GridView)findViewById(R.id.gridView);
        gv.setAdapter(gridAdapter);

        //그리드뷰에 넣을 이미지들 가져오기
        new FetchArticlesTask().execute("http://10.0.2.2:8080/boards");

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭된 셀의 Board 객체 가져오기
                Board selectedBoard = boardList.get(position);

                // Intent를 사용하여 BoardDetailActivity로 데이터 전달
                Intent intent = new Intent(MainActivity.this, BoardDetailActivity.class);
                intent.putExtra("board", selectedBoard);
                startActivity(intent);
            }
        });


        //업로드로 이동하기
        Button uploadButton=(Button) findViewById(R.id.addButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });
    }

    //데이터베이스에서 데이터 가져오기
    private class FetchArticlesTask extends AsyncTask<String, Void, List<Board>> {

        @Override
        protected List<Board> doInBackground(String... urls) {
            List<Board> boards = new ArrayList<>();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                StringBuilder responseBuilder = new StringBuilder();
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    responseBuilder.append(current);
                    data = reader.read();
                }
                reader.close();

                // 게시물 목록 파싱
                JSONArray jsonArray = new JSONArray(responseBuilder.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    long id=jsonObject.getLong("id");
                    String title = jsonObject.getString("title");
                    String content = jsonObject.getString("content");
                    String base64Image = jsonObject.getString("image");
                    System.out.println("id: "+id);
                    System.out.println("title:"+title);
                    System.out.println("content:"+content);
                    System.out.println("image:"+base64Image);
                    byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    boards.add(new Board(id,title, content, bitmap));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return boards;
        }

        @Override
        protected void onPostExecute(List<Board> boards) {
            if (boards != null && !boards.isEmpty()) {
                boardList.addAll(boards);
                gridAdapter.notifyDataSetChanged();
            }
        }
    }
}