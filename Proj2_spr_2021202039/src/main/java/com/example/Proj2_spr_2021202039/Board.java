package com.example.Proj2_spr_2021202039;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@EntityListeners({AuditingEntityListener.class})
@NoArgsConstructor
public class Board {
    @Id
    @Column(name="id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 데이터별 아이디
    private long id;

    //제목
    @Column
    private String title;

    //내용
    @Column
    private String content;

    //이미지
    @Lob
    @Column(columnDefinition = "TEXT")
    private String image;

}

