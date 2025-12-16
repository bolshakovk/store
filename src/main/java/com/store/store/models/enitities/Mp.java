package com.store.store.models.enitities;

import jakarta.persistence.*;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "mp")
public class Mp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // название МП, если требуется

    @ManyToOne(optional = false)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "mp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackingList> packingLists;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public List<PackingList> getPackingLists() {
        return packingLists;
    }

    public void setPackingLists(List<PackingList> packingLists) {
        this.packingLists = packingLists;
    }

    // getters/setters
}