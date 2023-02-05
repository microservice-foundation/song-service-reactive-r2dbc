package com.epam.training.microservicefoundation.songservice.api;

import com.epam.training.microservicefoundation.songservice.model.SongMetadata;
import com.epam.training.microservicefoundation.songservice.model.SongRecord;
import com.epam.training.microservicefoundation.songservice.service.SongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/songs")
public class SongController {
    private final Logger log = LoggerFactory.getLogger(SongController.class);

    private final SongService service;
    @Autowired
    public SongController(SongService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SongRecord save(@RequestBody SongMetadata songMetadata) {
        log.info("Saving a song metadata '{}'", songMetadata);
        return service.save(songMetadata);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SongMetadata get(@PathVariable long id) {
        log.info("Getting a song metadata by id '{}'", id);
        return service.getById(id);
    }

    @DeleteMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<SongRecord> delete(@RequestParam(value = "id") long[] ids) {
        log.info("Deleting song metadata(s) with id(s) '{}'", ids);
        return service.deleteByIds(ids);
    }

    @DeleteMapping("delete-by-resource-id")
    @ResponseStatus(value = HttpStatus.OK)
    public List<SongRecord> deleteByResourceId(@RequestParam(value = "id") long[] ids) {
        log.info("Deleting song metadata(s) with resource id(s) '{}'", ids);
        return service.deleteByResourceIds(ids);
    }
}
