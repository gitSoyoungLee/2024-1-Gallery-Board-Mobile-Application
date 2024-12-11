package com.example.proj2_and_2021202039;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class MyGridAdapter extends BaseAdapter {
    Context context;
    private List<Board> boards;

    public MyGridAdapter(Context c){
        this.context=c;
    }

    public MyGridAdapter(Context c, List<Board> boards){
        this.context=c;
        this.boards=boards;
    }


    @Override
    public int getCount() {
        return boards != null ? boards.size() : 0;
        //return pictureID.length;
    }

    @Override
    public Object getItem(int position) {
        return boards != null ? boards.get(position) : null;
//        return pictureID[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView=new ImageView(context);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        // 셀 크기 계산
        int cellWidth = (width - (2 * 10)) / 3;  // 3개의 열, 여백 고려 (2*10dp)
        int cellHeight = (height- 150 - (3 * 10)) / 4;  // 4개의 행, 여백 고려 (3*10dp)

        imageView.setLayoutParams(new ViewGroup.LayoutParams(cellWidth,cellHeight));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(1, 1, 1, 1);

        if(boards.get(i)==null) return imageView;
        imageView.setImageBitmap(boards.get(i).getImage());

        // 각 셀 클릭 시 정보 보기
        imageView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                Intent intent = new Intent(context, BoardDetailActivity.class);
                intent.putExtra("board", boards.get(i));
                context.startActivity(intent);
            }
        });
        return imageView;

    }
}
