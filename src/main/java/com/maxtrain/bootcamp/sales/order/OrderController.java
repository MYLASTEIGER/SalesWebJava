package com.maxtrain.bootcamp.sales.order;
//controller page includes code required for getting(also by id), posting, putting, & deleting
//within the database.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//CrossOrigin allows another target to talk to it
@CrossOrigin
//RestController will send and receive Json Data
@RestController
@RequestMapping("/api/orders")
//every model must be unique with the name and url
public class OrderController {
	@Autowired
	private OrderRepository ordRepo;
	//gets all data
	@GetMapping
	public ResponseEntity<Iterable<Orders>> GetOrders() {
		var orders = ordRepo.findAll();
														//will return ok response
		return new ResponseEntity<Iterable<Orders>>(orders, HttpStatus.OK);
	}
	//gets by id
	@GetMapping("{id}")
	public ResponseEntity<Orders> getOrder(@PathVariable int id) {
		var order = ordRepo.findById(id);
		if (order.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Orders>(order.get(), HttpStatus.OK);
	}
	@GetMapping("reviews")
	public ResponseEntity<Iterable<Orders>> getOrdersInReview(){
		var orders = ordRepo.findByStatus("Review");
		return new ResponseEntity<Iterable<Orders>>(orders, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Orders> postOrder(@RequestBody Orders order) {
		if (order == null || order.getId() != 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		order.setStatus("New");
		var ord = ordRepo.save(order);
		return new ResponseEntity<Orders>(ord, HttpStatus.CREATED);
	}
	//can use a generic type without a generic entity. reduces hassle factor
	@SuppressWarnings("rawtypes")
	//cannot use the same url (below {id} so must change a little
	@PutMapping("review/{id}")
	public ResponseEntity reviewOrder(@PathVariable int id, @RequestBody Orders order) {
		//set status to review or approved based on the $$
		var statusValue = (order.getTotal() <= 50) ? "Approved" : "Review";
		order.setStatus(statusValue);
		return putOrder(id, order);
	}
	@SuppressWarnings("rawtypes")
	@PutMapping("approve/{id}")
	public ResponseEntity approveOrder(@PathVariable int id, @RequestBody Orders order) {
		order.setStatus("Approved");
		return putOrder(id, order);
	}
	
	@SuppressWarnings("rawtypes")
	@PutMapping("reject/{id}")
	public ResponseEntity rejectOrder(@PathVariable int id, @RequestBody Orders order) {
		order.setStatus("Rejected");
		return putOrder(id, order);
	}
	
	@SuppressWarnings("rawtypes")
	@PutMapping("{id}")
	public ResponseEntity putOrder(@PathVariable int id, @RequestBody Orders order) {
		if (order == null || order.getId() != id) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var ord = ordRepo.findById(order.getId());
		if (ord.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		ordRepo.save(order);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@SuppressWarnings("rawtypes")
	@DeleteMapping
	public ResponseEntity deleteOrder(@PathVariable int id) {
		var order = ordRepo.findById(id);
		if (order.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		ordRepo.delete(order.get());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
