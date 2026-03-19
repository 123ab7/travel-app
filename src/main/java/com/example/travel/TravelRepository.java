package com.example.travel;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TravelRepository extends JpaRepository<TravelSpot, Long> {
	// 日付と順番で並び替えて取得する魔法のメソッドを追加
    List<TravelSpot> findAllByOrderByVisitDateAscVisitOrderAsc();
}