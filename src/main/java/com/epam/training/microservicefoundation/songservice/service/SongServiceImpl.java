package com.epam.training.microservicefoundation.songservice.service;

import com.epam.training.microservicefoundation.songservice.domain.Song;
import com.epam.training.microservicefoundation.songservice.domain.SongNotFoundException;
import com.epam.training.microservicefoundation.songservice.domain.SongRecord;
import com.epam.training.microservicefoundation.songservice.domain.SongRecordId;
import com.epam.training.microservicefoundation.songservice.repository.SongRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SongServiceImpl implements SongService {
    private static final Logger log = LoggerFactory.getLogger(SongServiceImpl.class);
    private final SongRepository repository;
    private final Mapper<Song, SongRecord> songMapper;
    private final Validator<SongRecord> songRecordValidator;
    private final Validator<long[]> idParameterValidator;

    public SongServiceImpl(SongRepository repository, Mapper<Song, SongRecord> songMapper,
                           Validator<SongRecord> songRecordValidator,
                           Validator<long[]> idParameterValidator) {
        this.repository = repository;
        this.songMapper = songMapper;
        this.songRecordValidator = songRecordValidator;
        this.idParameterValidator = idParameterValidator;
    }


    @Transactional
    @Override
    public SongRecordId save(SongRecord songRecord) {
        log.info("Saving a song record '{}'", songRecord);
        if(!songRecordValidator.validate(songRecord)) {
            IllegalArgumentException illegalArgumentException =
                    new IllegalArgumentException(String.format("Saving invalid song record '%s'", songRecord));

            log.error("Saving a song record with invalid value", illegalArgumentException);
            throw illegalArgumentException;
        }
        try {
            Song song = repository.persist(songMapper.mapToEntity(songRecord));
            return new SongRecordId(song.getId());
        } catch (DataIntegrityViolationException ex) {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    String.format("Saving a song record with invalid parameters length or duplicate value '%s'",
                            ex.getLocalizedMessage()), ex);

            log.error("Saving a song record with invalid parameters length or duplicate value", illegalArgumentException);
            throw illegalArgumentException;
        }
    }

    @Transactional
    @Override
    public SongRecord update(SongRecord songRecord) {
        log.info("Updating a song record '{}'", songRecord);
        if(!songRecordValidator.validate(songRecord)) {
            IllegalArgumentException illegalArgumentException =
                    new IllegalArgumentException(String.format("Updating an invalid song record '%s'", songRecord));

            log.error("Updating a song record with invalid value", illegalArgumentException);
            throw illegalArgumentException;
        }

        try {
            Song song = repository.update(songMapper.mapToEntity(songRecord));
            return songMapper.mapToRecord(song);
        } catch (DataIntegrityViolationException ex) {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    String.format("Updating a song record with invalid parameters length or duplicate value '%s'",
                            ex.getLocalizedMessage()), ex);

            log.error("Updating a song record with invalid parameters length or duplicate value",
                    illegalArgumentException);
            throw illegalArgumentException;
        }
    }

    @Transactional
    @Override
    public List<SongRecordId> deleteByIds(long[] ids) {
        log.info("Deleting Song(s) with id {}", ids);
        if(!idParameterValidator.validate(ids)) {
            IllegalArgumentException ex = new IllegalArgumentException("Id param was not validated, check your ids");
            log.error("Id param size '{}' should be less than 200 \nreason:", ids.length, ex);
            throw ex;
        }

        Arrays.stream(ids).mapToObj(repository::getReferenceById).forEach(repository::delete);

        log.debug("Songs with id(s) '{}' were deleted", ids);
        return Arrays.stream(ids).mapToObj(SongRecordId::new).collect(Collectors.toList());
    }

    @Override
    public SongRecord getById(long id) {
        log.info("Getting a song with id '{}'", id);
        Song song = repository.findById(id).orElseThrow(() -> new SongNotFoundException(String.format("Song was not " +
                "found with id '%d'", id)));

        return songMapper.mapToRecord(song);
    }
}
