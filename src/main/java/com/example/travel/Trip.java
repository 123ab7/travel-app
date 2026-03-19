package com.example.travel;

import java.time.LocalDate;
import java.util.ArrayList; // 👈 これを足す！
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Entity
@Data
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;      
      private String status= "計画中";  
      private String areaKbn = "国内";
    @DateTimeFormat(pattern = "yyyy-MM-dd") 
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd") 
    private LocalDate endDate;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    @OrderBy("visitDate ASC, visitOrder ASC") 
    private List<TravelSpot> spots;

    @ManyToMany
    @JoinTable(
        name = "trip_traveler_relation", 
        joinColumns = @JoinColumn(name = "trip_id"),
        inverseJoinColumns = @JoinColumn(name = "traveler_id")
    )
 
    
    
    private List<Traveler> travelers = new ArrayList<>(); // 👈 これでエラーが消えます

    public String getAreaKbn() { return areaKbn; }
    public void setAreaKbn(String areaKbn) { this.areaKbn = areaKbn; }
}