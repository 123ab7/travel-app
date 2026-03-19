package com.example.travel;

import jakarta.persistence.*; // 全てインポート

@Entity
@Table(name = "traveler") // テーブル名を明示的に指定
public class Traveler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ←これが「SQL側で新しい行を作る」スイッチです
    private Long id;

    private String name;
    private String color;

    // もし空のコンストラクタがなければ追加してください
    public Traveler() {}

    // ゲッターとセッター
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}