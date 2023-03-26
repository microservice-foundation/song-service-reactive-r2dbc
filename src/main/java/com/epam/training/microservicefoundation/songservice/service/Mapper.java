package com.epam.training.microservicefoundation.songservice.service;

public interface Mapper<Entity, Record> {

  Record mapToRecord(Entity entity);

  Entity mapToEntity(Record record);
}
