package com.example.todo.entity;

import lombok.Data;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String description;
    private boolean done;

    @JsonBackReference
    @ManyToOne
    private User owner;
}