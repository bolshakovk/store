package com.store.store.models.enitities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "packing_list")
public class PackingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String drawingNumber; // номер чертежа

    @Column(nullable = false)
    private String markNumber; // номер марки

    @Column(nullable = false)
    private String name; // наименование

    @Column(nullable = false)
    private Integer quantity; // количество штук

    @Column(nullable = false)
    private String mpCode; // монтажная партия (строка)

    @Column(nullable = false)
    private Double weight; // вес в кг

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "mp_id")
    private Mp mp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarkNumber() {
        return markNumber;
    }

    public void setMarkNumber(String markNumber) {
        this.markNumber = markNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDrawingNumber() {
        return drawingNumber;
    }

    public void setDrawingNumber(String drawingNumber) {
        this.drawingNumber = drawingNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getMpCode() {
        return mpCode;
    }

    public void setMpCode(String mpCode) {
        this.mpCode = mpCode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @JsonIgnore
    public Mp getMp() {
        return mp;
    }

    public void setMp(Mp mp) {
        this.mp = mp;
    }

    // Virtual getters for JSON serialization
    public String getMpName() {
        return mp != null ? mp.getName() : "";
    }

    public String getStoreName() {
        return mp != null && mp.getStore() != null ? mp.getStore().getName() : "";
    }

    // getters/setters
}