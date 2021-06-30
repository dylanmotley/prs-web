package com.prs.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.prs.business.LineItem;
import com.prs.business.Request;
import com.prs.db.LineItemRepo;
import com.prs.db.RequestRepo;

@CrossOrigin
@RestController
@RequestMapping("/api/line-items")
public class LineItemController {

	@Autowired
	private LineItemRepo lineItemRepo;
	@Autowired
	private RequestRepo requestRepo;

	@GetMapping("/")
	public Iterable<LineItem> getAll() {
		return lineItemRepo.findAll();
	}

	@GetMapping("/{id}")
	public Optional<LineItem> get(@PathVariable Integer id) {
		return lineItemRepo.findById(id);
	}

	// TR5
	@PostMapping("/")
	public LineItem add(@RequestBody LineItem lineItem) {
		LineItem lit = lineItemRepo.save(lineItem);
		if (recalculateTotal(lineItem.getRequest())) {
			
		}
		else {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
					"Exception caught during lineItem post.");
		}
		return lit;
	}

	// TR6
	@PutMapping("/")
	public LineItem update(@RequestBody LineItem lineItem) {
		LineItem lit = lineItemRepo.save(lineItem);
		if (recalculateTotal(lineItem.getRequest())) {
			
		}
		else {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
					"Exception caught during lineItem put.");
		}
		return lit;
	}

	// TR7
	@DeleteMapping("/{id}")
	public Optional<LineItem> delete(@PathVariable int id) {
		Optional<LineItem> lineItem = lineItemRepo.findById(id);
		if (lineItem.isPresent()) {
			try {
				lineItemRepo.deleteById(id);
				if (!recalculateTotal(lineItem.get().getRequest())) {
					throw new Exception ("Issue recalculating total on delete.");
				}
			} catch (DataIntegrityViolationException dive) {
				System.err.println(dive.getRootCause().getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Foreign Key Constraint Issue - lineItem id: " + id + " is " + "referred to elsewhere");
			} catch (Exception e) {
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Exception caught during lineItem delete.");
			}
		} else {
			System.err.println("LineItem delete error");
		}
		return lineItem;
	}
	
	//Custom Queries
	
	//TR4 List Line Items
	@GetMapping("lines-for-pr/{id}")
	public Iterable<LineItem> getAllByRequest(@PathVariable int id) {
		return lineItemRepo.findAllByRequestId(id);
	}
	
	// Recalculate method for TR5, TR6, TR7
	private boolean recalculateTotal(Request request) {
		boolean success = false;
			try {
				List<LineItem> lits = lineItemRepo.findAllByRequestId(request.getId());
				
				double tot = 0.0;
				for (LineItem lit: lits) {
					tot += (lit.getQuantity() * lit.getProduct().getPrice());
				}
				request.setTotal(tot);
				requestRepo.save(request);
				success = true;
			} catch (Exception e) {
				System.err.println("Error saving new total.");
				e.printStackTrace();
			}
			
			return success;
	}
	

}


