package com.example.demospringapplication.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FakeStoreProductDto {
    Long id;
    String title;
    String description;
    Double price;
    String category;
}
