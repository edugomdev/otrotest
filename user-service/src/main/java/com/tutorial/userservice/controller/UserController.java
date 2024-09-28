package com.tutorial.userservice.controller;

import com.tutorial.userservice.entity.User;
import com.tutorial.userservice.model.Bike;
import com.tutorial.userservice.model.Car;
import com.tutorial.userservice.service.UserService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.getAll();
        if(users.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") int id) {
        User user = userService.getUserById(id);
        if(user == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    @PostMapping()
    public ResponseEntity<User> save(@RequestBody User user) {
        User userNew = userService.save(user);
        return ResponseEntity.ok(userNew);
    }
    @CircuitBreaker(name = "carsCB", fallbackMethod = "failBackGetCars")
    @GetMapping("/cars/{userId}")
    public ResponseEntity<List<Car>> getCars(@PathVariable("userId") int userId) {
        User user = userService.getUserById(userId);
        if(user == null)
            return ResponseEntity.notFound().build();
        List<Car> cars = userService.getCars(userId);
        return ResponseEntity.ok(cars);
    }
    
    @CircuitBreaker(name = "carsCB", fallbackMethod = "failBackSaveCar")
    @PostMapping("/savecar/{userId}")
    public ResponseEntity<Car> saveCar(@PathVariable("userId") int userId, @RequestBody Car car) {
        if(userService.getUserById(userId) == null)
            return ResponseEntity.notFound().build();
        Car carNew = userService.saveCar(userId, car);
        return ResponseEntity.ok(car);
    }
    @CircuitBreaker(name = "bikesCB", fallbackMethod = "failBackGetBikes")
    @GetMapping("/bikes/{userId}")
    public ResponseEntity<List<Bike>> getBikes(@PathVariable("userId") int userId) {
        User user = userService.getUserById(userId);
        if(user == null)
            return ResponseEntity.notFound().build();
        List<Bike> bikes = userService.getBikes(userId);
        return ResponseEntity.ok(bikes);
    }


    @CircuitBreaker(name = "bikesCB", fallbackMethod = "failBackSaveBike")
    @PostMapping("/savebike/{userId}")
    public ResponseEntity<Bike> saveBike(@PathVariable("userId") int userId, @RequestBody Bike bike) {
        if(userService.getUserById(userId) == null)
            return ResponseEntity.notFound().build();
        Bike bikeNew = userService.saveBike(userId, bike);
        return ResponseEntity.ok(bike);
    }

    @CircuitBreaker(name = "allCB", fallbackMethod = "failBackGetAll")
    @GetMapping("/getAll/{userId}")
    public ResponseEntity<Map<String, Object>> getAllVehicles(@PathVariable("userId") int userId) {
        Map<String, Object> result = userService.getUserAndVehicles(userId);
        return ResponseEntity.ok(result);
    }
    
    private ResponseEntity<List<Car>> failBackGetCars(@PathVariable("userId") int userId,Throwable throwable){
    	return new ResponseEntity("El usuario "+ userId + " tiene los coches en el taller", HttpStatus.OK);
    }
    private ResponseEntity<Car> failBackSaveCar(@PathVariable("userId") int userId, @RequestBody Car car, Throwable throwable) {
    	return new ResponseEntity("El usuario "+ userId + " no tinee dinero para coches", HttpStatus.OK);
        
    }
    
    private ResponseEntity<List<Bike>> failBackGetBikes(@PathVariable("userId") int userId, Throwable throwable) {
    	return new ResponseEntity("El usuario "+ userId + " tiene los motos en el taller", HttpStatus.OK);
  
    }
    	public ResponseEntity<Bike> failBackSaveBike(@PathVariable("userId") int userId, @RequestBody Bike bike, Throwable throwable) {
    	 	return new ResponseEntity("El usuario "+ userId + " no tiene dinero para motos", HttpStatus.OK);
            
        } 
    	
    	
    private ResponseEntity<Map<String, Object>> failBackGetAll(@PathVariable("userId") int userId, Throwable throwable) {
    	   return new ResponseEntity("NO TIENE DE NA", HttpStatus.OK);
    }
}
