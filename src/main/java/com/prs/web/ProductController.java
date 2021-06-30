package com.prs.web;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.prs.business.Product;
import com.prs.db.ProductRepo;

@CrossOrigin
@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductRepo productRepo;

	@GetMapping("/")
	public Iterable<Product> getAll() {
		return productRepo.findAll();
	}

	@GetMapping("/{id}")
	public Optional<Product> get(@PathVariable Integer id) {
		return productRepo.findById(id);
	}

	@PostMapping
	public Product add(@RequestBody Product product) {
		return productRepo.save(product);
	}

	@PutMapping
	public Product update(@RequestBody Product product) {
		return productRepo.save(product);
	}

	@DeleteMapping("/{id}")
	public Optional<Product> delete(@PathVariable int id) {
		Optional<Product> product = productRepo.findById(id);
		if (product.isPresent()) {
			try {
				productRepo.deleteById(id);
			} catch (DataIntegrityViolationException dive) {
				System.err.println(dive.getRootCause().getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Foreign Key Constraint Issue - product id: " + id + " is " + "referred to elsewhere");
			} catch (Exception e) {
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Exception caught during product delete.");
			}
		} else {
			System.err.println("Product delete error");
		}
		return product;
	}

}
