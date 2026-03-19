package com.example.travel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreRemove;

import org.springframework.format.annotation.DateTimeFormat;

// --- 💡 JSON変換時の無限ループを防ぐためのインポート ---
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Data
public class TravelSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;
    private String kbn;       // 「計画」か「済」か
    private String category;
    
    // もともとあったdate（日記用など）
    private String date; 
    private String time;
    
    private String comment;
    private Double latitude;
    private Double longitude;

    @ManyToMany
    @JoinTable(
        name = "spot_traveler_relation",
        joinColumns = @JoinColumn(name = "spot_id"),
        inverseJoinColumns = @JoinColumn(name = "traveler_id")
    )
    // 💡 循環参照を防ぐ：Travelerクラス内の spots フィールドを無視させる
    @JsonIgnoreProperties("spots")
    private List<Traveler> travelers = new ArrayList<>(); 

    @PreRemove
    private void removeTravelersFromSpot() {
        this.travelers.clear(); // 削除する前に、紐付いている訪問者リストを空にする
    }

    // --- 💡 「線」を引くための設計項目 ---
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate visitDate; // 並び替えに使う日付（カレンダー形式）

    private Integer visitOrder;  // 並び替えに使う番号

    @ManyToOne
    @JoinColumn(name = "trip_id")
    // 💡 循環参照を防ぐ：JSON出力時に Trip 情報を深く追いかけないようにする
    @JsonIgnore
    private Trip trip;

    private String visitTime; // "10:00" のような形式で保存
}