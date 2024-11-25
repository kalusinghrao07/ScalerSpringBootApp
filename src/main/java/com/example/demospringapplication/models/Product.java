package com.example.demospringapplication.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    Long id;
    String title;
    String description;
    Double price;
    Category category;
}
