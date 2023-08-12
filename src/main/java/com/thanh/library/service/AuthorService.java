package com.thanh.library.service;

import com.thanh.library.domain.Author;
import com.thanh.library.domain.Authority;
import com.thanh.library.domain.User;
import com.thanh.library.repository.AuthorRepository;
import com.thanh.library.repository.UserRepository;
import com.thanh.library.security.SecurityUtils;
import com.thanh.library.service.dto.AuthorDTO;
import com.thanh.library.service.dto.response.ResponseMessageDTO;
import com.thanh.library.service.mapper.AuthorMapper;
import com.thanh.library.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Author}.
 */
@Service
@Transactional
public class AuthorService {

    private final Logger log = LoggerFactory.getLogger(AuthorService.class);

    private final AuthorRepository authorRepository;

    private final UserRepository userRepository;

    private final AuthorMapper authorMapper;

    public AuthorService(AuthorRepository authorRepository, UserRepository userRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.userRepository = userRepository;
        this.authorMapper = authorMapper;
    }

    public AuthorDTO save(AuthorDTO authorDTO) {
        log.debug("Request to save Author : {}", authorDTO);
        Author author = authorMapper.toEntity(authorDTO);
        author = authorRepository.save(author);
        return authorMapper.toDto(author);
    }

    public AuthorDTO update(AuthorDTO authorDTO) {
        log.debug("Request to update Author : {}", authorDTO);
        Author author = authorMapper.toEntity(authorDTO);
        author = authorRepository.save(author);
        return authorMapper.toDto(author);
    }

    public Optional<AuthorDTO> partialUpdate(AuthorDTO authorDTO) {
        log.debug("Request to partially update Author : {}", authorDTO);

        return authorRepository
            .findById(authorDTO.getId())
            .map(existingAuthor -> {
                authorMapper.partialUpdate(existingAuthor, authorDTO);

                return existingAuthor;
            })
            .map(authorRepository::save)
            .map(authorMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<AuthorDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Authors");
        String login = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Login not found", "User", "loginnotfound"));
        User user = userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("User not found", "User", "usernotfound"));
        Authority roleLibrarian = new Authority();
        roleLibrarian.setName("ROLE_LIBRARIAN");
        if (user.getAuthorities().contains(roleLibrarian)) {
            return authorRepository.findAll(pageable).map(authorMapper::toDto);
        }
        return authorRepository.findAllAvailable(pageable).map(authorMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<AuthorDTO> findOne(Long id) {
        log.debug("Request to get Author : {}", id);
        return authorRepository.findById(id).map(authorMapper::toDto);
    }

    public ResponseMessageDTO delete(Long id) {
        log.debug("Request to delete Author : {}", id);
        String login = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Login not found", "User", "loginnotfound"));
        User user = userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("User not found", "User", "usernotfound"));
        Authority roleLibrarian = new Authority();
        roleLibrarian.setName("ROLE_LIBRARIAN");
        if (!user.getAuthorities().contains(roleLibrarian)) {
            throw new BadRequestAlertException("You does not have permission to do this action", "User", "");
        }
        Author author = authorRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Author not found", "Author", "authornotfound"));
        authorRepository.save(author);
        return new ResponseMessageDTO(200, "Author deleted successfully!", Instant.now());
    }
}
