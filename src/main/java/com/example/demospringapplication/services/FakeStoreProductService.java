package com.example.demospringapplication.services;

import com.example.demospringapplication.dtos.FakeStoreProductDto;
import com.example.demospringapplication.exceptions.ProductNotFoundException;
import com.example.demospringapplication.exceptions.ProductValidationException;
import com.example.demospringapplication.models.Category;
import com.example.demospringapplication.models.Product;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import javax.management.InstanceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FakeStoreProductService implements ProductService{

    RestTemplate restTemplate;

    public FakeStoreProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Override
    public Product getProductById(Long id) throws ProductNotFoundException {
        FakeStoreProductDto fakeStoreProductDto = restTemplate.getForObject("https://fakestoreapi.com/products/" + id, FakeStoreProductDto.class);
        if(fakeStoreProductDto == null){
           throw new ProductNotFoundException(404L, "Product not found for id: " + id);
        }
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

        FakeStoreProductDto fakeStoreProductResponse = restTemplate.execute("https://fakestoreapi.com/products/" + id, HttpMethod.PUT, requestCallback, responseExtractor)
                .getBody();
        return convertFakeStoreProductDtoToProduct(fakeStoreProductResponse);
    }

    @Override
    public Product createProduct(Product product) {
        // Validate the product object
        if (product == null) {
            throw new ProductValidationException("Product cannot be null");
        }

        // Validate product fields
        if (product.getTitle() == null) {
            throw new ProductValidationException("Product title cannot be null");
        }

        if (product.getDescription() == null) {
            throw new ProductValidationException("Product description cannot be null");
        }

        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new ProductValidationException("Product price should not be negative or zero");
        }

        // Populate DTO
        FakeStoreProductDto fakeStoreProductDto = new FakeStoreProductDto();
        fakeStoreProductDto.setTitle(product.getTitle());
        fakeStoreProductDto.setDescription(product.getDescription());
        fakeStoreProductDto.setPrice(product.getPrice());

        // Prepare and send the REST request
        RequestCallback requestCallback = restTemplate.httpEntityCallback(fakeStoreProductDto, FakeStoreProductDto.class);
        ResponseExtractor<ResponseEntity<FakeStoreProductDto>> responseExtractor =
                restTemplate.responseEntityExtractor(FakeStoreProductDto.class);

        FakeStoreProductDto fakeStoreProductResponse = restTemplate.execute(
                "https://fakestoreapi.com/products/",
                HttpMethod.POST,
                requestCallback,
                responseExtractor
        ).getBody();

        // Convert the response DTO to a Product object
        return convertFakeStoreProductDtoToProduct(fakeStoreProductResponse);
    }

    @Override
    public void deleteProduct(Long id) throws ProductNotFoundException {
        Product product = getProductById(id);
        if (product == null) {
            throw new ProductNotFoundException(404L, "Product not found for id: " + id);
        }
        // Since DELETE usually does not require a request body, RequestCallback is null
        ResponseExtractor<ResponseEntity<Void>> responseExtractor =
                restTemplate.responseEntityExtractor(Void.class);

        // Execute the DELETE request
        restTemplate.execute(
                "https://fakestoreapi.com/products/" + id,
                HttpMethod.DELETE,
                null,
                responseExtractor
        );
    }

    @Override
    public Product updateProduct(Long id, Map<String, Object> updates) throws ProductNotFoundException {
        Product product = getProductById(id);
        if (product == null) {
            throw new ProductNotFoundException(404L, "Product not found for id: " + id);
        }
        FakeStoreProductDto fakeStoreProductDto = convertProductToFakeStoreProductDto(product);
        // Update fields dynamically
        updates.forEach((key, value) -> {
            switch (key) {
                case "title":
                    fakeStoreProductDto.setTitle((String) value);
                    break;
                case "description":
                    fakeStoreProductDto.setDescription((String) value);
                    break;
                case "price":
                    Double price = (Double) value;
                    if (price <= 0) {
                        throw new IllegalArgumentException("Price should not be negative or zero");
                    }
                    fakeStoreProductDto.setPrice(price);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field: " + key);
            }
        });

        // Prepare the request with updated fields
        RequestCallback requestCallback = restTemplate.httpEntityCallback(updates, Map.class);

        // Extract the response
        ResponseExtractor<ResponseEntity<FakeStoreProductDto>> responseExtractor =
                restTemplate.responseEntityExtractor(FakeStoreProductDto.class);

        // Execute the PATCH request
        // PATCH is not working so use PUT for now.
        FakeStoreProductDto updatedProductResponse = restTemplate.execute(
                "https://fakestoreapi.com/products/" + id,
                HttpMethod.PUT,
                requestCallback,
                responseExtractor
        ).getBody();

        return convertFakeStoreProductDtoToProduct(updatedProductResponse);

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

    private FakeStoreProductDto convertProductToFakeStoreProductDto(Product product) {
        if (product == null) {
            return null;
        }

        // Create a new FakeStoreProductDto instance
        FakeStoreProductDto fakeStoreProductDto = new FakeStoreProductDto();

        // Map simple fields
        fakeStoreProductDto.setId(product.getId());
        fakeStoreProductDto.setTitle(product.getTitle());
        fakeStoreProductDto.setDescription(product.getDescription());
        fakeStoreProductDto.setPrice(product.getPrice());

        // Convert the category from Category object to a String
        Category category = product.getCategory();
        if (category != null) {
            fakeStoreProductDto.setCategory(category.getTitle()); // Assuming `category.getTitle()` gives the category name as String
        } else {
            fakeStoreProductDto.setCategory(null); // Or handle differently if needed
        }
        return fakeStoreProductDto;
    }


}
