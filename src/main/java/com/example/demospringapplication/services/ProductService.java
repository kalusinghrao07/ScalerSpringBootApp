package com.example.demospringapplication.services;

import com.example.demospringapplication.models.Product;

import java.util.List;

public interface ProductService {
    Product getProductById(Long id);

    List<Product> getAllProducts();

    Product replaceProduct(Long id, Product product);
}
