package com.example.demospringapplication.services;

import com.example.demospringapplication.dtos.FakeStoreProductDto;
import com.example.demospringapplication.models.Category;
import com.example.demospringapplication.models.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FakeStoreProductService implements ProductService{

    RestTemplate restTemplate;

    public FakeStoreProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Override
    public Product getProductById(Long id) {
        FakeStoreProductDto fakeStoreProductDto = restTemplate.getForObject("https://fakestoreapi.com/products/" + id, FakeStoreProductDto.class);
        return convertFakeStoreProductDtoToProduct(fakeStoreProductDto);
    }

    private Product convertFakeStoreProductDtoToProduct(FakeStoreProductDto fakeStoreProductDto) {
        if(fakeStoreProductDto == null) {
            return null;
        }

        // Create a new Product instance
        Product product = new Product();

        // Map simple fields
        product.setId(fakeStoreProductDto.getId());
        product.setTitle(fakeStoreProductDto.getTitle());
        product.setDescription(fakeStoreProductDto.getDescription());
        product.setPrice(fakeStoreProductDto.getPrice());

        // Convert the category from String to Category object
        Category category = new Category();
        category.setTitle(fakeStoreProductDto.getCategory());
        // Assuming that the Category Id is not known at this point, it can be set to null or handled otherwise
        category.setId(null);  // Set the Id if it's known or retrievable from elsewhere

        // Set the category to the product
        product.setCategory(category);

        return product;
    }

}
