package com.example.demospringapplication.services;

import com.example.demospringapplication.dtos.FakeStoreProductDto;
import com.example.demospringapplication.models.Category;
import com.example.demospringapplication.models.Product;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<Product> getAllProducts() {
       FakeStoreProductDto[] fakeStoreProductDtos = restTemplate.getForObject("https://fakestoreapi.com/products", FakeStoreProductDto[].class);
       List<Product> products = new ArrayList<>();
       for (FakeStoreProductDto fakeStoreProductDto : fakeStoreProductDtos) {
           products.add(convertFakeStoreProductDtoToProduct(fakeStoreProductDto));
       }
       return products;
    }

    @Override
    public Product replaceProduct(Long id, Product product) {
        FakeStoreProductDto fakeStoreProductDto = new FakeStoreProductDto();
        fakeStoreProductDto.setTitle(product.getTitle());
        fakeStoreProductDto.setDescription(product.getDescription());
        fakeStoreProductDto.setPrice(product.getPrice());

        RequestCallback requestCallback = restTemplate.httpEntityCallback(fakeStoreProductDto, FakeStoreProductDto.class);
        ResponseExtractor<ResponseEntity<FakeStoreProductDto>> responseExtractor =
                restTemplate.responseEntityExtractor(FakeStoreProductDto.class);

        FakeStoreProductDto fakeStoreProductDto1 = restTemplate.execute("https://fakestoreapi.com/products/" + id, HttpMethod.PUT, requestCallback, responseExtractor)
                .getBody();
        return convertFakeStoreProductDtoToProduct(fakeStoreProductDto1);
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
