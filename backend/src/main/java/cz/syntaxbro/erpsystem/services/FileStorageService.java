package cz.syntaxbro.erpsystem.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String username);
}
