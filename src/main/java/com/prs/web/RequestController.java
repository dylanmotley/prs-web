package com.prs.web;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.prs.business.Request;
import com.prs.db.RequestRepo;

@CrossOrigin
@RestController
@RequestMapping("/api/requests")
public class RequestController {

	@Autowired
	private RequestRepo requestrepo;

	@GetMapping("/")
	public Iterable<Request> getAll() {
		return requestrepo.findAll();
	}
	
	//TR9 Incomplete
	@GetMapping("/list-review/{status}")
	public Iterable<Request> getAllByStatus(@PathVariable String status) {
		return requestrepo.findAllByStatus(status);
	}

	@GetMapping("/{id}")
	public Optional<Request> get(@PathVariable Integer id) {
		return requestrepo.findById(id);
	}

	//TR3 New and Submitted Date
	@PostMapping
	public Request addNewStatus(@RequestBody Request request) {
		request.setStatus("New");
		LocalDateTime currentDateTime = LocalDateTime.now();
		request.setSubmittedDate(currentDateTime.toLocalDate());
		return requestrepo.save(request);
	}
	
	// TR13 Approve
	@PostMapping("/approve")
	public Request addApprovedStatus(@RequestBody Request request) {
		request.setStatus("Approved");
		return requestrepo.save(request);
	}
	
	//TR14 Reject
	@PostMapping("/reject")
	public Request addRejectedStatus(@RequestBody Request request) {
		request.getReasonForRejection();
		request.setStatus("Rejected");
		return requestrepo.save(request);
	}

	@PutMapping
	public Request update(@RequestBody Request request) {
		return requestrepo.save(request);
	}
	
	//TR8 Approved or Review
	@PutMapping("/submit-review")
	public Request updateToReview(@RequestBody Request request) {
		if (request.getTotal() <= 50.00) {
			request.setStatus("Approved");
		} else {
			request.setStatus("Review");
		}
		return requestrepo.save(request);
	}

	@DeleteMapping("/{id}")
	public Optional<Request> delete(@PathVariable int id) {
		Optional<Request> request = requestrepo.findById(id);
		if (request.isPresent()) {
			try {
				requestrepo.deleteById(id);
			} catch (DataIntegrityViolationException dive) {
				System.err.println(dive.getRootCause().getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Foreign Key Constraint Issue - request id: " + id + " is " + "referred to elsewhere");
			} catch (Exception e) {
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Exception caught during request delete.");
			}
		} else {
			System.err.println("Request delete error");
		}
		return request;
	}

}

