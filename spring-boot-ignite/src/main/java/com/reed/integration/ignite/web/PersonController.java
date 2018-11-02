package com.reed.integration.ignite.web;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.cache.Cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.reed.integration.ignite.model.Person;
import com.reed.integration.ignite.repository.PersonRepository;

@RestController
@RequestMapping("/person")
public class PersonController {

	@Autowired
	private PersonRepository personRepository;

	@RequestMapping(value = "/init", method = RequestMethod.GET)
	public ResponseEntity<ResponseEntity<String>> initData() {
		TreeMap<Long, Person> persons = new TreeMap<>();
		persons.put(1L, new Person(1L, "Smith", true, new Date()));
		persons.put(2L, new Person(2L, "a", true, new Date()));
		persons.put(3L, new Person(3L, "Tomson", false, new Date()));
		persons.put(4L, new Person(4L, "Smith", true, new Date()));
		persons.put(5L, new Person(5L, "a", true, new Date()));
		persons.put(6L, new Person(6L, "Won", true, new Date()));
		persons.put(7L, new Person(7L, "Adis", false, new Date()));
		persons.put(8L, new Person(8L, "a", true, new Date()));
		// Adding data into the repository.
		personRepository.save(persons);
		return ResponseEntity.ok(new ResponseEntity<String>("OK", HttpStatus.OK));
	}

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public ResponseEntity<ResponseEntity<Person>> getByName(String name) {
		Person p = personRepository.findByName(name);

		return ResponseEntity.ok(new ResponseEntity<Person>(p, HttpStatus.OK));
	}

	@RequestMapping(value = "/top", method = RequestMethod.GET)
	public ResponseEntity<ResponseEntity<Person>> getTopByName(String name) {
		Cache.Entry<Long, Person> p = personRepository.findTopByNameLike(name);

		return ResponseEntity.ok(new ResponseEntity<Person>(p.getValue(), HttpStatus.OK));
	}

	@RequestMapping(value = "/list")
	public ResponseEntity<ResponseEntity<Page<Person>>> getByPage(
			@PageableDefault(size = 2, sort = { "id" }, direction = Direction.ASC) Pageable pageable) {

		List<Person> list = personRepository.findAllByPage(pageable);
		Page<Person> page = new PageImpl<>(list, pageable, pageable.getPageSize());
		ResponseEntity<Page<Person>> r = new ResponseEntity<>(page, HttpStatus.OK);
		return ResponseEntity.ok(r);

	}

	@RequestMapping(value = "/male")
	public ResponseEntity<ResponseEntity<Page<Person>>> getByMale(Boolean male,
			@PageableDefault(size = 2, sort = { "id" }, direction = Direction.ASC) Pageable pageable) {

		Page<Person> list = personRepository.findByMale(male,pageable);
		//Page<Person> page = new PageImpl<>(list, pageable, pageable.getPageSize());
		ResponseEntity<Page<Person>> r = new ResponseEntity<>(list, HttpStatus.OK);
		return ResponseEntity.ok(r);

	}

	@RequestMapping(value = "/ids")
	public ResponseEntity<ResponseEntity<List<Long>>> getIds(Boolean male,
			@PageableDefault(size = 2, sort = { "id" }, direction = Direction.ASC) Pageable pageable) {

		List<Long> list = personRepository.getIdsByMale(male);
		ResponseEntity<List<Long>> r = new ResponseEntity<>(list, HttpStatus.OK);
		return ResponseEntity.ok(r);

	}
}
