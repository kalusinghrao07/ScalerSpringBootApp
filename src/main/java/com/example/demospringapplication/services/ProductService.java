package com.example.demospringapplication.services;

import com.example.demospringapplication.exceptions.ProductNotFoundException;
import com.example.demospringapplication.models.Product;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Map;

public interface ProductService {
    Product getProductById(Long id) throws ProductNotFoundException;

    List<Product> getAllProducts();

    Product replaceProduct(Long id, Product product);

    Product createProduct(Product product);

    void deleteProduct(Long id) throws ProductNotFoundException;

    Product updateProduct(Long id, Map<String, Object> updates) throws ProductNotFoundException;
}
