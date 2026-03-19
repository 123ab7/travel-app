package com.example.travel; // 1. パッケージ名が .travel まであるか確認

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.travel.Trip; // 2. これを書き足す（Tripクラスを読み込む）

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    // ここは空っぽでも大丈夫です
}