package com.sales_api.domain.service;

import com.sales_api.Infrastructure.repository.SaleRepository;
import com.sales_api.Infrastructure.repository.UserRepository;
import com.sales_api.Infrastructure.repository.ProductRepository;
import com.sales_api.domain.dtos.request.SaleRequestDto;
import com.sales_api.domain.dtos.response.SaleResponseDto;
import com.sales_api.domain.entities.Sale;
import com.sales_api.domain.entities.User;
import com.sales_api.domain.entities.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaleServiceImpl implements SaleServiceInterface {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public SaleServiceImpl(SaleRepository saleRepository,
                           UserRepository userRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // POST method implementation
    @Override
    public SaleResponseDto save(SaleRequestDto saleRequestDto) {
        // Fetching the User entity based on the given "user_id"
        User user = userRepository.findById(saleRequestDto.getUser_id()).orElseThrow(()
                -> new RuntimeException("The given \"user_id\":" + saleRequestDto.getUser_id() +
                ", is not related to an existing User!"));


        // Fetching the Product entity based on the given " product_id"
        Product product = productRepository.findById(saleRequestDto.getProduct_id()).orElseThrow(()
                -> new RuntimeException("The given \"product_id\":" + saleRequestDto.getProduct_id() +
                ", is not related to an existing Product!"));

        // Validation of the given "quantity" on top of Product.quantity
        if (product.getQuantity() < saleRequestDto.getQuantity()) {
            throw new RuntimeException("The product related to the sale doesn't have enough stock!\n" +
                "The actual stock of: " + product.getName() + " is " + product.getQuantity() + "units.");
        }

        // Saving the Sale with the given validated data
        Sale sale = new Sale();
        sale.setQuantity(saleRequestDto.getQuantity());
        sale.setPrice(product.getPrice() * saleRequestDto.getQuantity());
        sale.setUser(user);
        sale.setProduct(product);

        Sale savedSale = saleRepository.save(sale);

        // Update Product.quantity upon Sale registering and given "quantity"
        product.setQuantity(product.getQuantity() - sale.getQuantity());
        productRepository.save(product);

        SaleResponseDto saleResponseDto = new SaleResponseDto();
        saleResponseDto.setId(savedSale.getId());
        saleResponseDto.setQuantity(savedSale.getQuantity());
        saleResponseDto.setPrice(savedSale.getPrice());
        saleResponseDto.setUser_id(savedSale.getUser().getId());
        saleResponseDto.setProduct_id(savedSale.getProduct().getId());

        return saleResponseDto;
    }

    // GET method implementation
    public SaleResponseDto getSale(Long id) {
        // Looks for a Sale based on the give "id"
        Sale existingSale = saleRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Sale not found!\n" +
                "The given id:" + id + ", is not related to an existing Sale!"));

        SaleResponseDto saleResponseDto = new SaleResponseDto();
        saleResponseDto.setId(existingSale.getId());
        saleResponseDto.setQuantity(existingSale.getQuantity());
        saleResponseDto.setPrice(existingSale.getPrice());
        saleResponseDto.setUser_id(existingSale.getUser().getId());
        saleResponseDto.setProduct_id(existingSale.getProduct().getId());

        return saleResponseDto;
    }

    // PUT method implementation
    public SaleResponseDto updateSale(Long id, SaleRequestDto saleRequestDto) {
        // Looks for a Sale based on the give "id"
        Sale existingSale = saleRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Sale not found!\n" +
                "The given id:" + id + ", is not related to an existing Sale!"));

        // Fetching the User entity based on the given "user_id"
        User user = userRepository.findById(saleRequestDto.getUser_id()).orElseThrow(()
                -> new RuntimeException("The given \"user_id\":" + saleRequestDto.getUser_id() +
                ", is not related to an existing User!"));

        // Fetching the Product entity based on the given " product_id"
        Product product = productRepository.findById(saleRequestDto.getProduct_id()).orElseThrow(()
                -> new RuntimeException("The given \"product_id\":" + saleRequestDto.getProduct_id() +
                ", is not related to an existing Product!"));

        // Validation of the given "quantity" on top of Product.quantity
        if (product.getQuantity() < saleRequestDto.getQuantity()) {
            throw new RuntimeException("The product related to the sale doesn't have enough stock!\n" +
                    "The actual stock of: " + product.getName() + " is: " + product.getQuantity() + " units.");
        }

        // Updating the Sale with the given validated data
        existingSale.setQuantity(saleRequestDto.getQuantity());
        existingSale.setPrice(product.getPrice() * saleRequestDto.getQuantity());
        existingSale.setUser(user);
        existingSale.setProduct(product);

        saleRepository.save(existingSale);

        // Update Product.quantity upon Sale update and given "quantity"
        product.setQuantity(product.getQuantity() - saleRequestDto.getQuantity());
        productRepository.save(product);

        SaleResponseDto saleResponseDto = new SaleResponseDto();
        saleResponseDto.setId(existingSale.getId());
        saleResponseDto.setQuantity(existingSale.getQuantity());
        saleResponseDto.setPrice(existingSale.getPrice());
        saleResponseDto.setUser_id(existingSale.getUser().getId());
        saleResponseDto.setProduct_id(existingSale.getProduct().getId());

        return saleResponseDto;
    }

    //DELETE method implementation
    public SaleResponseDto deleteSale(Long id) {
        // Looks for a Sale based on the give "id"
        Sale existingSale = saleRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Sale not found!\n" +
                "The given id:" + id + ", is not related to an existing Sale!"));

        saleRepository.delete(existingSale);

        return null;
    }
}
