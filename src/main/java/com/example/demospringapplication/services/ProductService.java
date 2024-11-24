package com.example.demospringapplication.services;

import com.example.demospringapplication.models.Product;

public interface ProductService {
    Product getProductById(Long id);
}
