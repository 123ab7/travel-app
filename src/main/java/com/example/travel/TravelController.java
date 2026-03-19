package com.example.travel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TravelController {

    @Autowired
    private TravelRepository repository;
    @Autowired
    private TravelerRepository travelerRepository;
    @Autowired
    private TripRepository tripRepository;
  
    
    
  
 // --- 地図画面 ---
 // --- 🗺️ 地図画面 ---
    @GetMapping("/map")
    public String showMap(@RequestParam(name = "tripId", required = false) Long tripId, Model model) {
        // 1. データの取得：フィルタを自由に動かすため、常に「全件」取得する
        // これにより、画面を開いた後でもJS側で自由に切り替えが可能になります
        List<TravelSpot> sourceSpots = repository.findAllByOrderByVisitDateAscVisitOrderAsc();

        // 特定の旅行が選択されている場合、タイトル表示用にTripオブジェクトを取得
        if (tripId != null) {
            Trip trip = tripRepository.findById(tripId).orElse(null);
            model.addAttribute("trip", trip);
        } else {
            model.addAttribute("trip", null);
        }

        // 2. MapSpotDtoへの詰め替え (ロジックは以前と同じでOK)
        List<MapSpotDto> dtos = sourceSpots.stream()
            .filter(s -> s.getLatitude() != null && s.getLongitude() != null)
            .map(s -> {
                MapSpotDto dto = new MapSpotDto();
                dto.setId(s.getId());
                dto.setAddress(s.getAddress());
                dto.setLatitude(s.getLatitude());
                dto.setLongitude(s.getLongitude());
                dto.setKbn(s.getKbn());
                
                if (s.getTrip() != null) {
                    dto.setTripId(s.getTrip().getId());
                }
                
                dto.setTravelers(s.getTravelers().stream().map(t -> {
                    MapSpotDto.TravelerDto td = new MapSpotDto.TravelerDto();
                    td.setId(t.getId());
                    td.setName(t.getName());
                    td.setColor(t.getColor());
                    return td;
                }).collect(Collectors.toList()));
                
                return dto;
            }).collect(Collectors.toList());

        // 3. 画面に渡すデータ
        model.addAttribute("spots", dtos);
        model.addAttribute("travelers", travelerRepository.findAll()); // 全訪問者リスト
        model.addAttribute("trips", tripRepository.findAll());         // 全旅行リスト
        model.addAttribute("tripId", tripId); // 現在の選択状態（JSがこれを見て初期フィルタをかけます）

        return "map"; 
    }
//    @GetMapping("/map")
//    public String showMap(@RequestParam(name = "tripId", required = false) Long tripId, Model model) {
//        List<TravelSpot> sourceSpots;
//        
//        // 1. データの取得
//        if (tripId != null) {
//            // ダッシュボードから来た場合：その旅行のスポットだけを取得
//            Trip trip = tripRepository.findById(tripId).orElse(null);
//            model.addAttribute("trip", trip);
//            sourceSpots = (trip != null) ? trip.getSpots() : new ArrayList<>();
//        } else {
//            // 直接「地図」を開いた場合：全スポットを取得（件数制限なしでOK）
//            sourceSpots = repository.findAllByOrderByVisitDateAscVisitOrderAsc(); // 日付順で取得
//            model.addAttribute("trip", null);
//        }
//
//        // 2. MapSpotDtoへの詰め替え
//        List<MapSpotDto> dtos = sourceSpots.stream()
//            .filter(s -> s.getLatitude() != null && s.getLongitude() != null)
//            .map(s -> {
//                MapSpotDto dto = new MapSpotDto();
//                dto.setId(s.getId());
//                dto.setAddress(s.getAddress());
//                dto.setLatitude(s.getLatitude());
//                dto.setLongitude(s.getLongitude());
//                dto.setKbn(s.getKbn());
//                
//                // 💡 ここがポイント！ JavaScriptで絞り込むために「どの旅行か」をセットする
//                if (s.getTrip() != null) {
//                    dto.setTripId(s.getTrip().getId());
//                }
//                
//                dto.setTravelers(s.getTravelers().stream().map(t -> {
//                    MapSpotDto.TravelerDto td = new MapSpotDto.TravelerDto();
//                    td.setId(t.getId());
//                    td.setName(t.getName());
//                    td.setColor(t.getColor());
//                    return td;
//                }).collect(Collectors.toList()));
//                
//                return dto;
//            }).collect(Collectors.toList());
//
//        // 3. 画面に渡すデータ（絞り込みフィールド用）
//        model.addAttribute("spots", dtos);
//        model.addAttribute("travelers", travelerRepository.findAll()); // 全訪問者リスト
//        model.addAttribute("trips", tripRepository.findAll());         // 💡 全旅行リストを追加！
//        model.addAttribute("tripId", tripId); // 今選んでいるID
//
//        return "map"; 
//    }
//    @GetMapping("/map")
//    public String showMap(@RequestParam(name = "tripId", required = false) Long tripId, Model model) {
//        List<TravelSpot> sourceSpots;
//        
//        // 1. データの元ネタを取得
//        if (tripId != null) {
//            Trip trip = tripRepository.findById(tripId).orElse(null);
//            model.addAttribute("trip", trip);
//            sourceSpots = (trip != null) ? trip.getSpots() : new ArrayList<>();
//            model.addAttribute("tripId", tripId);
//        } else {
//            // 全体表示：以前の「10件制限」を活かしつつ取得
//            sourceSpots = repository.findAll().stream().limit(10).collect(Collectors.toList());
//            model.addAttribute("trip", null);
//            model.addAttribute("tripId", null);
//        }
//
//        // 2. ★重要：MapSpotDtoへの「詰め替え」処理
//        // これにより、循環参照エラー（ERR_INCOMPLETE_CHUNKED_ENCODING）を確実に防ぎます
//        List<MapSpotDto> dtos = sourceSpots.stream()
//            .filter(s -> s.getLatitude() != null && s.getLongitude() != null) // 座標があるものだけ
//            .map(s -> {
//                MapSpotDto dto = new MapSpotDto();
//                dto.setId(s.getId());
//                dto.setAddress(s.getAddress());
//                dto.setLatitude(s.getLatitude());
//                dto.setLongitude(s.getLongitude());
//                dto.setKbn(s.getKbn());
//                
//                // 訪問者情報も軽いDtoに変換
//                dto.setTravelers(s.getTravelers().stream().map(t -> {
//                    MapSpotDto.TravelerDto td = new MapSpotDto.TravelerDto();
//                    td.setId(t.getId());
//                    td.setName(t.getName());
//                    td.setColor(t.getColor());
//                    return td;
//                }).collect(Collectors.toList()));
//                
//                return dto;
//            }).collect(Collectors.toList());
//
//        // 3. 詰め替えた「軽いデータ」をモデルに渡す
//        model.addAttribute("spots", dtos);
//        model.addAttribute("travelers", travelerRepository.findAll());
//
//        return "map"; 
//    }
    
    @GetMapping("/list")
    public String showList(@RequestParam(name = "tripId") Long tripId, Model model) {
        Trip trip = tripRepository.findById(tripId).orElseThrow();
        model.addAttribute("trip", trip);
        model.addAttribute("spots", trip.getSpots()); // これがOrderBy順（日付・時間順）で渡される
        model.addAttribute("tripId", tripId);
        return "list";
    }

    // --- 📍 スポット登録・編集 ---
    @GetMapping("/add")
    public String displayInput(
            @RequestParam(name = "tripId") Long tripId,
            // ...他の引数...
            Model model) {
        
        TravelSpot spot = new TravelSpot();
        Trip trip = tripRepository.findById(tripId).orElseThrow();
        
        // 💡 これらが input.html の th:min/max で必要です
        model.addAttribute("tripStartDate", trip.getStartDate());
        model.addAttribute("tripEndDate", trip.getEndDate());
        
        model.addAttribute("spot", spot);
        model.addAttribute("tripId", tripId);
        // ...
        return "input";
    }

//    @PostMapping("/save")
//    public String saveSpot(@ModelAttribute TravelSpot spot, 
//                           @RequestParam("tripId") Long tripId) {
//        
//        // 1. まず、紐付け先の Trip を取得
//        Trip trip = tripRepository.findById(tripId).orElseThrow();
//        
//        // 2. 画面から届いた spot に Trip をセット（これがないと保存に失敗することがあります）
//        spot.setTrip(trip);
//
//        // 💡 訪問者（Travelers）の紐付けも、画面から届いた ID リストに基づいて
//        // 自動的にこの spot オブジェクトにセットされています。
//
//        // 3. 保存実行！ 
//        // ※ spot.id に値が入っていれば、DB上の既存データが新しい「緯度・経度・住所」で更新されます。
//        travelSpotRepository.save(spot);
//
//        return "redirect:/dashboard?tripId=" + tripId;
//    }
    @PostMapping("/save")
    public String saveSpot(@ModelAttribute TravelSpot spot, 
                           @RequestParam("tripId") Long tripId, 
                           @RequestParam(name = "sort", defaultValue = "date") String sort, // 💡 追加：ソート順を受け取る
                           @RequestParam(name = "travelers", required = false) List<Long> travelerIds, // 💡 追加：訪問者のリスト
                           Model model) {
        
        Trip trip = tripRepository.findById(tripId).orElse(null);
        
        if (trip != null) {
            // 💡 期間外チェック
            if (spot.getVisitDate() != null) {
                if (spot.getVisitDate().isBefore(trip.getStartDate()) || 
                    spot.getVisitDate().isAfter(trip.getEndDate())) {
                    
                    model.addAttribute("errorMessage", "⚠️ 日付が旅行期間（" + trip.getStartDate() + " ～ " + trip.getEndDate() + "）の範囲外です！");
                    
                    // 再表示用のデータを詰め直す（現在の並び順も考慮）
                    List<TravelSpot> sortedSpots = new ArrayList<>(trip.getSpots());
                    if ("new".equals(sort)) {
                        sortedSpots.sort((a, b) -> b.getId().compareTo(a.getId()));
                    } else {
                        sortedSpots.sort(Comparator.comparing(TravelSpot::getVisitDate, Comparator.nullsLast(Comparator.naturalOrder()))
                                .thenComparing(TravelSpot::getVisitTime, Comparator.nullsLast(Comparator.naturalOrder())));
                    }

                    model.addAttribute("trip", trip);
                    model.addAttribute("spots", sortedSpots);
                    model.addAttribute("tripId", tripId);
                    model.addAttribute("travelers", travelerRepository.findAll());
                    model.addAttribute("sort", sort); // 現在のソート順を維持
                    return "dashboard"; 
                }
            }
            spot.setTrip(trip);
        }
        
        // 💡 訪問者のセット（これをしないと訪問者が保存されません）
        if (travelerIds != null && !travelerIds.isEmpty()) {
            spot.setTravelers(travelerRepository.findAllById(travelerIds));
        } else {
            spot.setTravelers(new ArrayList<>());
        }
        
        repository.save(spot);
        // 💡 修正：リダイレクト先にも sort を付ける
        return "redirect:/dashboard?tripId=" + tripId + "&sort=" + sort;
    }

    // --- 🚀 ダッシュボード ---
    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(name = "tripId", required = false) Long tripId, 
            @RequestParam(name = "sort", defaultValue = "date") String sort,
            Model model) {
        
        if (tripId == null) return "redirect:/traveler/list";

        Trip trip = tripRepository.findById(tripId).orElse(null);
        if (trip != null) {
            // 💡 修正：一旦すべてのスポットを新しいリストにコピー（ソートを安全に行うため）
            List<TravelSpot> sortedSpots = new ArrayList<>(trip.getSpots());

            if ("new".equals(sort)) {
                // IDの降順（新しく追加した順）
                sortedSpots.sort((a, b) -> b.getId().compareTo(a.getId()));
            } else {
                // 💡 日付でソート、同じ日付なら時間でソート（nullは後ろに回す）
                sortedSpots.sort(
                    Comparator.comparing(TravelSpot::getVisitDate, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(TravelSpot::getVisitTime, Comparator.nullsLast(Comparator.naturalOrder()))
                );
            }

            model.addAttribute("trip", trip);
            model.addAttribute("spots", sortedSpots); 
            model.addAttribute("travelers", travelerRepository.findAll());
            model.addAttribute("spot", new TravelSpot());
            model.addAttribute("sort", sort);
        }
        
        model.addAttribute("tripId", tripId);
        return "dashboard";
    }
//    @GetMapping("/dashboard")
//    public String dashboard(@RequestParam(name = "tripId", required = false) Long tripId, Model model) {
//        if (tripId == null) return "redirect:/traveler/list";
//
//        Trip trip = tripRepository.findById(tripId).orElse(null);
//        if (trip != null) {
//            model.addAttribute("trip", trip);
//            model.addAttribute("spots", trip.getSpots());
//            // 💡 ここ！全訪問者ではなく、tripに登録された訪問者(getTravelers)を渡す
//            model.addAttribute("travelers", trip.getTravelers()); 
//        }
//        model.addAttribute("tripId", tripId);
//        return "dashboard";
//    }

    // --- 👤 訪問者マスタ (省略なし) ---
    @GetMapping("/traveler/list")
    public String listTravelers(Model model) {
        model.addAttribute("travelers", travelerRepository.findAll());
        model.addAttribute("newTraveler", new Traveler());
        return "traveler_list";
    }

    @PostMapping("/traveler/save")
    public String saveTraveler(@RequestParam("name") String name, @RequestParam("color") String color) {
        Traveler t = new Traveler();
        t.setName(name); t.setColor(color);
        travelerRepository.save(t);
        return "redirect:/traveler/list";
    }
 // --- 🏠 旅行一覧画面 ---
    @GetMapping("/trip/list")
    public String listTrips(
            @RequestParam(name = "status", defaultValue = "all") String status,
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            Model model) {

        List<Trip> trips = new ArrayList<>(tripRepository.findAll());
        java.time.LocalDate today = java.time.LocalDate.now(); // 💡 今日の日付を取得

        // 1. フィルタリング (日付による自動判定に切り替え！)
        if ("完了".equals(status)) {
            trips = trips.stream()
                    .filter(t -> t.getEndDate() != null && t.getEndDate().isBefore(today))
                    .collect(Collectors.toList());
        } else if ("計画中".equals(status)) {
            trips = trips.stream()
                    .filter(t -> t.getEndDate() == null || !t.getEndDate().isBefore(today))
                    .collect(Collectors.toList());
        }

        // 2. ソート (ここは今のままでOK)
        if ("date".equals(sort)) {
            trips.sort(Comparator.comparing(Trip::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())));
        } else if ("oldest".equals(sort)) {
            trips.sort(Comparator.comparing(Trip::getId));
        } else {
            trips.sort((a, b) -> b.getId().compareTo(a.getId()));
        }

        model.addAttribute("trips", trips);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        model.addAttribute("today", today); // 💡 HTML側で判定に使うために追加

        return "trip_list";
    }
 /// --- ➕ 新規旅行作成フォームの表示 ---
    @GetMapping("/trip/add")
    public String addTripForm(Model model) {
        model.addAttribute("trip", new Trip());
        // 💡 訪問者リストを渡す！
        model.addAttribute("travelers", travelerRepository.findAll()); 
        return "trip_input"; 
    }
    // 旅行の編集画面を表示する
//    @GetMapping("/trip/edit")
//    public String editTrip(@RequestParam("id") Long id, Model model) {
//        // 1. 既存の旅行データを取得
//        Trip trip = tripRepository.findById(id).orElseThrow();
//        
//        // 2. モデルに詰める
//        model.addAttribute("trip", trip);
//        model.addAttribute("title", "🗓️ 旅行計画の編集");
//        
//        return "trip_input"; // 💡 新規登録でも使っているHTMLファイル
//    }
 // 編集画面（editTrip）の方にも同じように追加
    @GetMapping("/trip/edit")
    public String editTrip(@RequestParam("id") Long id, Model model) {
        Trip trip = tripRepository.findById(id).orElseThrow();
        model.addAttribute("trip", trip);
        // 💡 訪問者リストを渡す！
        model.addAttribute("travelers", travelerRepository.findAll()); 
        return "trip_input";
    }
    // --- 💾 旅行の保存処理 ---
 // --- 💾 旅行の保存処理 ---
    @PostMapping("/trip/save")
    public String saveTrip(
            @ModelAttribute Trip trip, 
            @RequestParam(name = "travelers", required = false) List<Long> travelerIds) {
        
        // 1. チェックボックスで選ばれたIDがある場合、データベースからそのEntityを取得してセットする
        if (travelerIds != null && !travelerIds.isEmpty()) {
            List<Traveler> selectedTravelers = travelerRepository.findAllById(travelerIds);
            trip.setTravelers(selectedTravelers); 
        } else {
            // 何も選ばれていない場合は空リストをセット
            trip.setTravelers(new ArrayList<>());
        }
        
        Trip savedTrip = tripRepository.save(trip);
        return "redirect:/dashboard?tripId=" + savedTrip.getId();
    }
//    @PostMapping("/trip/save")
//    public String saveTrip(@ModelAttribute Trip trip) {
//        Trip savedTrip = tripRepository.save(trip); // 保存してIDが確定したインスタンスを受け取る
//        // 保存後、そのままその旅行のダッシュボードへ！
//        return "redirect:/dashboard?tripId=" + savedTrip.getId();
//    }
//    @PostMapping("/delete")
//    public String deleteSpot(@RequestParam("id") Long id, @RequestParam("tripId") Long tripId) {
//        // スポットを削除
//        repository.deleteById(id);
//        // 削除後、その旅行のダッシュボードにリダイレクト
//        return "redirect:/dashboard?tripId=" + tripId;
//    }
    @org.springframework.transaction.annotation.Transactional
    @PostMapping("/delete")
    public String deleteSpot(@RequestParam("id") Long id, @RequestParam("tripId") Long tripId) {
        TravelSpot spot = repository.findById(id).orElse(null);
        if (spot != null) {
            spot.getTravelers().clear();
            repository.saveAndFlush(spot);
            repository.delete(spot);
        }
        return "redirect:/dashboard?tripId=" + tripId;
    }
 // 地図表示に最低限必要な項目だけの「封筒」
    
    public static class MapSpotDto {
        private Long id;
        private Long tripId;
        private String address;
        private Double latitude;
        private Double longitude;
        private String kbn;
        private List<TravelerDto> travelers;

        // 手動でGetter/Setterを定義
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public String getKbn() { return kbn; }
        public void setKbn(String kbn) { this.kbn = kbn; }
        public List<TravelerDto> getTravelers() { return travelers; }
        public void setTravelers(List<TravelerDto> travelers) { this.travelers = travelers; }
        public Long getTripId() { return tripId; }
        public void setTripId(Long tripId) { this.tripId = tripId; }
        
        public static class TravelerDto {
            private Long id;
            private String name;
            private String color;

            public Long getId() { return id; }
            public void setId(Long id) { this.id = id; }
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public String getColor() { return color; }
            public void setColor(String color) { this.color = color; }
        }
    }

}